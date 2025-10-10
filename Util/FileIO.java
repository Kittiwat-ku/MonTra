package Util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
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
}
