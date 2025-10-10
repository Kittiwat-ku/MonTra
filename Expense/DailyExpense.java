package Expense;

import java.util.ArrayList;
import java.util.List;

public class DailyExpense {

    //เก็บObj Expense ไว้เป็นArrayList
    private List<Expense> expenses = new ArrayList<>();

    /**
     * คืนค่ารายจ่าย
     *@return รายจ่ายทั้งหมดในรูปแบบArrayList
     */
    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }
    /**
     * set รายการสินค้าไปที่expenses ถ้าListมีข้อมูลรายจ่ายก็สร้างแล้วในListไปใส่ในexpenses ถ้ามีไม่ก็สร้างexpensesป่าว
     * @param list รายจ่าย
     */
    public void setExpenses(List<Expense> list) {
        if (list == null) {
            this.expenses = new ArrayList<>();
        } else {
            this.expenses = new ArrayList<>(list);
        }
    }
    /**
     * เพื่มข้อมูลรายจ่ายใส่expenses
     * @param e obj Expense ข้อมูลของรายการจ่ายนั้น
     */
    public void addExpense(Expense e) {
        if (e != null) {
            expenses.add(e);
        }
    }
    /**
     * ลบรายจ่ายด้วยIndex
     * @param index ของลบรายจ่ายที่จะลบ
     */
    public void removeAt(int index) {
        if (index >= 0 && index < expenses.size()) {
            expenses.remove(index);
        }
    }
    /**
     * clear รายจ่ายทั้งหมด
     */
    public void clear() {
        expenses.clear();
    }
    /**
     * ราคาของสินค้าทั้งหมด
     * @return sum ราคาของสินค้าทั้งหมดบวกกัน
     */
    public double getSpent() {
        double sum = 0.0;
        for (Expense e : expenses) {
            sum += e.getAmount();
        }
        return sum;
    }
    /**
     * เช็คexpensesว่าว่างมั้ย
     * @return boolean trueเมื่อexpensesว่าง
     */
    public boolean isEmpty() {
        return expenses.isEmpty();
    }
}
