package Service;

import java.util.List;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDateTime;

import Expense.CsvManager;
import Expense.DailyExpense;
import Expense.Expense;
import Expense.TempManager;

public class ExpenseService {
    private final CsvManager csvManager;
    private DailyExpense dailyExpense;

    public ExpenseService(CsvManager csvManager, DailyExpense dailyExpense) throws IOException {
        this.csvManager = csvManager;
        this.dailyExpense = dailyExpense;
    }

    public void addExpense(String description, double amount, String category) {
        dailyExpense.addExpense(new Expense(description, amount, category, LocalDateTime.now().toString()));
    }

    public void autoexportToCSV() {
        csvManager.exportToCSV(dailyExpense.getExpenses());
    }

    public void exportToCSV(String filename) {
        csvManager.exportToCSVwithfilename(dailyExpense.getExpenses(), filename);
    }
    public void writetemp(List<Expense> expenses){
        
    }
}
