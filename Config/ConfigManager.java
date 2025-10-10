package Config;

import Util.PropertiesStore;
import Util.CsvUtils;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class ConfigManager {
    private final Path configPath;

    public ConfigManager() {
        this.configPath = Paths.get("./File/Setting/config.txt");
    }

    // โหลดค่า Config จากไฟล์
    public Config load() throws IOException {
        if (!Files.exists(configPath)) {
            LocalDate today = LocalDate.now();
            List<String> defaults = new ArrayList<>();
            defaults.add("Food");
            defaults.add("Travel");
            defaults.add("Education");
            return new Config(0.00, defaults, today);
        }

        Properties p = PropertiesStore.load(configPath);

        // balance
        double balance = CsvUtils.parseDoubleOrZero(p.getProperty("balance"));

        // categories
        String catsRaw = p.getProperty("categories");
        List<String> categories = new ArrayList<>();
        if (catsRaw == null) {
            categories.add("Food");
            categories.add("Travel");
            categories.add("Education");
        } else {
            String catsTrimmed = CsvUtils.trimOrEmpty(catsRaw);
            if (catsTrimmed.isEmpty()) {
                categories.add("Food");
                categories.add("Travel");
                categories.add("Education");
            } else {
                String[] parts = catsTrimmed.split(",");
                int i = 0;
                while (i < parts.length) {
                    String s = parts[i];
                    if (s != null) {
                        String t = s.trim();
                        if (!t.isEmpty()) {
                            if (!categories.contains(t)) {
                                categories.add(t);
                            }
                        }
                    }
                    i = i + 1;
                }
                if (categories.isEmpty()) {
                    categories.add("Food");
                    categories.add("Travel");
                    categories.add("Education");
                }
            }
        }

        // last_date
        LocalDate lastDate = CsvUtils.parseDateOrToday(p.getProperty("last_date"));

        return new Config(balance, categories, lastDate);
    }

    // เซฟ config ลงไฟล์
    public void save(Config cfg) throws IOException {
        Properties p = new Properties();

        String balanceStr = String.format(Locale.US, "%.2f", cfg.getBalance());
        p.setProperty("balance", balanceStr);

        List<String> cats = cfg.getCategories();
        StringBuilder sb = new StringBuilder();
        int idx = 0;
        while (idx < cats.size()) {
            String c = cats.get(idx);
            if (c != null) {
                String t = c.trim();
                if (!t.isEmpty()) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(t);
                }
            }
            idx = idx + 1;
        }
        p.setProperty("categories", sb.toString());

        LocalDate d = cfg.getLastDate();
        if (d == null) {
            d = LocalDate.now();
        }
        p.setProperty("last_date", d.toString());

        PropertiesStore.save(configPath, p, "MonTra config");
    }
}
