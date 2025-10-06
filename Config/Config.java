package Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Config {
    private double dailyBudget;
    private List<String> categories;

    public Config(double dailyBudget, List<String> categories) {
        this.dailyBudget = dailyBudget;
        this.categories = new ArrayList<>(categories);
    }
    public double getDailyBudget() {
        return dailyBudget; 
    }
    public void setDailyBudget(double v) {
        this.dailyBudget = v; 
    }
    public List<String> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public void addCategory(String c) {
        if (c != null && !c.isBlank() && !categories.contains(c)) categories.add(c);
    }

    public void removeCategory(String c) {
        categories.remove(c);
    }
}
