package Expense;



import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TempExpenseStore {
    private static final Path TEMP_DIR = Paths.get("./File/Temp");
    private static final Path TEMP_FILE = TEMP_DIR.resolve("TodayTemp.csv");
    private static final Path DATE_TRACK_FILE = TEMP_DIR.resolve("last_date.txt");
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TempExpenseStore() throws IOException {
        initTempFile(); //เช็ควัน
    }

    /** ตรวจและสร้าง temp file ใหม่ ถ้าเป็นวันใหม่
     * 
    */
    private void initTempFile() throws IOException {
        if (!Files.exists(TEMP_DIR)) Files.createDirectories(TEMP_DIR);
        LocalDate today = LocalDate.now();

        if (Files.exists(DATE_TRACK_FILE)) {
            String lastDate = Files.readString(DATE_TRACK_FILE, StandardCharsets.UTF_8).trim();
            if (!lastDate.equals(today.toString())) {
                Files.deleteIfExists(TEMP_FILE);
                resetToday();
                Files.writeString(DATE_TRACK_FILE, today.toString(), StandardCharsets.UTF_8);
                System.out.println("ล้าง Temp เก่าสำหรับวันใหม่ " + today);
                return;
            }
        }

        if (!Files.exists(TEMP_FILE)) {
            resetToday();
            Files.writeString(DATE_TRACK_FILE, today.toString(), StandardCharsets.UTF_8);
        }
    }

    /** 
     * สร้างหรือรีเซ็ตไฟล์ TodayTemp.csv 
     * */
    public void resetToday() throws IOException {
        Files.write(TEMP_FILE, List.of("description,category,amount,date"), StandardCharsets.UTF_8);
    }

    /** 
     * อ่านข้อมูลของวันนี้จากไฟล์ TodayTemp.csv 
     * */
    public List<Expense> readToday() throws IOException {
        if (!Files.exists(TEMP_FILE)) resetToday();
        List<String> lines = Files.readAllLines(TEMP_FILE, StandardCharsets.UTF_8);
        List<Expense> out = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) continue;
            String[] a = split(line);
            if (a.length < 4) continue;

            //  description, category, amount, date
            out.add(new Expense(
                    unescape(a[0]),   // description
                    unescape(a[1]),   // category
                    parseDouble(a[2]),// amount
                    a[3].trim()       // date
            ));
        }
        return out;
    }

    /** เพิ่มข้อมูลรายจ่ายใหม่ลงใน TodayTemp.csv */
    public void appendToday(Expense e) throws IOException {
        if (!Files.exists(TEMP_FILE)) resetToday();

        // ฟอร์แมตใหม่: description, category, amount, date
        String row = String.join(",",
                escape(e.getDescription()),
                escape(e.getCategory()),
                toStr(e.getAmount()),
                e.getDate()
        );

        Files.writeString(TEMP_FILE, System.lineSeparator() + row,
                StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }

    // ---------- helper ----------
    private static String toStr(double v) { return String.format(Locale.US, "%.2f", v); }
    private static double parseDouble(String s) { return s == null || s.isBlank() ? 0 : Double.parseDouble(s.trim()); }
    private static String[] split(String line) { return line.split(",", -1); }
    private static String escape(String s) {
        if (s == null) return "";
        String x = s.replace("\"", "\"\"");
        if (x.contains(",") || x.contains("\n")) return "\"" + x + "\"";
        return x;
    }
    private static String unescape(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\""))
            s = s.substring(1, s.length() - 1).replace("\"\"", "\"");
        return s;
    }
}