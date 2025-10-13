package Service;

import Config.Config;
import Config.ConfigManager;
import Expense.DailyExpense;
import Expense.Expense;
import Expense.MonthlySummary;
import Expense.TempExpenseStore;
import Expense.CategorySlice;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;

/**
 * AppContext
 * เป็นตัวจัดการข้อมูลและสถานะต่างๆ และมีการเชื่อมกับ Service อืนๆ
 */
public class AppContext {

    private final StorageService storage;
    private final ConfigManager configManager;
    private final TempExpenseStore tempStore;
    private final CustomExport customExport;

    private Config config;
    private final DailyExpense dailyExpense = new DailyExpense();
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * โหลดข้อมูลทั้งหมดจากFileในFolder storage และเตรียมให้พร้อมใช้งาน
     */
    public AppContext() throws IOException {
        this.storage = new StorageService();
        this.configManager = new ConfigManager();

        // เตรียมFolder/Fileให้พร้อมก่อน
        this.storage.initAll();

        // สร้าง TempExpenseStore หลังจาก storage พร้อมแล้ว
        this.tempStore = new TempExpenseStore(storage);
        this.customExport = new CustomExport(this.storage);

        // โหลด config และ temp
        this.config = configManager.load();
        dailyExpense.setExpenses(tempStore.readToday());

        // ตรวจวันใหม่
        rolloverIfNewDay();
    }

    // ---------- รายจ่ายวันนี้ ----------
    /** คืนค่ารายการรายจ่ายทั้งหมดของวันนี้ */
    public List<Expense> getTodayExpenses() {
        return dailyExpense.getExpenses();
    }

    /**
     * เพิ่มรายจ่ายใหม่ → เขียน temp → หัก balance → แจ้ง UI reload
     */
    public void addExpense(String desc, double amount, String category) throws IOException {
        Expense e = new Expense(desc.trim(), category.trim(), amount, java.time.LocalDateTime.now().toString());
        dailyExpense.addExpense(e);
        tempStore.writeAllToday(dailyExpense.getExpenses());
        config.setBalance(config.getBalance() - amount);
        configManager.save(config);
        pcs.firePropertyChange("reload", null, null);
    }

    /**
     * ลบข้อมูลตาม index → เขียน temp → คืนเงินกลับ balance → แจ้ง UI reload
     */
    public void removeExpense(int index) throws IOException {
        List<Expense> before = dailyExpense.getExpenses();
        if (index >= 0 && index < before.size()) {
            Expense e = before.get(index);
            dailyExpense.removeAt(index);
            tempStore.writeAllToday(dailyExpense.getExpenses());
            config.setBalance(config.getBalance() + e.getAmount());
            configManager.save(config);
            pcs.firePropertyChange("reload", null, null);
        }
    }

    /** ยอดที่ใช้ไปวันนี้ */
    public double getSpentToday() {
        return dailyExpense.getSpent();
    }

    // ---------- จัดการ rollover วันใหม่ ----------
    /**
     * เปลี่ยนวัน:
     * - นำรายการ Temp ของวันก่อนหน้าไปเพิ่มใน log เดือนนั้น + อัปเดต summary
     * - reset Temp
     * - เซฟ last_date ใหม่
     * - แจ้ง UI reload
     */
    public void rolloverIfNewDay() throws IOException {
        LocalDate lastDate = config.getLastDate();
        LocalDate today = LocalDate.now();
        if (!lastDate.equals(today)) {
            double remainingEnd = config.getBalance();
            tempStore.appendDailyToMonthlyLog(lastDate, remainingEnd);
            dailyExpense.clear();
            tempStore.resetToday();
            config.setLastDate(today);
            configManager.save(config);
            pcs.firePropertyChange("reload", null, null);
        }
    }

    // ---------- Balance ----------
    /** คืนค่ายอดคงเหลือปัจจุบัน */
    public double getBalance() {
        return config.getBalance();
    }

