package Expense;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TempManager {
    public static List<Expense> readExpensesFromCSV(String filePath) {
        List<Expense> expenses = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) { // ข้ามบรรทัดแรก (header)
                    isHeader = false;
                    continue;
                }

                String[] parts = line.split(",");

                if (parts.length == 4) {
                    String description = parts[0].trim();
                    String category = parts[1].trim();
                    double amount = Double.parseDouble(parts[2].trim());
                    String date = parts[3].trim();

                    expenses.add(new Expense(description, amount, category, date));
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }

        return expenses;
    }
}
