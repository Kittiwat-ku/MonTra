package Expense;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;



public class DailyLog{
    List<Expense> expenses = new ArrayList<>();
    private double remainingBudget;
    private double spent;
    private Double DailyBudget;


    public DailyLog(Double DailyBudget){
        if (DailyBudget.equals(0.0)) {
           //goto budget setter gui
        }
        this.DailyBudget = DailyBudget;
        this.remainingBudget = DailyBudget;
    }
    public void add(Expense e){
        expenses.add(e);
        UpdateAndCalculateExpenses();
    }
    //if date = newday then clearlist and set spent, remain to 0.0
    public void endDay(){
        //Loop check

        exportToCSV("Log");
        expenses.clear();
        this.spent = 0.0;
        this.remainingBudget = this.DailyBudget;
        UpdateAndCalculateExpenses();
    }
    
    public void exportToCSV(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("./Logs/"+filename+".csv"))) {
            // header
            writer.println("Name,Amount,Category,DateTime");

            // data
            for (Expense e : expenses) {
                writer.printf("%s,%.2f,%s,%s%n",
                        e.getDescription(),
                        e.getAmount(),
                        e.getCategory(),
                        e.getTimestamp().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*         // Import log จาก CSV
    public void importFromCSV(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) { // ข้าม header
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String name = parts[0];
                    double amount = Double.parseDouble(parts[1]);
                    String category = parts[2];
                    LocalDateTime dateTime = LocalDateTime.parse(parts[3]);

                    this.add(new Expense(name, amount, category, dateTime));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    } */

    private void UpdateAndCalculateExpenses(){ //recalculate when DailyBudget have been change
        double tmp1 = 0; //Spent
        double tmp2 = this.DailyBudget;//Remain
        //calculate     
        for (Expense expense : expenses) {
        this.spent = (tmp1 += expense.getAmount());
        this.remainingBudget = (tmp2 -= expense.getAmount());
        }
    }
    public List<Expense> getExpenses() {
        return expenses;
    }
    public double getDailyBudget() {
        return DailyBudget;
    }
    public void setDailyBudget(double dailyBudget) {
        DailyBudget = dailyBudget;
        UpdateAndCalculateExpenses();
    }
    public double getRemainingBudget() {
        return remainingBudget;
    }
    public double getSpent() {
        return spent;
    }
}
