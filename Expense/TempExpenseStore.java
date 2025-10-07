package Expense;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TempExpenseStore {
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final Path dir;

    public TempExpenseStore(String folder){ this.dir = Paths.get(folder); }

    private Path todayPath() throws IOException {
        if (!Files.exists(dir)) Files.createDirectories(dir);
        return dir.resolve("expenses_" + LocalDate.now().format(DF) + ".csv");
    }

    public void resetToday() throws IOException {
        Path p = todayPath();
        Files.write(p, List.of("date,category,amount,description"), StandardCharsets.UTF_8);
    }

    public List<Expense> readToday() throws IOException {
        Path p = todayPath();
        if (!Files.exists(p)) resetToday();
        List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);
        List<Expense> out = new ArrayList<>();
        for (int i=1;i<lines.size();i++){
            String line = lines.get(i);
            if (line.isBlank()) continue;
            String[] a = split(line);
            if (a.length < 4) continue;
            out.add(new Expense(a[0].trim(), parseDouble(a[2]), unescape(a[1]), unescape(a[3])));
        }
        return out;
    }

    public void appendToday(Expense e) throws IOException {
        Path p = todayPath();
        if (!Files.exists(p)) resetToday();
        String row = String.join(",",
                e.getDate(),
                escape(e.getCategory()),
                toStr(e.getAmount()),
                escape(e.getDescription())
        );
        Files.writeString(p, System.lineSeparator() + row, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }

    // ---------- helpers ----------
    private static String toStr(double v){ return String.format(Locale.US, "%.2f", v); }
    private static double parseDouble(String s){ return s==null||s.isBlank()?0:Double.parseDouble(s.trim()); }
    private static String[] split(String line){ return line.split(",", -1); }
    private static String escape(String s){
        if (s==null) return "";
        String x = s.replace("\"","\"\"");
        if (x.contains(",") || x.contains("\n")) return "\"" + x + "\"";
        return x;
    }
    private static String unescape(String s){
        if (s==null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) s = s.substring(1, s.length()-1).replace("\"\"","\"");
        return s;
    }
}