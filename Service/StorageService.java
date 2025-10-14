package Service;

import Util.FileIO;
import Util.CsvUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

/**
 * - เตรียมโฟลเดอร์/ไฟล์พื้นฐาน
 * - TodayTemp: read/write/clear
 * - Monthly Logs: build/read(writeAll)/NoCreate + list ปี/เดือนที่มีจริง
 * - Export: build path + write
 */
public class StorageService {

    private final Path ROOT_DIR   = Paths.get("./File");
    private final Path CONFIG_DIR = ROOT_DIR.resolve("Setting");
    private final Path TEMP_DIR   = ROOT_DIR.resolve("Temp");
    private final Path LOGS_DIR   = ROOT_DIR.resolve("Logs");
    private final Path EXPORT_DIR = ROOT_DIR.resolve("Export");

    private final Path CONFIG_FILE = CONFIG_DIR.resolve("config.txt");
    private final Path TEMP_FILE   = TEMP_DIR.resolve("TodayTemp.csv");

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
        if (!Files.exists(p)) Files.createDirectories(p);
    }

    public List<String> readTempLines() throws IOException {
        if (!Files.exists(TEMP_FILE)) {
            FileIO.writeString(TEMP_FILE, "description,category,amount,date\n");
        }
        return FileIO.readLines(TEMP_FILE);
    }

    public void writeTempLines(List<String> lines) throws IOException {
        if (lines == null) throw new IllegalArgumentException("lines must not be null");
        FileIO.writeLines(TEMP_FILE, lines);
    }

    public void clearTempToHeader() throws IOException {
        FileIO.writeString(TEMP_FILE, "description,category,amount,date\n");
    }
    // Monthly
    public Path buildMonthlyLogPath(LocalDate date) throws IOException {
        if (date == null) throw new IllegalArgumentException("date must not be null");
        String year  = String.valueOf(date.getYear());
        String month = String.format("%02d", date.getMonthValue());
        Path yearDir = LOGS_DIR.resolve(year);
        createDirIfNotExists(yearDir);
        Path file = yearDir.resolve(month + ".csv");
        FileIO.ensureParentDir(file);
        return file;
    }

    // อ่านไฟล์ log ของเดือนถ้าไม่มีไฟล์ให้ลิสต์ว่าง
    public List<String> readMonthlyLogLines(LocalDate date) throws IOException {
        Path file = buildMonthlyLogPath(date);
        if (!Files.exists(file)) return new ArrayList<>();
        return Files.readAllLines(file, StandardCharsets.UTF_8);
    }

    // เขียนทั้งไฟล์ log ของเดือน (ทับ)
    public void writeMonthlyLogAll(LocalDate date, String content) throws IOException {
        if (content == null) throw new IllegalArgumentException("content must not be null");
        Path file = buildMonthlyLogPath(date);
        FileIO.writeString(file, content);
    }

    // Monthly
    public Path buildMonthlyLogPathNoCreate(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("date must not be null");
        String year  = String.valueOf(date.getYear());
        String month = String.format("%02d", date.getMonthValue());
        return LOGS_DIR.resolve(year).resolve(month + ".csv");
    }

    public List<String> readMonthlyLogLinesNoCreate(LocalDate date) throws IOException {
        Path file = buildMonthlyLogPathNoCreate(date);
        if (!Files.exists(file)) return new ArrayList<>();
        return FileIO.readLines(file);
    }
    // ปีที่มีโฟลเดอร์จริง
    public List<Integer> listExistingLogYears() throws IOException {
        List<Integer> years = new ArrayList<>();
        if (!Files.exists(LOGS_DIR)) return years;
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(LOGS_DIR)) {
            for (Path p : ds) {
                if (!Files.isDirectory(p)) continue;
                String name = p.getFileName().toString();
                try { years.add(Integer.parseInt(name)); } catch (NumberFormatException ignore) {}
            }
        }
        years.sort(Integer::compareTo);
        return years;
    }

    // เดือนที่มีไฟล์จริงในปีนั้น
    public List<Integer> listExistingMonths(int year) throws IOException {
        List<Integer> months = new ArrayList<>();
        Path yearDir = LOGS_DIR.resolve(String.valueOf(year));
        if (!Files.exists(yearDir) || !Files.isDirectory(yearDir)) return months;
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(yearDir, "*.csv")) {
            for (Path p : ds) {
                String mm = p.getFileName().toString().replace(".csv", "");
                try {
                    int m = Integer.parseInt(mm);
                    if (m >= 1 && m <= 12) months.add(m);
                } catch (NumberFormatException ignore) {}
            }
        }
        months.sort(Integer::compareTo);
        return months;
    }

    // Export
    public Path buildExportPath(String filename) throws IOException {
        if (filename == null) throw new IllegalArgumentException("filename must not be null");
        String trimmed = CsvUtils.trimOrEmpty(filename);
        if (trimmed.isEmpty()) throw new IllegalArgumentException("filename must not be empty");
        if (!trimmed.toLowerCase().endsWith(".csv")) trimmed = trimmed + ".csv";
        Path out = EXPORT_DIR.resolve(trimmed);
        FileIO.ensureParentDir(out);
        return out;
    }

    public void writeExport(Path target, String content) throws IOException {
        if (target == null) throw new IllegalArgumentException("target path must not be null");
        if (content == null) throw new IllegalArgumentException("content must not be null");
        FileIO.writeString(target, content);
    }
}