    /** เพิ่มรายรับ เข้าBalance */
    public void addIncome(double amount) throws IOException {
        if (amount <= 0)
            throw new IllegalArgumentException("amount must be > 0");
        config.setBalance(config.getBalance() + amount);
        configManager.save(config);
        pcs.firePropertyChange("reload", null, null);
    }

    /** ลบBalance */
    public void removeIncome(double amount) throws IOException {
        if (amount <= 0)
            throw new IllegalArgumentException("amount must be > 0");
        config.setBalance(config.getBalance() - amount);
        configManager.save(config);
        pcs.firePropertyChange("reload", null, null);
    }

    // ---------- หมวดหมู่ ----------
    /** รายชื่อหมวดหมู่ */
    public List<String> getCategories() {
        return config.getCategories();
    }

    /** เพิ่มหมวดหมู่ (ห้ามค่าว่าง/มี comma) */
    public void addCategory(String cat) throws IOException {
        String trimmed = cat.trim();
        if (trimmed.isEmpty() || trimmed.contains(",")) {
            throw new IllegalArgumentException("Invalid category name");
        }
        config.addCategory(trimmed);
        configManager.save(config);
        pcs.firePropertyChange("UpdateCatList", null, null);
    }

    /** ลบหมวดหมู่ */
    public void removeCategory(String cat) throws IOException {
        config.removeCategory(cat);
        configManager.save(config);
        pcs.firePropertyChange("UpdateCatList", null, null);
    }

    // ---------- Summary รายเดือน (อ่าน log + รวม Temp ถ้าเป็นเดือนปัจจุบัน) ----------
    /**
     * ดึง summary ของเดือน:
     * - อ่านจากไฟล์รายเดือน
     * - ถ้าเป็นเดือนปัจจุบัน → รวม TodayTemp เพิ่มเข้าไป
     */
    public MonthlySummary getMonthlySummary(YearMonth month) throws IOException {
        if (month == null) {
            throw new IllegalArgumentException("month must not be null");
        }

        // อ่านไฟล์ log ของเดือนนั้น
        List<String> lines = storage.readMonthlyLogLines(month.atDay(1));

        int totalTransactions = 0;
        double totalSpent = 0.0;
        double remainingEnd = 0.0;

        for (String line : lines) {
            if (line == null)
                continue;
            String t = line.trim();
            if (t.startsWith("# summary,")) {
                String[] parts = t.split(",", -1);
                if (parts.length >= 4) {
                    totalTransactions += Util.CsvUtils.parseIntOrZero(parts[1]);
                    totalSpent += Util.CsvUtils.parseDoubleOrZero(parts[2]);
                    remainingEnd = Util.CsvUtils.parseDoubleOrZero(parts[3]); // ใช้ค่าล่าสุดเป็น remainingEnd
                }
            }
        }

        // ถ้าเป็นเดือนปัจจุบัน ก็ให้รวมค่าจาก Temp เพิ่มด้วย
        YearMonth current = YearMonth.now();
        if (month.equals(current)) {
            List<Expense> todayItems = tempStore.readToday();
            int todayCount = todayItems.size();
            double todaySpent = 0.0;
            for (Expense e : todayItems) {
                todaySpent += e.getAmount();
            }

            totalTransactions += todayCount;
            totalSpent += todaySpent;
            remainingEnd = getBalance(); // ใช้ balance ปัจจุบันแทน remaining_end ของวันนี้
        }

        return new MonthlySummary(totalTransactions, totalSpent, remainingEnd);
    }

    // ---------- Export ----------
    /** Export Today ไปที่ ./File/Export/<filename>.csv (เขียนโดย CustomExport) */
    public void exportCustom(String filename) throws IOException {
        if (filename == null) {
            throw new IllegalArgumentException("filename must not be null");
        }
        String safe = filename.trim();
        if (safe.isEmpty()) {
            throw new IllegalArgumentException("filename must not be empty");
        }

        // ดึงรายการวันนี้จาก Temp/DailyExpense
        List<Expense> items = getTodayExpenses();
        if (items == null || items.isEmpty()) {
            throw new IllegalStateException("No data to export");
        }

        double remainingEnd = getBalance();
        customExport.exportCSV(items, remainingEnd, safe);
    }

