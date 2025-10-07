package Service;

import java.time.LocalDateTime;

import Expense.CsvManager;
import Expense.DailyExpense;
import Expense.Expense;

public class ExpenseService {
    private final CsvManager csvManager;
    private DailyExpense dailyExpense;

    public ExpenseService(CsvManager csvManager, DailyExpense dailyExpense) {
        this.csvManager = csvManager;
        this.dailyExpense = dailyExpense;
    }

    public void addExpense(String description, double amount, String category) {
        dailyExpense.addExpense(new Expense(description, amount, category, LocalDateTime.now()));
    }

    public void autoexportToCSV() {
        csvManager.exportToCSV(dailyExpense.getExpenses());
    }

    public void exportToCSV(String filename) {
        csvManager.exportToCSVwithfilename(dailyExpense.getExpenses(), filename);
    }
}
