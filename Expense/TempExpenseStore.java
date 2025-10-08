package Expense;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TempExpenseStore {

    // ---------- Path หลัก ----------
    private static final Path TEMP_DIR = Paths.get("./File/Temp");
    private static final Path TEMP_FILE = TEMP_DIR.resolve("TodayTemp.csv");
    private static final Path DATE_TRACK_FILE = TEMP_DIR.resolve("last_date.txt");
    private static final Path LOGS_DIR = Paths.get("./File/Logs");
    private static final Path EXPORT_DIR = Paths.get("./File/Export");

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ---------- Constructor ----------
    public TempExpenseStore() throws IOException {
        initTempFile();
    }

    // ---------- ตรวจและเตรียม Temp ตอนเริ่มโปรแกรม ----------
    private void initTempFile() throws IOException {
        if (!Files.exists(TEMP_DIR)) Files.createDirectories(TEMP_DIR);
        if (!Files.exists(TEMP_FILE)) resetToday();

        if (!Files.exists(DATE_TRACK_FILE)) {
            Files.writeString(DATE_TRACK_FILE, LocalDate.now().toString(), StandardCharsets.UTF_8);
        }
    }

    // ---------- Reset Temp ----------
    public void resetToday() throws IOException {
        Files.write(TEMP_FILE, List.of("description,category,amount,date"), StandardCharsets.UTF_8);
    }

    // ---------- อ่านข้อมูลใน Temp ----------
    public List<Expense> readToday() throws IOException {
        if (!Files.exists(TEMP_FILE)) resetToday();
        List<String> lines = Files.readAllLines(TEMP_FILE, StandardCharsets.UTF_8);
        List<Expense> out = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) continue;
            String[] a = line.split(",", -1);
            if (a.length < 4) continue;
            out.add(new Expense(
                    a[0].trim(),
                    a[1].trim(),
                    parseDouble(a[2]),
                    a[3].trim()
            ));
        }
        return out;
    }

    // ---------- เขียนทับ Temp ใหม่ทั้งไฟล์ ----------
    public void writeAllToday(List<Expense> items) throws IOException {
        if (!Files.exists(TEMP_DIR)) Files.createDirectories(TEMP_DIR);

        StringBuilder sb = new StringBuilder();
        sb.append("description,category,amount,date\n");
        for (Expense e : items) {
            sb.append(escape(e.getDescription())).append(",")
              .append(escape(e.getCategory())).append(",")
              .append(toStr(e.getAmount())).append(",")
              .append(e.getDate()).append("\n");
        }

        Path tmp = TEMP_FILE.resolveSibling("TodayTemp.csv.tmp");
        Files.writeString(tmp, sb.toString(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        try {
            Files.move(tmp, TEMP_FILE,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(tmp, TEMP_FILE, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // ---------- Export แบบทั่วไป (ให้ชื่อไฟล์เองได้) ----------
    public void exportCustom(String filename, double budget) throws IOException {
        if (!Files.exists(EXPORT_DIR)) Files.createDirectories(EXPORT_DIR);
        if (!filename.toLowerCase().endsWith(".csv")) filename += ".csv";
        String exportPath = EXPORT_DIR.resolve(filename).toString();
        exportCustomInternal(exportPath, budget);
    }

    // ---------- Export สำหรับ Logs (ใช้วันที่เป็นชื่อไฟล์) ----------
    public void exportTodayToLogs(double budget) throws IOException {
        String lastDateStr = Files.exists(DATE_TRACK_FILE)
                ? Files.readString(DATE_TRACK_FILE, StandardCharsets.UTF_8).trim()
                : "";
        LocalDate lastDate = lastDateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(lastDateStr);
        LocalDate today = LocalDate.now();

        if (lastDate.equals(today)) {
            // System.out.println("ยังเป็นวันเดียวกัน ไม่ต้อง export ซ้ำ");
            return;
        }

        if (!Files.exists(LOGS_DIR)) Files.createDirectories(LOGS_DIR);
        String logPath = LOGS_DIR.resolve(lastDate.toString() + ".csv").toString();
        exportCustomInternal(logPath, budget);

        Files.writeString(DATE_TRACK_FILE, today.toString(), StandardCharsets.UTF_8);
    }

    // ---------- แกนกลางการ Export (ใช้ร่วมกันได้ทั้ง Logs/Export) ----------
    private void exportCustomInternal(String outputPath, double budget) throws IOException {
        List<Expense> items = readToday();
        if (items.isEmpty()) {
            // System.out.println("ไม่มีข้อมูลใน Temp ไม่สามารถ export ได้");
            return;
        }

        Path outFile = Paths.get(outputPath);
        Files.createDirectories(outFile.getParent());

        double remaining = budget;
        double totalSpent = 0.0;

        StringBuilder sb = new StringBuilder();
        sb.append("# summary,budget,transactions,total_spent,remaining_end\n");
        sb.append("description,category,amount,date,remaining\n");

        for (Expense e : items) {
            double amt = e.getAmount();
            totalSpent += amt;
            remaining -= amt;
            sb.append(escape(e.getDescription())).append(",")
              .append(escape(e.getCategory())).append(",")
              .append(toStr(amt)).append(",")
              .append(e.getDate()).append(",")
              .append(toStr(remaining)).append("\n");
        }

        sb.insert(0, "# summary," + toStr(budget) + "," + items.size() + ","
                + toStr(totalSpent) + "," + toStr(remaining) + "\n");

        Files.writeString(outFile, sb.toString(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // System.out.println("Export สำเร็จ → " + outFile.toAbsolutePath());
    }


    // ---------- Helper ----------
    private static String toStr(double v) { return String.format(Locale.US, "%.2f", v); }
    private static double parseDouble(String s) { return s == null || s.isBlank() ? 0 : Double.parseDouble(s.trim()); }
    private static String escape(String s) {
        if (s == null) return "";
        String x = s.replace("\"", "\"\"");
        if (x.contains(",") || x.contains("\n")) return "\"" + x + "\"";
        return x;
    }
}
