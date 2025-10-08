package Service;

import java.util.List;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import Expense.CsvManager;
import Expense.DailyExpense;
import Expense.Expense;
import Expense.TempExpenseStore;

public class ExpenseService {
    private final CsvManager csvManager;
    private DailyExpense dailyExpense;
    private TempExpenseStore tempExpenseStore;

    public ExpenseService(CsvManager csvManager, DailyExpense dailyExpense,TempExpenseStore tempExpenseStore) throws IOException {
        this.csvManager = csvManager;
        this.dailyExpense = dailyExpense;
        this.tempExpenseStore = tempExpenseStore;
    }

    public void addExpense(String description, double amount, String category) throws IOException {
        dailyExpense.addExpense(new Expense(description, category, amount, LocalDateTime.now().toString()));
        tempExpenseStore.appendToday(new Expense(description, category, amount, LocalDate.now().toString()));
    }

    public void startWriting(double budget) throws IOException {
        tempExpenseStore.rolloverIfNewDay(budget);
        dailyExpense.setExpenses(tempExpenseStore.readToday());
    }

    public void exportToCSV(String filename) {
        csvManager.exportToCSVwithfilename(dailyExpense.getExpenses(), filename);
    }
    public void removeExpense(int index) throws IOException{
        dailyExpense.removeAt(index);
        tempExpenseStore.writeAllToday(dailyExpense.getExpenses());
        
    }
}
