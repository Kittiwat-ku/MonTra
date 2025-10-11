package Config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Config {
    private double balance;           // ยอดคงเหลือในกระเป๋า
    private List<String> categories;  // หมวดหมู่
    private LocalDate lastDate;       // วันที่ล่าสุดที่เปิดโปรแกรม

    public Config(double balance, List<String> categories) {
        this(balance, categories, LocalDate.now());
    }

    public Config(double balance, List<String> categories, LocalDate lastDate) {
        this.balance = balance;
        this.categories = new ArrayList<>(categories);
        this.lastDate = lastDate;
    }

    // Getter / Setter 
    public double getBalance() {
        return balance;
    }

    public void setBalance(double v) {
        this.balance = v;
    }

    public List<String> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public void addCategory(String c) {
        if (c != null && !c.isBlank() && !categories.contains(c))
            categories.add(c);
    }

    public void removeCategory(String c) {
        categories.remove(c);
    }

    public LocalDate getLastDate() {
        return lastDate;
    }

    public void setLastDate(LocalDate lastDate) {
        this.lastDate = lastDate;
    }
}
