package Service;

import java.util.List;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import Expense.DailyExpense;
import Expense.Expense;
import Expense.TempExpenseStore;

public class ExpenseService {
    private DailyExpense dailyExpense;
    private TempExpenseStore tempExpenseStore;

    public ExpenseService(DailyExpense dailyExpense,TempExpenseStore tempExpenseStore) throws IOException {
        this.dailyExpense = dailyExpense;
        this.tempExpenseStore = tempExpenseStore;
    }

    public void addExpense(String description, double amount, String category) throws IOException {
        dailyExpense.addExpense(new Expense(description, category, amount, LocalDateTime.now().toString()));
        tempExpenseStore.writeAllToday(dailyExpense.getExpenses());
    }

    public void startProgram(double budget) throws IOException {
        // tempExpenseStore.rolloverIfNewDay(budget);
        tempExpenseStore.exportTodayToLogs(budget);
        dailyExpense.setExpenses(tempExpenseStore.readToday());
    }

    public void removeExpense(int index) throws IOException{
        dailyExpense.removeAt(index);
        tempExpenseStore.writeAllToday(dailyExpense.getExpenses());
        
    }
}
