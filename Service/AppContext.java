package Service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

import Config.ConfigManager;
import Expense.DailyExpense;

public class AppContext {
    private final ConfigManager cfgMgr;
    private final CategoryService categoryService;
    private final DailyExpense dailyExpense;
    private final ExpenseService expenseService;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public AppContext(ConfigManager cfgMgr, CategoryService categoryService, DailyExpense dailyExpense,
            ExpenseService expenseService) {
        this.cfgMgr = cfgMgr;
        this.categoryService = categoryService;
        this.dailyExpense = dailyExpense;
        this.expenseService = expenseService;
    }

    public ConfigManager getCfgMgr() {
        return cfgMgr;
    }

    public CategoryService getCategoryService() {
        return categoryService;
    }

    public DailyExpense getDailyExpense() {
        return dailyExpense;
    }

    public ExpenseService getExpenseService() {
        return expenseService;
    }

    public void addListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    // public void setDailyBudget(double newBudget) throws Exception{
    // double oldbg = categoryService.getDailyBudget();
    // categoryService.setDailyBudget(newBudget);
    // pcs.firePropertyChange("dailyBudget", oldbg, newBudget);
    // }
    public double getRemining() {
        return dailyExpense.getRemining(categoryService.getDailyBudget());
    }

    // Set new value and refresh state change
    public void setDailyBudget(double newBudget) throws Exception {
        categoryService.setDailyBudget(newBudget);
        pcs.firePropertyChange("reload", null, null);
    }

    public void addExpense(String description, Double amount, String category) {
        expenseService.addExpense(description, amount, category);
        pcs.firePropertyChange("reload", null, null);
    }

    public void addCategory(String name) throws IOException {
        categoryService.add(name);
        pcs.firePropertyChange("UpdateCatList", null, null);
    }

    public void RemoveCat(String name) throws IOException {
        categoryService.remove(name);
        pcs.firePropertyChange("UpdateCatList", null, null);
    }
}
