package Service;

import Util.CsvUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Expense.Expense;

/**
 * จัดการไฟล์ Temp (รายการวันนี้) และการอัปเดต Logs รายเดือน
 * รูปแบบไฟล์รายเดือน:
 * # summary,<transactions>,<total_spent>,<remaining_end>
 * # summary,transactions,total_spent,remaining_end
 * description,category,amount,date
 * 
 * จบวัน: รวมยอด summary เดิม + ยอดของวันนี้ -> เขียน summary ใหม่ แล้วต่อท้ายรายการวันนี้
 */
public class TempExpenseStore {

    private final StorageService storage;

    public TempExpenseStore(StorageService storage) throws IOException {
        if (storage == null) {
            throw new IllegalArgumentException("storage must not be null");
        }
        this.storage = storage;
        this.storage.initAll();
    }

    /** ล้าง Temp แล้วเขียน header ใหม่ */
    public void resetToday() throws IOException {
        storage.clearTempToHeader();
    }

    /** อ่านรายการวันนี้จาก Temp โดยข้าม header แถวแรก */
    public List<Expense> readToday() throws IOException {
        List<String> lines = storage.readTempLines();

        if (lines.isEmpty()) {
            return new ArrayList<>();
        }

        // ตัด header แถวแรกออกก่อน
        List<String> dataLines = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            dataLines.add(line);
        }

        return CsvUtils.parseExpensesFromLines(dataLines);
    }

    /** เขียนรายการของวันนี้ทั้งหมดลง Temp โดยเขียนทับทั้งไฟล์ */
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

    /** 
     * เพิ่มข้อมูลรายวันลง Log ของเดือนนั้น และอัปเดต summary ด้านบนสุด
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

        // อ่านไฟล์ log ของเดือนนี้
        List<String> oldLines = storage.readMonthlyLogLines(logDate);

        // อ่าน summary เดิม
        int prevTrans = 0;
        double prevSpent = 0.0;
        for (String line : oldLines) {
            if (line == null) {
                continue;
            }
            String t = line.trim();
            if (t.startsWith("# summary,")) {
                String[] parts = t.split(",", -1);
                if (parts.length >= 4) {
                    prevTrans += CsvUtils.parseIntOrZero(parts[1]);
                    prevSpent += CsvUtils.parseDoubleOrZero(parts[2]);
                }
            }
        }

        int newTransTotal = prevTrans + transactionsToday;
        double newSpentTotal = prevSpent + totalSpentToday;

        // สร้าง summary ใหม่
        StringBuilder head = new StringBuilder();
        head.append("# summary,")
            .append(newTransTotal).append(",")
            .append(CsvUtils.fmt2(newSpentTotal)).append(",")
            .append(CsvUtils.fmt2(remainingEnd)).append("\n")
            .append("# summary,transactions,total_spent,remaining_end").append("\n");

        // เก็บเนื้อหาเดิมที่ไม่ใช่ summary
        List<String> bodyOld = new ArrayList<>();
        for (String l : oldLines) {
            if (l == null) {
                continue;
            }
            String t = l.trim();
            if (!t.startsWith("# summary")) {
                bodyOld.add(l);
            }
        }

        // ตรวจว่าไฟล์เดิมมี header หรือยัง
        boolean hasHeader = false;
        for (String l : bodyOld) {
            if (l != null && l.trim().equalsIgnoreCase("description,category,amount,date")) {
                hasHeader = true;
                break;
            }
        }

        // รวมเนื้อหาสุดท้าย: header + เนื้อหาเก่า + รายการใหม่
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

        String finalContent = head.toString() + body.toString();
        storage.writeMonthlyLogAll(logDate, finalContent);

        System.out.println("[TempExpenseStore] Monthly log updated: "+ storage.buildMonthlyLogPath(logDate).toAbsolutePath());
    }
}
