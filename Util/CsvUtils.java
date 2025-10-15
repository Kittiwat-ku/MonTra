package Util;

import java.time.LocalDate;
import java.util.Locale;
import Expense.Expense;
import java.util.ArrayList;
import java.util.List;

public final class CsvUtils {
    private CsvUtils() {

    }

    /**
     * ใช้จักการข้อมูลต่างๆที่เกี่ยวข้องกับ
     * - การตัดช่องว่างของ String
     * - Formatตัวเลข เช่น ทศนิยม 2 ตำแหน่ง
     * - การแปลง String เป็น double หรือวันที่
     */
    public static String trimOrEmpty(String s) {
        if (s == null) {
            return "";
        } else {
            return s.trim();
        }
    }

    /**
     * แปลงค่าตัวเลขแบบDouble ให้อยู่ในรูปทศนิยม 2 ตำแหน่ง
     * Ex. 1589.50 เป็น 1,589.50
     */
    public static String fmt2(double v) {
        return String.format(Locale.US, "%.2f", v);
    }

    /**
     * แปลงString เป็นค่า double
     */
    public static double parseDoubleOrZero(String s) {
        if (s == null) {
            return 0.0;
        } else {
            String t = s.trim();
            if (t.isEmpty()) {
                return 0.0;
            } else {
                try {
                    return Double.parseDouble(t);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        }
    }

    /** แปลง String เป็น int ถ้า null หรือแปลงไม่ได้ → 0 */
    public static int parseIntOrZero(String s) {
        if (s == null) {
            return 0;
        }
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** แปลง 1 บรรทัด CSV เป็น Expense */
    public static Expense parseExpenseLine(String line) {
        if (line == null)
            return null;
        String t = line.trim();
        if (t.isEmpty())
            return null;
        if (t.startsWith("#"))
            return null; // ข้าม summary หรือ comment
        if (t.equalsIgnoreCase("description,category,amount,date"))
            return null; // ข้าม header

        String[] parts = t.split(",", -1);
        if (parts.length < 4)
            return null;

        String desc = parts[0].trim();
        String cat = parts[1].trim();
        double amt = parseDoubleOrZero(parts[2]);
        String date = parts[3].trim();

        return new Expense(desc, cat, amt, date);
    }

    /** แปลงหลายบรรทัด CSV เป็น List<Expense> */
    public static List<Expense> parseExpensesFromLines(List<String> lines) {
        List<Expense> result = new ArrayList<>();
        if (lines == null)
            return result;

        for (String line : lines) {
            Expense e = parseExpenseLine(line);
            if (e != null)
                result.add(e);
        }

        return result;
    }

    /**
     * แปลงอักษรพิเศษก่อนบันทึกในไฟล์ CSV
     */
    public static String escapeCsv(String s) {
        if (s == null) {
            return "";
        }

        String x = s.replace("\"", "\"\"");
        if (x.contains(",") || x.contains("\n")) {
            return "\"" + x + "\"";
        }
        return x;
    }

    /**
     * แปลงสตริงเป็น LocalDate
     */
    public static LocalDate parseDateOrToday(String s) {
        if (s == null) {
            return LocalDate.now();
        } else {
            String t = s.trim();
            if (t.isEmpty()) {
                return LocalDate.now();
            } else {
                try {
                    return LocalDate.parse(t);
                } catch (Exception e) {
                    return LocalDate.now();
                }
            }
        }
    }
}
