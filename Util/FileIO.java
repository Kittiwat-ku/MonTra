package Util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;

public final class FileIO {
    private FileIO() {

    }
    /**
     * เช็คว่ามีFileอยู่แล้วมั้ย ถ้าไม่มีก็สร้างใหม่
     */
    public static void ensureParentDir(Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            if (!Files.exists(parent)) {
                Files.createDirectories(parent);
            }
        }
    }
    /**
     * เขียนข้อความแบบString ลงในFile ปลายทาง
     */
    public static void writeString(Path target, String content) throws IOException {
        ensureParentDir(target);
        Files.writeString(target, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        
    }

    /** 
     * เขียนรายการข้อมูลทีละบรรทัด เป็นList<String> ลงFileปลายทาง
     * โดยที่เรียงลำดับบรรทัดตามList ที่รับเข้ามา
    */
    public static void writeLines(Path target, List<String> lines) throws IOException {
        ensureParentDir(target);
        Files.write(target, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    
    /**
     * อ่านไฟล์ทั้งหมดเป็น List<String> (อ่านทีละบรรทัด)
     * ถ้าไม่มีไฟล์คืน List เปล่า
     */
    public static List<String> readLines(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Path must not be null");
        }
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    /**
     * อ่านไฟล์ทั้งหมดเป็น String เดียว
     * ถ้าไม่มีไฟล์คืน "" (string ว่าง)
     */
    public static String readString(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Path must not be null");
        }
        if (!Files.exists(path)) {
            return "";
        }
        return Files.readString(path, StandardCharsets.UTF_8);
    }
    
}
