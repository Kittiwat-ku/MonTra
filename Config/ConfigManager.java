package Config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class ConfigManager {
    private final Path configPath;

    public ConfigManager() {
        this.configPath = Paths.get("./File/Setting/config.txt");
    }

    public Config load() throws IOException {
        if (!Files.exists(configPath)) {
            // ค่าเริ่มต้น
            return new Config(0.00, List.of("Food", "Travel", "Education"));
        }
        Properties p = new Properties();
        try (Reader r = Files.newBufferedReader(configPath)) {
            p.load(r);
        }
        double budget = Double.parseDouble(p.getProperty("dailyBudget", "500.00"));
        String cats = p.getProperty("categories", "Food,Travel,Education");
        List<String> categories = new ArrayList<>();
        for (String c : cats.split(",")) {
            String trimmed = c.trim();
            if (!trimmed.isEmpty()) categories.add(trimmed);
        }
        return new Config(budget, categories);
    }

    public void save(Config cfg) throws IOException {
        Properties p = new Properties();
        p.setProperty("dailyBudget", String.format(Locale.US, "%.2f", cfg.getDailyBudget()));
        p.setProperty("categories", String.join(",", cfg.getCategories()));
        Files.createDirectories(configPath.getParent());
        try (Writer w = Files.newBufferedWriter(configPath)) {
            p.store(w, "MonTra config");
        }
    }
}
