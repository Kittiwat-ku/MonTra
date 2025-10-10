package Expense;

/**
*  เก็บข้อมูลของ รายการรายจ่ายภายใน1รายการ
*/
public class Expense {
    private String description;
    private double amount;
    private String category;
    private String date;
    
    /**
     *  @param description คำอธิบาย
     *  @param category หมวดหมู่
     *  @param amount จำนวนเงิน
     *  @param date วันที่
     */
    public Expense(String description,String category , double amount, String date) {
        setDescription(description);
        setAmount(amount);
        setCategory(category);
        setDate(date);

    }

    // คืนค่า Description
    public String getDescription() {
        return description;
    }

    // กำหนด Description
    public void setDescription(String description) {
        this.description = description;

    }

    // คืนค่าของจำนวนเงิน
    public double getAmount() {
        return amount;
    }

    // กำหนดจำนวนเงิน
    public void setAmount(double amount) {
        this.amount = amount;

    }

    // คืนค่าของหมวดหมู่
    public String getCategory() {
        return category;
    }

    // กำหนดหมวดหมู่
    public void setCategory(String category) {
        this.category = category;

    }

    // คืนค่าวันที่
    public String getDate() {
        return date;
    }

    // กำหนดวันที่
    public void setDate(String date) {
        this.date = date;
    }
    //     @Override
    //     public String toString() {
    //     return String.format("%s | %s | %.2f | %s", date, category, amount, description);
    // }
}
