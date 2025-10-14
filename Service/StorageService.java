package Service;

import Util.FileIO;
import Util.CsvUtils;
import Util.PropertiesStore;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import Expense.MonthlySummary;

/**
 * StorageService
 * ----------------
 * Classไว้สร้างFolder ใน Folder File
 * - เตรียมFolder (initAll)
 * - อ่าน/เขียน config.txt
 * - อ่าน/เขียน TodayTemp.csv
 * - อ่าน/เขียนไฟล์ Log รายเดือน (Logs/<Year>/<MM>.csv)
 * - สร้าง path และเขียนไฟล์ Export (./File/Export/<filename>.csv)
 */
public class StorageService {

    // โครงสร้างโฟลเดอร์หลัก
    private final Path ROOT_DIR   = Paths.get("./File");
    private final Path CONFIG_DIR = ROOT_DIR.resolve("Setting");
    private final Path TEMP_DIR   = ROOT_DIR.resolve("Temp");
    private final Path LOGS_DIR   = ROOT_DIR.resolve("Logs");
    private final Path EXPORT_DIR = ROOT_DIR.resolve("Export");

    // ไฟล์หลัก
    private final Path CONFIG_FILE = CONFIG_DIR.resolve("config.txt");
    private final Path TEMP_FILE   = TEMP_DIR.resolve("TodayTemp.csv");

    // ---------- เตรียมโครงสร้างพื้นฐาน ----------
    public void initAll() throws IOException {
        createDirIfNotExists(ROOT_DIR);
        createDirIfNotExists(CONFIG_DIR);
        createDirIfNotExists(TEMP_DIR);
        createDirIfNotExists(LOGS_DIR);
        createDirIfNotExists(EXPORT_DIR);

        if (!Files.exists(CONFIG_FILE)) {
            String content = "# Default config\n"
                    + "balance=0.00\n"
                    + "categories=Food,Travel,Education\n"
                    + "last_date=" + LocalDate.now();
            FileIO.writeString(CONFIG_FILE, content);
        }

        if (!Files.exists(TEMP_FILE)) {
            FileIO.writeString(TEMP_FILE, "description,category,amount,date\n");
        }
    }

    private void createDirIfNotExists(Path p) throws IOException {
        if (!Files.exists(p)) {
            Files.createDirectories(p);
        }
    }

    // ---------- Config ----------
    public Properties loadConfig() throws IOException {
        if (!Files.exists(CONFIG_FILE)) {
            initAll();
        }
        return PropertiesStore.load(CONFIG_FILE);
    }

    public void saveConfig(Properties props) throws IOException {
        PropertiesStore.save(CONFIG_FILE, props, "MonTra config");
    }

    public Path getConfigFile() {
        return CONFIG_FILE;
    }

    // ---------- TodayTemp ----------
    public Path getTempFile() {
        return TEMP_FILE;
    }

    public List<String> readTempLines() throws IOException {
        if (!Files.exists(TEMP_FILE)) {
            FileIO.writeString(TEMP_FILE, "description,category,amount,date\n");
        }
        return FileIO.readLines(TEMP_FILE);
    }

    public void writeTempLines(List<String> lines) throws IOException {
        if (lines == null) {
            throw new IllegalArgumentException("lines must not be null");
        }
        FileIO.writeLines(TEMP_FILE, lines);
    }

    public void clearTempToHeader() throws IOException {
        FileIO.writeString(TEMP_FILE, "description,category,amount,date\n");
    }

    public boolean isTempEmpty() throws IOException {
        List<String> lines = readTempLines();
        return lines.size() <= 1;
    }

    // ---------- Monthly Logs (เวอร์ชันปกติ: อาจสร้างโฟลเดอร์ถ้ายังไม่มี) ----------
    /**
     * @return พาธไฟล์รายเดือน: Logs/<YEAR>/<MM>.csv   เช่น Logs/2025/01.csv
     * (เมธอดนี้ "สร้าง" โฟลเดอร์ปีถ้ายังไม่มี)
     */
    public Path buildMonthlyLogPath(LocalDate date) throws IOException {
        if (date == null) {
            throw new IllegalArgumentException("date must not be null");
        }
        String year  = String.valueOf(date.getYear());
        String month = String.format("%02d", date.getMonthValue());

        Path yearDir = LOGS_DIR.resolve(year);
        createDirIfNotExists(yearDir);

        Path file = yearDir.resolve(month + ".csv");
        FileIO.ensureParentDir(file);
        return file;
    }

