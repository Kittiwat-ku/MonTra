package Expense;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CsvManager {

    public void exportToCSVwithfilename(List<Expense> expenses, String filename) {
        String filelocation = "./File/Export/" + filename + ".csv";
        writeToCSV(filelocation, "description,category,amount,date"); // header
        for (Expense expense : expenses) {
            String text = expense.getDescription() + "," + expense.getCategory() + "," + expense.getAmount() + ","
                    + expense.getDate();
            writeToCSV(filelocation, text);
        }
    }
    public void exportToCSVwithfilelocation(List<Expense> expenses, String location) {
        String filelocation = location;
        writeToCSV(filelocation, "description,category,amount,date"); // header
        for (Expense expense : expenses) {
            String text = expense.getDescription() + "," + expense.getCategory() + "," + expense.getAmount() + ","
                    + expense.getDate();
            writeToCSV(filelocation, text);
        }
    }
    // public void writeTempLog(List<Expense> expenses) {
    //     String filename = "./File/TodaySpent/todayTEMP.txt";
    //     Path pathToDelete = Paths.get("./File/TodaySpent/todayTEMP.txt");
    //     try {
    //         if (Files.deleteIfExists(pathToDelete)) {
    //             pathToDelete.getFileName();
    //             for (Expense expense : expenses) {
    //                 writeToCSV(filename,
    //                         expense.getDescription() + "," + expense.getCategory() + "," + expense.getAmount());
    //             }
    //         } else {
    //             for (Expense expense : expenses) {
    //                 writeToCSV(filename,
    //                         expense.getDescription() + "," + expense.getCategory() + "," + expense.getAmount());
    //             }
    //         }
    //     } catch (Exception e) {

    //     }

    // }

    public void exportToCSV(List<Expense> expenses) {
        // Create filename
        String date = LocalDate.now().toString();
        String filename = "report" + date + ".csv";
        // filelocation
        String filelocation = "./File/Logs/" + filename;
        // Write
        writeToCSV(filelocation, "description,category,amount,date"); // header
        for (Expense expense : expenses) {
            String text = expense.getDescription() + "," + expense.getCategory() + "," + expense.getAmount() + ","
                    + expense.getDate();
            writeToCSV(filelocation, text);
        }
    }

    private void writeToCSV(String filelocation, String text) {
        BufferedWriter bw = null;
        FileWriter fw = null;
        File f = null;
        try {
            f = new File(filelocation);
            fw = new FileWriter(f, true);
            bw = new BufferedWriter(fw);
            bw.write(text);
            bw.newLine();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            try {
                bw.close();
                fw.close();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
}
