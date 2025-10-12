package Service;

import Expense.Expense;
import Util.CsvUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * ใช้สำหรับสร้างและบันทึกไฟล์ CSV สำหรับ Export แบบกำหนดชื่อ
 * - ใช้ StorageService จัดการโฟลเดอร์และเขียนไฟล์จริง
 */
public class CustomExport {

    private final StorageService storage;

    /** Constructor: ต้องส่ง StorageService เข้ามา */
    public CustomExport(StorageService storage) {
        if (storage == null) {
            throw new IllegalArgumentException("StorageService must not be null");
        }
        this.storage = storage;
    }

    /**
     * Export File CSV พร้อมเขียนFileลงใน ./File/Export/<filename>.csv
     *
     * @param items รายการวันนี้ (List<Expense>)
     * @param remainingEnd ยอดคงเหลือปลายวัน
     * @param filename ชื่อไฟล์ (ไม่ต้องมี .csv)
     * @throws IOException เมื่อเขียนไฟล์ไม่ได้
     * @throws IllegalArgumentException เมื่อ input ผิด เช่น filename ว่าง หรือ list เป็น null
     */
    public void exportCSV(List<Expense> items, double remainingEnd, String filename)
            throws IOException {

        // ตรวจสอบ input
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("No data to export");
        }
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename must not be empty");
        }

        String safeName = filename.trim().replaceAll("[\\\\/:*?\"<>|]", "_");
        if (!safeName.toLowerCase().endsWith(".csv")) {
            safeName += ".csv";
        }

        // สร้างเนื้อหา CSV
        String content = buildCSV(items, remainingEnd);

        //ใช้ StorageService สร้าง path และเขียนไฟล์
        Path out = storage.buildExportPath(safeName);
        storage.writeExport(out, content);

        System.out.println("[CustomExport] Export completed → " + out.toAbsolutePath());
    }

    /**
     * สร้างเนื้อหา แล้วreturnให้เอาไปเขียน
     */
    private String buildCSV(List<Expense> items, double remainingEnd) {
        int transactions = items.size();
        double totalSpent = 0.0;
        for (Expense e : items) {
            totalSpent += e.getAmount();
        }

        StringBuilder sb = new StringBuilder();

        // Summary
        sb.append("# summary,")
          .append(transactions).append(",")
          .append(CsvUtils.fmt2(totalSpent)).append(",")
          .append(CsvUtils.fmt2(remainingEnd)).append("\n");
        sb.append("# summary,transactions,total_spent,remaining_end").append("\n");

        // Header
        sb.append("description,category,amount,date").append("\n");

        // Rows
        for (Expense e : items) {
            sb.append(CsvUtils.escapeCsv(CsvUtils.trimOrEmpty(e.getDescription()))).append(",")
              .append(CsvUtils.escapeCsv(CsvUtils.trimOrEmpty(e.getCategory()))).append(",")
              .append(CsvUtils.fmt2(e.getAmount())).append(",")
              .append(CsvUtils.trimOrEmpty(e.getDate())).append("\n");
        }

        return sb.toString();
    }
}