    public MonthlySummary readMonthlySummary(LocalDate anyDateInMonth) throws IOException {
        List<String> lines = readMonthlyLogLines(anyDateInMonth);
        int tx = 0;
        double spent = 0.0;
        double rem = 0.0;

        for (String line : lines) {
            if (line == null) continue;
            String t = line.trim();
            if (t.startsWith("# summary,")) {
                String[] parts = t.split(",", -1);
                if (parts.length >= 4) {
                    tx = CsvUtils.parseIntOrZero(parts[1]);
                    spent = CsvUtils.parseDoubleOrZero(parts[2]);
                    rem = CsvUtils.parseDoubleOrZero(parts[3]);
                    return new MonthlySummary(tx, spent, rem);
                }
            }
        }
        return MonthlySummary.zero();
    }

    /** อ่านไฟล์ log ของเดือน (ถ้าไม่มี ก็ให้ คืนลิสต์ว่าง) */
    public List<String> readMonthlyLogLines(LocalDate date) throws IOException {
        Path file = buildMonthlyLogPath(date);
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        return Files.readAllLines(file, StandardCharsets.UTF_8);
    }

    /** เขียนทับทั้งไฟล์ log ของเดือน */
    public void writeMonthlyLogAll(LocalDate date, String content) throws IOException {
        if (content == null) {
            throw new IllegalArgumentException("content must not be null");
        }
        Path file = buildMonthlyLogPath(date);
        FileIO.writeString(file, content);
    }

    /** ต่อท้ายไฟล์ log ของเดือน (สร้างใหม่ถ้ายังไม่มี) */
    public void appendMonthlyLog(LocalDate date, String content) throws IOException {
        if (content == null) {
            throw new IllegalArgumentException("content must not be null");
        }
        Path file = buildMonthlyLogPath(date);
        FileIO.ensureParentDir(file);

        Files.writeString(
                file,
                content,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    // ---------- Monthly Logs (เวอร์ชัน NoCreate: ไม่สร้างอะไรตอน "อ่าน") ----------
    /** พาธไฟล์รายเดือนแบบไม่สร้างโฟลเดอร์/ไฟล์ */
    public Path buildMonthlyLogPathNoCreate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("date must not be null");
        }
        String year  = String.valueOf(date.getYear());
        String month = String.format("%02d", date.getMonthValue());
        return LOGS_DIR.resolve(year).resolve(month + ".csv");
    }

    /** อ่านไฟล์ log ของเดือนแบบไม่สร้างโฟลเดอร์/ไฟล์ (ไม่มี -> คืนลิสต์ว่าง) */
    public List<String> readMonthlyLogLinesNoCreate(LocalDate date) throws IOException {
        Path file = buildMonthlyLogPathNoCreate(date);
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        return Files.readAllLines(file, StandardCharsets.UTF_8);
    }

    /** รายชื่อ "ปี" ที่มีอยู่จริงใน ./File/Logs (โฟลเดอร์ชื่อเป็นตัวเลขเท่านั้น) */
    public List<Integer> listExistingLogYears() throws IOException {
        List<Integer> years = new ArrayList<>();
        if (!Files.exists(LOGS_DIR)) return years;

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(LOGS_DIR)) {
            for (Path p : ds) {
                if (Files.isDirectory(p)) {
                    String name = p.getFileName().toString();
                    try {
                        years.add(Integer.parseInt(name));
                    } catch (NumberFormatException ignore) {
                        // ข้ามโฟลเดอร์ที่ไม่ใช่ตัวเลขล้วน
                    }
                }
            }
        }
        years.sort(Integer::compareTo); // เรียงจากน้อยไปมาก (อยากกลับด้านก็ Collections.reverse)
        return years;
    }

    // ---------- Export ----------
    /** คืนพาธไฟล์ export: ./File/Export/<filename>.csv (ถ้าไม่ลงท้าย .csv จะเติมให้) */
    public Path buildExportPath(String filename) throws IOException {
        if (filename == null) {
            throw new IllegalArgumentException("filename must not be null");
        }
        String trimmed = CsvUtils.trimOrEmpty(filename);
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("filename must not be empty");
        }
        if (!trimmed.toLowerCase().endsWith(".csv")) {
            trimmed = trimmed + ".csv";
        }
        Path out = EXPORT_DIR.resolve(trimmed);
        FileIO.ensureParentDir(out);
        return out;
    }

    /** เขียนไฟล์ export ตามพาธที่ให้มา */
    public void writeExport(Path target, String content) throws IOException {
        if (target == null) {
            throw new IllegalArgumentException("target path must not be null");
        }
        if (content == null) {
            throw new IllegalArgumentException("content must not be null");
        }
        FileIO.writeString(target, content);
    }

    // ---------- Getter ----------
    public Path getLogsDir() {
        return LOGS_DIR;
    }

    public Path getExportDir() {
        return EXPORT_DIR;
    }

    public Path getConfigDir() {
        return CONFIG_DIR;
    }
}
