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

    // สร้างโฟลเดอร์และไฟล์พื้นฐาน ถ้ายังไม่มี
    private void initTempFile() throws IOException {
        if (!Files.exists(TEMP_DIR))
            Files.createDirectories(TEMP_DIR);
        if (!Files.exists(TEMP_FILE))
            resetToday();
        if (!Files.exists(DATE_TRACK_FILE)) {
            Files.writeString(DATE_TRACK_FILE, LocalDate.now().toString(), StandardCharsets.UTF_8);
        }
    }

    // อ่านวันที่ล่าสุดจาก last_date.txt (ถ้าไม่มีไฟล์จะสร้างเป็นวันที่ปัจจุบัน)
    private LocalDate getLastDate() throws IOException {
        if (!Files.exists(DATE_TRACK_FILE)) {
            LocalDate today = LocalDate.now();
            Files.writeString(DATE_TRACK_FILE, today.toString(), StandardCharsets.UTF_8);
            return today;
        }
        String s = Files.readString(DATE_TRACK_FILE, StandardCharsets.UTF_8).trim();
        if (s.isEmpty()) {
            LocalDate today = LocalDate.now();
            Files.writeString(DATE_TRACK_FILE, today.toString(), StandardCharsets.UTF_8);
            return today;
        }
        return LocalDate.parse(s);
    }

    // ใช้ตอนเริ่มโปรแกรม: ตรวจว่าวันเปลี่ยนหรือยัง
    // ถ้าวันใหม่ → export log ของวันเก่า + reset temp + update วันที่ใหม่
    // ถ้าวันเดิม → ไม่ทำอะไร
    public void exportTodayToLogs(double budget) throws IOException {
        LocalDate lastDate = getLastDate();
        LocalDate today = LocalDate.now();

        if (!lastDate.equals(today)) {
            System.out.println("[TempExpenseStore] Detected new day: " + today);

            // 1) export log ของวันเก่า
            exportMonthlyLogFor(budget, lastDate);

            // 2) reset temp สำหรับวันใหม่
            resetToday();

            // 3) update last_date.txt เป็นวันนี้
            Files.writeString(DATE_TRACK_FILE, today.toString(), StandardCharsets.UTF_8);

            System.out.println("[TempExpenseStore] Export complete and temp reset for new day.");
        } else {
            System.out.println("[TempExpenseStore] Still the same day: " + today);
        }
    }

    // ล้างข้อมูล Temp และเขียนหัวข้อใหม่
    public void resetToday() throws IOException {
        Files.write(TEMP_FILE, List.of("description,category,amount,date"), StandardCharsets.UTF_8);
    }

    // อ่านข้อมูลจาก Temp แล้วคืนค่าเป็น List<Expense>
    public List<Expense> readToday() throws IOException {
        if (!Files.exists(TEMP_FILE))
            resetToday();
        List<String> lines = Files.readAllLines(TEMP_FILE, StandardCharsets.UTF_8);
        List<Expense> out = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank())
                continue;
            String[] a = line.split(",", -1);
            if (a.length < 4)
                continue;
            out.add(new Expense(
                    a[0].trim(), // description
                    a[1].trim(), // category
                    parseDouble(a[2]), // amount
                    a[3].trim() // date
            ));
        }
        return out;
    }

    // เขียนข้อมูลทั้งหมดกลับเข้า Temp (ใช้ตอนเพิ่ม/ลบ/แก้ไข)
    public void writeAllToday(List<Expense> items) throws IOException {
        if (!Files.exists(TEMP_DIR))
            Files.createDirectories(TEMP_DIR);

        StringBuilder sb = new StringBuilder();
        sb.append("description,category,amount,date\n");
        for (Expense e : items) {
            sb.append(escape(normDesc(e.getDescription()))).append(",")
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

    // Export ข้อมูลแบบกำหนดชื่อไฟล์เอง (เก็บใน ./File/Export)
    public void exportCustom(String filename, double budget) throws IOException {
        if (!Files.exists(EXPORT_DIR))
            Files.createDirectories(EXPORT_DIR);
        if (!filename.toLowerCase().endsWith(".csv"))
            filename += ".csv";
        Path out = EXPORT_DIR.resolve(filename);
        exportCustomInternal(out.toString(), budget);
    }

    // Export Log รายเดือน (สำหรับ exportTodayToLogs ใช้)
    private void exportMonthlyLogFor(double budget, LocalDate logDate) throws IOException {
        Path out = buildMonthlyLogPath(logDate);
        Files.createDirectories(out.getParent());
        exportCustomInternal(out.toString(), budget);
        System.out.println("[TempExpenseStore] Log exported to " + out.toAbsolutePath());
    }

    // สร้าง Path ปลายทางของ Log รายเดือน (File/Logs/ปี/เดือน/วันที่.csv)
    private Path buildMonthlyLogPath(LocalDate date) {
        String year = String.valueOf(date.getYear());
        String month = String.format("%02d", date.getMonthValue());
        return LOGS_DIR.resolve(year).resolve(month).resolve(date.toString() + ".csv");
    }

    // Core export: เขียนไฟล์ CSV จริง
    private void exportCustomInternal(String outputPath, double budget) throws IOException {
        List<Expense> items = readToday();
        if (items.isEmpty()) {
            System.out.println("[TempExpenseStore] No data to export.");
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

            // ตัดช่องว่างหน้า-หลัง description ก่อนเขียนไฟล์
            sb.append(escape(normDesc(e.getDescription()))).append(",")
                    .append(escape(e.getCategory())).append(",")
                    .append(toStr(amt)).append(",")
                    .append(e.getDate()).append(",")
                    .append(toStr(remaining)).append("\n");
        }

        sb.insert(0, "# summary," + toStr(budget) + "," + items.size() + ","
                + toStr(totalSpent) + "," + toStr(remaining) + "\n");

        Files.writeString(outFile, sb.toString(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("[TempExpenseStore] Export successful: " + outFile.toAbsolutePath());
    }

    // ---------- Helper ----------
    private static String toStr(double v) {
        return String.format(Locale.US, "%.2f", v);
    }

    private static double parseDouble(String s) {
        return (s == null || s.isBlank()) ? 0.0 : Double.parseDouble(s.trim());
    }

    private static String escape(String s) {
        if (s == null)
            return "";
        String x = s.replace("\"", "\"\"");
        if (x.contains(",") || x.contains("\n"))
            return "\"" + x + "\"";
        return x;
    }

    // ตัดช่องว่างหน้า-หลัง ถ้า null ให้เป็น "" 
    private static String normDesc(String s) {
        return (s == null) ? "" : s.trim();
    }
}
