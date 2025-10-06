package Service;

import java.io.IOException;
import java.util.List;

import Config.Config;
import Config.ConfigManager;

public class CategoryService {
    private final ConfigManager configManager;
    private Config config;

    public CategoryService(ConfigManager configManager) throws IOException {
        this.configManager = configManager;
        this.config = configManager.load();
    }

    public List<String> list() { return config.getCategories(); }

    public void add(String name) throws IOException {
        config.addCategory(name);
        configManager.save(config);
    }

    public void remove(String name) throws IOException {
        config.removeCategory(name);
        configManager.save(config);
    }

    public void setDailyBudget(double amount) throws IOException {
        config.setDailyBudget(amount);
        configManager.save(config);
    }

    public double getDailyBudget() { 
        return config.getDailyBudget(); 
    }
    public List<String> getCategory(){
        return config.getCategories();
    }
    public void addCategory(String name) throws IOException {
        config.addCategory(name);
        configManager.save(config);
    }
}