    // ---------- รวมยอดตามหมวด (วันนี้) ----------
    /** รวมยอดใช้จ่ายวันนี้ตามหมวดหมู่เป็น Map */
    public Map<String, Double> getTodaySpendByCategory() {
        Map<String, Double> map = new LinkedHashMap<>();
        for (Expense e : getTodayExpenses()) {
            String cat = e.getCategory() == null ? "" : e.getCategory().trim();
            double amt = e.getAmount();
            map.put(cat, map.getOrDefault(cat, 0.0) + amt);
        }
        return map;
    }

    /** ลิสต์พร้อมเปอร์เซ็นต์ (เหมาะกับกราฟ) ของ "วันนี้" */
    public List<CategorySlice> getTodayCategorySlices() {
        Map<String, Double> totals = getTodaySpendByCategory();
        double totalSpent = 0.0;
        for (double v : totals.values()) totalSpent += v;

        List<CategorySlice> out = new ArrayList<>();
        if (totalSpent <= 0.0) return out;

        for (Map.Entry<String, Double> e : totals.entrySet()) {
            double pct = (e.getValue() * 100.0) / totalSpent;
            out.add(new CategorySlice(e.getKey(), e.getValue(), pct));
        }
        return out;
    }

    // ---------- รวมยอดตามหมวด (รายเดือน) ----------
    /** รวมยอดใช้จ่ายรายเดือนตามหมวดหมู่ (อ่าน Logs + รวม Temp ถ้าเป็นเดือนปัจจุบัน) */
    public Map<String, Double> getMonthlySpendByCategory(YearMonth month) throws IOException {
        if (month == null) throw new IllegalArgumentException("month must not be null");

        Map<String, Double> totals = new LinkedHashMap<>();

        // 1) อ่านจากไฟล์ log ของเดือนที่เลือก
        List<String> lines = storage.readMonthlyLogLines(month.atDay(1));
        for (String raw : lines) {
            if (raw == null) continue;
            String line = raw.trim();
            if (line.isEmpty()) continue;
            if (line.startsWith("#")) continue; // ข้าม summary/header
            if (line.equalsIgnoreCase("description,category,amount,date")) continue;

            String[] parts = line.split(",", -1);
            if (parts.length >= 3) {
                String category = Util.CsvUtils.trimOrEmpty(parts[1]);
                double amount   = Util.CsvUtils.parseDoubleOrZero(parts[2]);
                totals.put(category, totals.getOrDefault(category, 0.0) + amount);
            }
        }

        // 2) ถ้าเป็นเดือนปัจจุบัน → รวมรายการของวันนี้เข้าไปด้วย
        if (YearMonth.now().equals(month)) {
            for (Expense e : getTodayExpenses()) {
                String cat = e.getCategory() == null ? "" : e.getCategory().trim();
                double amt = e.getAmount();
                totals.put(cat, totals.getOrDefault(cat, 0.0) + amt);
            }
        }

        return totals;
    }

    /** เวอร์ชันพร้อมเปอร์เซ็นต์ (เหมาะกับกราฟ) ของ "รายเดือน" */
    public List<CategorySlice> getMonthlyCategorySlices(YearMonth month) throws IOException {
        Map<String, Double> totals = getMonthlySpendByCategory(month);
        double totalSpent = 0.0;
        for (double v : totals.values()) totalSpent += v;

        List<CategorySlice> out = new ArrayList<>();
        if (totalSpent <= 0.0) return out;

        for (Map.Entry<String, Double> e : totals.entrySet()) {
            double pct = (e.getValue() * 100.0) / totalSpent;
            out.add(new CategorySlice(e.getKey(), e.getValue(), pct));
        }
        return out;
    }

    // ---------- Listener ----------
    public void addListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
}
