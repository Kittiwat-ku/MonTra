package Expense;

/** โครงสร้างข้อมูลสำหรับพายชาร์ต (ชื่อหมวด และยอดรวมที่ใช้ไป) */
public final class CategorySlice {
    private final String category;
    private final double amount;

    public CategorySlice(String category, double amount) {
        this.category = category;
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "CategorySlice{category='" + category + "', amount=" + amount + "}";
    }
}
