package Expense;

/**
 * สรุปรายเดือน:
 * - transactions     จำนวนรายการทั้งหมดในเดือน
 * - totalSpent       ยอดใช้จ่ายรวมในเดือน
 * - remainingEnd     ยอดคงเหลือ ณ สิ้นเดือนอิงตาม balance ปัจจุบัน
 *
 * ใช้คู่กับไฟล์ Logs:
 *   # summary,<transactions>,<total_spent>,<remaining_end>
 *   # summary,transactions,total_spent,remaining_end
 */
public final class MonthlySummary {
    private final int transactions;
    private final double totalSpent;
    private final double remainingEnd;

    public MonthlySummary(int transactions, double totalSpent, double remainingEnd) {
        this.transactions = transactions;
        this.totalSpent = totalSpent;
        this.remainingEnd = remainingEnd;
    }

    //factory สำหรับค่าว่าง (ยังไม่มีข้อมูลของเดือนนั้น)
    public static MonthlySummary zero() {
        return new MonthlySummary(0, 0.0, 0.0);
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

    @Override
    public String toString() {
        return "MonthlySummary{" +"transactions=" + transactions +", totalSpent=" + totalSpent +", remainingEnd=" + remainingEnd +'}';
    }
}
