package Service;

import Expense.Expense;
import Util.CsvUtils;

import java.util.List;

public class CustomExport {

    /**
     *  สร้าง File CSV จากรายจ่าย กับข้อมูลสรุป
     * 
     *  @param item รายการของtotal expenses
     *  @return ข้อมูลในไฟล์เป็น String
     */

    public static String buildCSV(List<Expense> items, double remainingEnd) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("No data to export");
        }

        int transactions = items.size();
        double totalSpent = 0.0;
        for (Expense e : items) totalSpent += e.getAmount();


        // ส่วนของ summary
        StringBuilder sb = new StringBuilder();
        sb.append("# summary,")
          .append(transactions).append(",")
          .append(CsvUtils.fmt2(totalSpent)).append(",")
          .append(CsvUtils.fmt2(remainingEnd)).append("\n");
        sb.append("# summary,transactions,total_spent,remaining_end\n");
        sb.append("description,category,amount,date\n");

        // รายการ
        for (Expense e : items) {
            sb.append(CsvUtils.escapeCsv(CsvUtils.trimOrEmpty(e.getDescription()))).append(",")
              .append(CsvUtils.escapeCsv(CsvUtils.trimOrEmpty(e.getCategory()))).append(",")
              .append(CsvUtils.fmt2(e.getAmount())).append(",")
              .append(CsvUtils.trimOrEmpty(e.getDate())).append("\n");
        }

        return sb.toString();
    }
}
