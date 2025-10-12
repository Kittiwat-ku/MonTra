package Expense;

import Service.StorageService;
import Util.CsvUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * TempExpenseStore
 * -----------------
 * จัดการไฟล์ Temp (รายการวันนี้) และการอัปเดต Logs รายเดือน
 * รูปแบบไฟล์รายเดือน:
 * # summary,<transactions>,<total_spent>,<remaining_end>
 * # summary,transactions,total_spent,remaining_end
 * description,category,amount,date
 * GotoKU,Travel,70,2025-10-10T20:50:45.701211
 * 
 * จบวันก็ รวมยอด summary เดิม + ยอดของวันนี้ -> เขียน summary ใหม่ แล้ว append
 * รายการวันนี้
 */
public class TempExpenseStore {

    private final StorageService storage;

    /**
     * Constructor: เรียก initAll() เพื่อเตรียมโฟลเดอร์/ไฟล์พื้นฐาน
     */
    public TempExpenseStore(StorageService storage) throws IOException {
        if (storage == null) {
            throw new IllegalArgumentException("storage must not be null");
        }
        this.storage = storage;
        this.storage.initAll();
    }

    // จัดการ Temp วันนี้
    /**
     * ล้าง Temp แล้วเขียน header ใหม่
     */
    public void resetToday() throws IOException {
        storage.clearTempToHeader();
    }

    /**
     * อ่านรายการวันนี้จาก Temp (ข้ามบรรทัดหัวตาราง)
     */
    public List<Expense> readToday() throws IOException {
        List<String> lines = storage.readTempLines();
        List<Expense> out = new ArrayList<>();

        int i = 0;
        while (i < lines.size()) {
            String line = lines.get(i);
            if (i == 0) { // header
                i++;
                continue;
            }
            if (line == null || line.trim().isEmpty()) {
                i++;
                continue;
            }

            String[] a = line.split(",", -1);
            if (a.length >= 4) {
                String description = CsvUtils.trimOrEmpty(a[0]);
                String category = CsvUtils.trimOrEmpty(a[1]);
                double amount = CsvUtils.parseDoubleOrZero(a[2]);
                String dateStr = CsvUtils.trimOrEmpty(a[3]);
                out.add(new Expense(description, category, amount, dateStr));
            }
            i++;
        }
        return out;
    }

    /**
     * เขียนรายการวันนี้ทั้งหมดกลับเข้า Temp (ทับทั้งไฟล์)
     */
    public void writeAllToday(List<Expense> items) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("description,category,amount,date");

        if (items != null) {
            for (Expense e : items) {
                String row = CsvUtils.escapeCsv(CsvUtils.trimOrEmpty(e.getDescription())) + ","
                        + CsvUtils.escapeCsv(CsvUtils.trimOrEmpty(e.getCategory())) + ","
                        + CsvUtils.fmt2(e.getAmount()) + ","
                        + CsvUtils.trimOrEmpty(e.getDate());
                lines.add(row);
            }
        }
        storage.writeTempLines(lines);
    }

    // อัปเดต Logs รายเดือนเมื่อ "ปิดวัน"
    /**
     * เพิ่มรายการของ logDate ลงไฟล์รายเดือน และอัปเดต summary
     * ด้านบนให้เป็นค่าล่าสุดของทั้งเดือน
     * 
     * @param logDate      วันที่ที่จะลง log (เช่น วันที่ของวันก่อนหน้าในช่วง
     *                     rollover)
     * @param remainingEnd ยอดคงเหลือปลายวัน (แนวกระเป๋าเงิน)
     */
    public void appendDailyToMonthlyLog(LocalDate logDate, double remainingEnd) throws IOException {
        List<Expense> items = readToday();
        if (items == null || items.isEmpty()) {
            System.out.println("[TempExpenseStore] No data to append (temp is empty).");
            return;
        }

        // รวมยอดวันนี้
        double totalSpentToday = 0.0;
        for (Expense e : items) {
            totalSpentToday += e.getAmount();
        }
        int transactionsToday = items.size();

        // อ่านไฟล์เดิมของเดือน
        List<String> oldLines = storage.readMonthlyLogLines(logDate);

        // ดึง summary เดิม (ถ้ามันมี)
        int prevTrans = 0;
        double prevSpent = 0.0;
        for (String line : oldLines) {
            if (line == null)
                continue;
            String t = line.trim();
            if (t.startsWith("# summary,")) {
                String[] parts = t.split(",", -1);
                if (parts.length >= 4) {
                    try {
                        prevTrans += Integer.parseInt(parts[1].trim());
                    } catch (NumberFormatException ignore) {
                    }
                    try {
                        prevSpent += Double.parseDouble(parts[2].trim());
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
        }

        int newTransTotal = prevTrans + transactionsToday;
        double newSpentTotal = prevSpent + totalSpentToday;

        // เขียนหัว summary ใหม่ (ทับของเดิม)
        StringBuilder head = new StringBuilder();
        head.append("# summary,")
                .append(newTransTotal).append(",")
                .append(CsvUtils.fmt2(newSpentTotal)).append(",")
                .append(CsvUtils.fmt2(remainingEnd)).append("\n")
                .append("# summary,transactions,total_spent,remaining_end").append("\n");

        // เก็บเนื้อหาเดิมที่ไม่ใช่ summary
        List<String> bodyOld = new ArrayList<>();
        for (String line : oldLines) {
            if (line == null) {
                continue;
            }

            String t = line.trim();
            if (!t.startsWith("# summary")) {
                bodyOld.add(line);
            }

        }

        // ตรวจ header columns
        boolean hasHeader = false;
        for (String l : bodyOld) {
            if (l != null && l.trim().equalsIgnoreCase("description,category,amount,date")) {
                hasHeader = true;
                break;
            }
        }

        // สร้าง body ใหม่: header (ถ้ายังไม่มี) + เนื้อหาเดิม + รายการวันนี้
        StringBuilder body = new StringBuilder();
        if (!hasHeader) {
            body.append("description,category,amount,date").append("\n");
        }

        for (String l : bodyOld) {
            body.append(l).append("\n");
        }

        for (Expense e : items) {
            body.append(CsvUtils.escapeCsv(CsvUtils.trimOrEmpty(e.getDescription()))).append(",")
                    .append(CsvUtils.escapeCsv(CsvUtils.trimOrEmpty(e.getCategory()))).append(",")
                    .append(CsvUtils.fmt2(e.getAmount())).append(",")
                    .append(CsvUtils.trimOrEmpty(e.getDate())).append("\n");
        }

        // เขียนทับทั้งไฟล์: summary ใหม่ + body ใหม่
        String finalContent = head.toString() + body.toString();
        storage.writeMonthlyLogAll(logDate, finalContent);

        System.out.println(
                "[TempExpenseStore] Monthly log updated: " + storage.buildMonthlyLogPath(logDate).toAbsolutePath());
    }
}