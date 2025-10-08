package Expense;

import java.time.LocalDateTime;

public class Expense {
    private String description;
    private double amount;
    private String category;
    private String date;

    public Expense(String description,String category , double amount, String date) {
        setDescription(description);
        setAmount(amount);
        setCategory(category);
        setDate(date);

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
        @Override
        public String toString() {
        return String.format("%s | %s | %.2f | %s", date, category, amount, description);
    }
}
