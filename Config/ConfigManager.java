
import java.io.*;
import java.util.*;

public class ConfigManager {
    private double dailyBudget;
    private List<String> categories;

    public ConfigManager() { 
        loadConfig();
    }

    private void loadConfig() {
        dailyBudget = 0;
        categories = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("./Config/config.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("dailyBudget=")) {
                    dailyBudget = Double.parseDouble(line.split("=")[1].trim());
                } else if (line.startsWith("categories=")) {
                    String[] cats = line.split("=")[1].split(",");
                    for (String cat : cats) {
                        categories.add(cat.trim());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Config file not found, creating default...");
            saveConfig(); // ถ้าไฟล์ยังไม่มี ให้สร้างใหม่
        }
    }

    public void saveConfig() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("./Config/config.txt"))) {
            bw.write("dailyBudget=" + dailyBudget);
            bw.newLine();
            bw.write("categories=" + String.join(",", categories));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public double getDailyBudget() {
        return dailyBudget;
    }

    public void setDailyBudget(double dailyBudget) {
        this.dailyBudget = dailyBudget;
        saveConfig();
    }

    public List<String> getCategories() {
        return categories;
    }

    public void addCategory(String category) {
        if (!categories.contains(category)) {
            categories.add(category);
            saveConfig();
        }
    }

    public void removeCategory(String category) {
        if (categories.remove(category)) {
            saveConfig();
        }
    }
}
