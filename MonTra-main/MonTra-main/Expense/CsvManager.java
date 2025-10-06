package Expense;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;

public class CsvManager {

    public void exportToCSVwithfilename(List<Expense> expenses,String filename){
        String filelocation = "./File/Export/"+filename+".csv";
        writeToCSV(filelocation, "description,category,amount,date");
        for (Expense expense : expenses) {
            String tmp = expense.getDescription()+expense.getCategory()+expense.getAmount()+expense.getTimestamp();
            writeToCSV(filelocation, tmp);
        }
    }
    
    public void exportToCSV(List<Expense> expenses){
        //Create filename
        String date = LocalDate.now().toString();
        String filename = "report"+date+".csv";
        //filelocation
        String filelocation = "./File/Logs/"+filename;
        //Write
        writeToCSV(filelocation, "description,category,amount,date");
        for (Expense expense : expenses) {
            String tmp = expense.getDescription()+expense.getCategory()+expense.getAmount()+expense.getTimestamp();
            writeToCSV(filelocation, tmp);
        }
    }
    private void writeToCSV(String filelocation,String text){
        BufferedWriter bw = null;
        FileWriter fw = null;
        File f = null;
        try {
            f = new File(filelocation);
            fw = new FileWriter(f);
            bw = new BufferedWriter(fw);
            bw.write(text);
        } catch (Exception e) {
            // TODO: handle exception
        } finally{
            try {
                bw.close();
                fw.close();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
}
