package Expense;

import java.time.LocalDateTime;

public class Expense {
    private String description;
    private double amount;
    private String category;
    private LocalDateTime timestamp;

    public Expense(String description, double amount, String category, LocalDateTime timestamp){
        setDescription(description);
        setAmount(amount);
        setCategory(category);
        setTimestamp(timestamp);

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
}
