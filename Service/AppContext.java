package Service;

import Config.Config;
import Config.ConfigManager;
import Expense.DailyExpense;
import Expense.Expense;
import Expense.TempExpenseStore;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/**
 * เป็นตัวจัดการข้อมูลและสถานะต่างๆ และ
 * มีการเชื่อมกับ Service อืนๆ
 */
public class AppContext {

    private final StorageService storage;
    private final ConfigManager configManager;
    private final TempExpenseStore tempStore;

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
        this.tempStore = new TempExpenseStore(storage);
        this.config = configManager.load();

        // โหลด temp วันนี้เข้ามาใน dailyExpense
        dailyExpense.setExpenses(tempStore.readToday());

        // เช็ควันใหม่
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

    public double getSpentToday() {
        return dailyExpense.getSpent();
    }

    /**  
     * จัดการ rollover วันใหม่
     * -นำรายการ Temp ไปเพิ่มใน log รายเดือน
     * -รีเซ็ต Temp ใหม่
     * -เซฟวันใหม่ลง config
     * -แจ้ง UI ให้ reload
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

    // Balance

    /**
     *  คืนค่ายอดคงเหลือปัจจุบัน
     */
    public double getBalance() {
        return config.getBalance();
    }

    /**
     *  เพิ่มรายรับ เข้าBalance
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
     * @param amount
     * @throws IOException
     */
    public void removeIncome(double amount)throws IOException{
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
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

    // Export ข้อมูลเป็นFile CSV 
    public void exportCustom(String filename) throws IOException {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be empty.");
        }
        // Check Exception
        String safe = filename.trim().replaceAll("[\\\\/:*?\"<>|]", "_");

        List<Expense> items = getTodayExpenses();
        double remainingEnd = getBalance();

        // สร้างเนื้อหา CSV จาก CustomExport
        String content = CustomExport.buildCSV(items, remainingEnd);

        // จัดการ path และเขียนไฟล์สำหรับบันทึกขอมูล
        Path out = storage.buildExportPath(safe);
        storage.writeExport(out, content);

        System.out.println("[AppContext] Exported custom CSV to " + out.toAbsolutePath());
    }

    // Listener 
    public void addListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
}
