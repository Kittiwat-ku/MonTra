package Service;

import Config.Config;
import Config.ConfigManager;
import Expense.DailyExpense;
import Expense.Expense;
import Expense.MonthlySummary;
import Expense.TempExpenseStore;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.time.YearMonth;

/**
 * AppContext
 * /**
 * เป็นตัวจัดการข้อมูลและสถานะต่างๆ และ
 * มีการเชื่อมกับ Service อืนๆ
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
     * โหลดข้อมูลทั้งหมดจากFileในFolder storage
     * และเตรียมให้พร้อมใช้งาน
     */
    public AppContext() throws IOException {
        this.storage = new StorageService();
        this.configManager = new ConfigManager();

        // เตรียมโฟลเดอร์/ไฟล์ให้พร้อมก่อน
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

    // รายจ่ายวันนี้
    // คืนค่ารายการรายจ่ายทั้งหมดของวันนี้
    public List<Expense> getTodayExpenses() {
        return dailyExpense.getExpenses();
    }

    /**
     * เพิ่มรายจ่ายใหม่
     * เพิ่มเข้า DailyExpense
     * บันทึกลง temp ทันที
     * อัปเดตยอดเงินในBalance
     * แจ้ง UI หรือส่วนอื่นให้ reload
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
     * ลบข้อมูลตาม index
     * ลบออกจาก DailyExpense
     * เขียน temp ทับใหม่
     * คืนเงินกลับ balance
     * แจ้ง UI หรือส่วนอื่นให้ reload
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

    /**
     * จัดการ rollover วันใหม่
     * -นำรายการ Temp ไปเพิ่มใน log รายเดือน
     * -รีเซ็ต Temp ใหม่
     * -เซฟวันใหม่ลง config
     * -แจ้ง UI ให้ reload
     */
    public double getSpentToday() {
        return dailyExpense.getSpent();
    }

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

    // Balance
    /**
     * คืนค่ายอดคงเหลือปัจจุบัน
     */
    public double getBalance() {
        return config.getBalance();
    }

    /**
     * เพิ่มรายรับ เข้าBalance
     */
    public void addIncome(double amount) throws IOException {
        if (amount <= 0)
            throw new IllegalArgumentException("amount must be > 0");
        config.setBalance(config.getBalance() + amount);
        configManager.save(config);
        pcs.firePropertyChange("reload", null, null);
    }

    /**
     * ลบBalance
     * 
     * @param amount
     * @throws IOException
     */
    public void removeIncome(double amount) throws IOException {
        if (amount <= 0)
            throw new IllegalArgumentException("amount must be > 0");
        config.setBalance(config.getBalance() - amount);
        configManager.save(config);
        pcs.firePropertyChange("reload", null, null);
    }

    // คืนค่ารายชื่อหมวดหมู่
    public List<String> getCategories() {
        return config.getCategories();
    }

    /**
     * -เพิ่มหมวดหมู่ใหม่
     * -ตรวจไม่ให้มีช่องว่างหรือเครื่องหมาย","
     */
    public void addCategory(String cat) throws IOException {
        String trimmed = cat.trim();
        if (trimmed.isEmpty() || trimmed.contains(",")) {
            throw new IllegalArgumentException("Invalid category name");
        }
        config.addCategory(trimmed);
        configManager.save(config);
        pcs.firePropertyChange("UpdateCatList", null, null);
    }

    public void removeCategory(String cat) throws IOException {
        config.removeCategory(cat);
        configManager.save(config);
        pcs.firePropertyChange("UpdateCatList", null, null);
    }

    /**
     * ดึงข้อมูล summary ของเดือนที่กำหนด
     * - อ่านจากไฟล์รายเดือน
     * - ถ้าเป็นเดือนปัจจุบัน → รวมค่าจาก Temp (TodayTemp.csv) ด้วย
     * 
     * @param month เดือนที่ต้องการ เช่น YearMonth.of(2025, 10)
     * @return MonthlySummary ที่รวมยอดทั้งหมดแล้ว
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

    // Export ข้อมูลเป็นFile CSV
    public void exportCustom(String filename) throws IOException {
        if (filename == null) {
            throw new IllegalArgumentException("filename must not be null");
        }
        String safe = filename.trim();
        if (safe.isEmpty()) {
            throw new IllegalArgumentException("filename must not be empty");
        }

        // ดึงรายการวันนี้จาก Temp
        List<Expense> items = getTodayExpenses();
        if (items == null || items.isEmpty()) {
            throw new IllegalStateException("No data to export");
        }

        // ใช้ยอดคงเหลือปัจจุบันเป็น remaining_end
        double remainingEnd = getBalance();

        // ให้ CustomExport จัดรูป + เขียนFlie
        customExport.exportCSV(items, remainingEnd, safe);
    }

    public void addListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
}
