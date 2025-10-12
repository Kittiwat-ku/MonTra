package Util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.*;
import java.util.Properties;

public final class PropertiesStore {

    private PropertiesStore() {}

    /**
     * โหลดค่า Properties จากไฟล์ที่กำหนด
     * ใช้ FileIO.readString() อ่านเนื้อหาทั้งไฟล์แล้ว parse ด้วย StringReader
     * 
     * @param path path ของไฟล์ config
     * @return Properties object (ถ้าไฟล์ไม่มีอยู่จะคืน Properties ว่าง)
     * @throws IOException หากอ่านไฟล์ล้มเหลว
     */
    public static Properties load(Path path) throws IOException {
        Properties p = new Properties();

        if (!Files.exists(path)) {
            return p; // ไม่มีไฟล์ก็คืนค่าเปล่า
        }

        String content = FileIO.readString(path); // อ่านทั้งไฟล์เป็น String

        if (content == null || content.trim().isEmpty()) {
            return p; // ถ้าไฟล์ว่าง
        }

        try (StringReader reader = new StringReader(content)) {
            p.load(reader); // แปลงเนื้อหาเป็น Properties
        }

        return p;
    }

    /**
     * บันทึก Properties ลงไฟล์
     * 
     * @param path path ของไฟล์ config
     * @param props Properties ที่จะบันทึก
     * @param comment คำอธิบายที่จะเขียนด้านบนไฟล์
     * @throws IOException หากเขียนไฟล์ล้มเหลว
     */
    public static void save(Path path, Properties props, String comment) throws IOException {
        if (props == null) {
            throw new IllegalArgumentException("Properties cannot be null");
        }

        StringWriter writer = new StringWriter();
        props.store(writer, comment); // เขียน Properties ทั้งหมดลง StringWriter

        String result = writer.toString();
        FileIO.writeString(path, result); // ใช้ FileIO เขียนไฟล์จริง
    }
}
