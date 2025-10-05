package Service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import Config.ConfigManager;

public class AppContext {
    private final ConfigManager cfgMgr;
    private final CategoryService categoryService;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public AppContext(ConfigManager cfgMgr, CategoryService categoryService){
        this.cfgMgr = cfgMgr;
        this.categoryService = categoryService;
    }

    public ConfigManager getCfgMgr() { return cfgMgr; }
    public CategoryService getCategoryService() { return categoryService; }

    public void addListener(PropertyChangeListener l){ pcs.addPropertyChangeListener(l); }
    public void removeListener(PropertyChangeListener l){ pcs.removePropertyChangeListener(l); }

    public void setDailyBudget(double newBudget) throws Exception{
        double oldbg = categoryService.getDailyBudget();
        categoryService.setDailyBudget(newBudget);
        pcs.firePropertyChange("dailyBudget", oldbg, newBudget);
    }
}
