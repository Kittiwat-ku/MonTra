package Expense;

import java.util.ArrayList;
import java.util.List;

public class DailyExpense {
    List<Expense> expenses = new ArrayList<>();

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public double getRemining(double budget) {
        return budget - getSpent();
    }

    public double getSpent() {
        double total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        return total;
    }

    public void clearExpenses() {
        expenses.clear();
    }

    public void setExpenses(List<Expense> expenses){
        this.expenses = expenses;
    }
}
