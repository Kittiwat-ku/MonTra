package Service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDateTime;

import Config.ConfigManager;
import Expense.DailyExpense;
import Expense.Expense;

public class AppContext {
    private final ConfigManager cfgMgr;
    private final CategoryService categoryService;
    private final DailyExpense dailyExpense;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public AppContext(ConfigManager cfgMgr, CategoryService categoryService,DailyExpense dailyExpense){
        this.cfgMgr = cfgMgr;
        this.categoryService = categoryService;
        this.dailyExpense = dailyExpense;
    }

    public ConfigManager getCfgMgr() { return cfgMgr; }
    public CategoryService getCategoryService() { return categoryService; }
    public DailyExpense getDailyExpense(){return dailyExpense;}

    public void addListener(PropertyChangeListener l){ pcs.addPropertyChangeListener(l); }
    public void removeListener(PropertyChangeListener l){ pcs.removePropertyChangeListener(l); }

    // public void setDailyBudget(double newBudget) throws Exception{
    //     double oldbg = categoryService.getDailyBudget();
    //     categoryService.setDailyBudget(newBudget);
    //     pcs.firePropertyChange("dailyBudget", oldbg, newBudget);
    // }
    public void setDailyBudget(double newBudget) throws Exception{
        categoryService.setDailyBudget(newBudget);
        pcs.firePropertyChange("reload", null, null);
    }
    public void addExpense(String description,Double amount,String category){
        dailyExpense.addExpense(new Expense(description, amount, category, LocalDateTime.now()));
        pcs.firePropertyChange("reload",null,null);
    }
    public double getRemining(){
        return dailyExpense.getRemining(categoryService.getDailyBudget());
    }
}
