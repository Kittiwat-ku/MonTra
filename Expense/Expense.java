package Expense;

import java.time.LocalDateTime;
/**
 * 
 */
public class Expense{
    private String description;
    private double amount;
    private String category;
    private LocalDateTime timestamp;
    /**
     * description, timestamp, category ต้องไม่เป็น Blank หรือ Null |
     * amount ต้องน้อยกว่าและไม่เป็น 0.0
     * @throws IllegalArgumentException เมื่อตัวแปลมีค่าเป็น Null หรือ Blank และ amount มีค่าเป็น 0.0
     */
    private void checkREP(){
        if (description == null || category == null || timestamp == null || amount <= 0.0) {
            throw new IllegalArgumentException("Cannot be null");
        }
        if (description.isBlank() ||category.isBlank()) {
            throw new IllegalArgumentException("Cannot be Blank");
        }
    }
    public Expense(String description, double amount, String category, LocalDateTime timestamp){
        setDescription(description);
        setAmount(amount);
        setCategory(category);
        setTimestamp(timestamp);
        checkREP();
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
        
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
        
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
        
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        
    }
    @Override
    public String toString() {
        return timestamp + " | " + description + " | " + amount + " | " + category;
    }

}


