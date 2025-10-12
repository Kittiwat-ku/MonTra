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

/**
 * StorageService
 * ----------------
 * Classไว้สร้างFolder ใน Folder File
 * - เตรียมFolder (initAll)
 * - อ่าน/เขียน config.txt
 * - อ่าน/เขียน TodayTemp.csv
 * - อ่าน/เขียนไฟล์ Log รายเดือน (Logs/ํYear/number of month.csv)
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

    // เตรียมFolder กับFile Config และ Temp
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

    // Config
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

    // TodayTemp
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
        if (lines.size() <= 1) {
            return true;
        } else {
            return false;
        }
    }

    // Monthly Logs
    /**
     * @return ที่อยู่ไฟล์รายเดือน: (Logs/ํYear/number of month.csv).csv  ex: Logs/2025/1.csv
     * 
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

    /** 
     * อ่านไฟล์ log ของเดือน (ถ้ายังไม่มี จะคืนลิสต์ว่าง) 
    */
    public List<String> readMonthlyLogLines(LocalDate date) throws IOException {
        Path file = buildMonthlyLogPath(date);
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        return Files.readAllLines(file, StandardCharsets.UTF_8);
    }

    /** 
     * เขียนข้อมูลทับลง File log ของประจำเดือนทั้งไฟล์
    */
    public void writeMonthlyLogAll(LocalDate date, String content) throws IOException {  
        if (content == null) {
           throw new IllegalArgumentException("content must not be null");
        }
        Path file = buildMonthlyLogPath(date);
        FileIO.writeString(file, content);
    }

    /** 
     * เขียนต่อท้ายข้อมูลลง File log ของเดือน  
     * ถ้าไฟล์ยังไม่ถูกสร้างจะสร้าง File ใหม่โดยอัตโนมัติ
    */
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

    //Export
    /** 
     * สร้างPathไฟล์ export: ./File/Export/<filename>.csv 
     * แล้วก็เช็คด้วยว่าFileมันเติม.csv ด้านหลังยัง ถ้ายังก็เติมให้
     * */
    public Path buildExportPath(String filename) throws IOException {
        if (filename == null) {
            throw new IllegalArgumentException("filename must not be null");
        }
        String trimmed = CsvUtils.trimOrEmpty(filename);
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("filename must not be empty");
        } else {
            if (!trimmed.toLowerCase().endsWith(".csv")) {
                trimmed = trimmed + ".csv";
            }
        }
        Path out = EXPORT_DIR.resolve(trimmed);
        FileIO.ensureParentDir(out);
        return out;
    }

    /** 
     * เขียนไฟล์ export ตามพาธที่ให้มา
    */
    public void writeExport(Path target, String content) throws IOException {
        if (target == null) {
            throw new IllegalArgumentException("target path must not be null");
        }
        if (content == null) {
            throw new IllegalArgumentException("content must not be null");
        }
        FileIO.writeString(target, content);
    }

    // Getter method
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
