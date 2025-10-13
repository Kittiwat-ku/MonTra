package Expense;

/** โครงสร้างข้อมูลสำหรับพายชาร์ต (ชื่อหมวด, ยอด, เปอร์เซ็นต์) */
public final class CategorySlice {
    private final String category;
    private final double amount;
    private final double percent;

    public CategorySlice(String category, double amount, double percent) {
        this.category = category;
        this.amount = amount;
        this.percent = percent;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public double getPercent() {
        return percent;
    }

    @Override
    public String toString() {
        return "CategorySlice{category='" + category + "', amount=" + amount + ", percent=" + percent + "}";
    }
}
