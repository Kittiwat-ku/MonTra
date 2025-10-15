package Expense;

import java.util.List;

/**
 * สรุปรายเดือน:
 * - transactions     จำนวนรายการทั้งหมดในเดือน
 * - totalSpent       ยอดใช้จ่ายรวมในเดือน
 * - remainingEnd     ยอดคงเหลือ ณ สิ้นเดือนอิงตาม balance ปัจจุบัน
 * - expenses         รายการทั้งหมดของเดือนนั้น
 *
 * ใช้คู่กับไฟล์ Logs:
 *   # summary,<transactions>,<total_spent>,<remaining_end>
 *   และแต่ละบรรทัด: description,category,amount,date
 */
public final class MonthlySummary {
    private final int transactions;
    private final double totalSpent;
    private final double remainingEnd;
    private final List<Expense> expenses;

    public MonthlySummary(int transactions, double totalSpent, double remainingEnd, List<Expense> expenses) {
        this.transactions = transactions;
        this.totalSpent = totalSpent;
        this.remainingEnd = remainingEnd;
        this.expenses = expenses;
    }

    /** factory สำหรับค่าว่าง (ยังไม่มีข้อมูลของเดือนนั้น) */
    public static MonthlySummary zero() {
        return new MonthlySummary(0, 0.0, 0.0, java.util.Collections.emptyList());
    }

    public int getTransactions() {
        return transactions;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public double getRemainingEnd() {
        return remainingEnd;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    @Override
    public String toString() {
        return "MonthlySummary{" +
                "transactions=" + transactions +
                ", totalSpent=" + totalSpent +
                ", remainingEnd=" + remainingEnd +
                ", expenses=" + (expenses == null ? 0 : expenses.size()) +
                '}';
    }
}
