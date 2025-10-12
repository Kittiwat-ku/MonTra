package Expense;

public class MonthlySummary {
    private final int transactions;
    private final double totalSpent;
    private final double remainingEnd;

    public MonthlySummary(int transactions, double totalSpent, double remainingEnd) {
        this.transactions = transactions;
        this.totalSpent = totalSpent;
        this.remainingEnd = remainingEnd;
    }
    public int getTransactions() { return transactions; }
    public double getTotalSpent() { return totalSpent; }
    public double getRemainingEnd() { return remainingEnd; }

    /** รวมยอดจากอีกฝั่ง; ถ้า takeOtherRemaining=true จะใช้ remaining ของ other */
    public MonthlySummary plus(MonthlySummary other, boolean takeOtherRemaining) {
        double rem = takeOtherRemaining ? other.remainingEnd : this.remainingEnd;
        return new MonthlySummary(
            this.transactions + other.transactions,
            this.totalSpent + other.totalSpent,
            rem
        );
    }

    public static MonthlySummary zero() { return new MonthlySummary(0, 0.0, 0.0); }
}
