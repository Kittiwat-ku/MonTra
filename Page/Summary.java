package Page;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import javax.swing.*;

import ButtonDesign.PillButton;
import ButtonDesign.RoundedPanel;
import Controller.AppController;
import Expense.CategorySlice;
import Expense.MonthlySummary;
import Service.AppContext;
import chart.ModelPieChart;
import chart.PieChart;

public class Summary extends JPanel {
    private final AppContext appContext;
    private RoundedPanel chartPanel;
    private JComboBox<String> monthBox;
    private JComboBox<Integer> yearBox;
    private JLabel totalSpendLabel, incomeLabel, saveLabel, transactionLabel;

    // ใช้กัน ActionEvent ไม่ให้ยิงตอนกำลังเติมรายการเดือน
    private boolean suppressMonthEvents = false;

    public Summary(AppController controller, AppContext appContext) {
        this.appContext = appContext;
        setLayout(null);

        JButton b1 = new PillButton("← Back");
        b1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b1.setBounds(0, 10, 100, 30);
        ((PillButton) b1).setButtonStyle(PillButton.Style.OUTLINE);
        b1.setForeground(Color.WHITE);
        add(b1);
        b1.addActionListener(e -> {
            clearSummaryDisplay();
            controller.showPage("Home");
        });

        totalSpendLabel = makeValueLabel(Color.RED, 120, 476);
        incomeLabel = makeValueLabel(Color.YELLOW, 120, 551);
        saveLabel = makeValueLabel(Color.GREEN, 120, 626);
        transactionLabel = makeValueLabel(Color.WHITE, 160, 701);
        add(totalSpendLabel);
        add(incomeLabel);
        add(saveLabel);
        add(transactionLabel);

        add(makeTitleLabel("Total Spend:", 10, 475));
        add(makeTitleLabel("Income:", 10, 550));
        add(makeTitleLabel("Save:", 10, 625));
        add(makeTitleLabel("Transaction:", 10, 700));

        add(makeLine(0, 510));
        add(makeLine(0, 585));
        add(makeLine(0, 660));
        add(makeLine(0, 735));

        chartPanel = new RoundedPanel(30, 30, new Color(255, 255, 255, 153), Color.GRAY, 1);
        chartPanel.setBounds(30, 100, 300, 300);
        chartPanel.setLayout(new BorderLayout());
        add(chartPanel);

        // Month / Year
        monthBox = new JComboBox<>();
        monthBox.setBounds(180, 402, 100, 30);
        add(monthBox);

        yearBox = new JComboBox<>();
        populateYearsFromStorage();
        yearBox.setBounds(65, 402, 100, 30);
        add(yearBox);

        // เริ่มต้นให้ว่าง
        monthBox.setSelectedIndex(-1);
        yearBox.setSelectedIndex(-1);

        // เมื่อเลือกปี ให้ เติมเดือน (แต่ยังไม่สรุปจนกว่าจะเลือกเดือนด้วย)
        yearBox.addActionListener(e -> {
            if (yearBox.getSelectedIndex() != -1) {
                Integer year = (Integer) yearBox.getSelectedItem();
                populateMonthsFromStorage(year);
                // บังคับให้ว่างหลังเติม เพื่อรอผู้ใช้เลือกเอง
                monthBox.setSelectedIndex(-1);
            } else {
                suppressMonthEvents = true;
                try {
                    monthBox.removeAllItems();
                    monthBox.setSelectedIndex(-1);
                } finally {
                    suppressMonthEvents = false;
                }
            }
        });

        // เมื่อเลือกเดือน กับ ปี ครบ ให้แสดงสรุป
        monthBox.addActionListener(e -> {
            if (suppressMonthEvents) {
                return; // กำลังเติมค่าอยู่ ไม่ต้องรีเฟรช
            }

            if (yearBox.getSelectedIndex() == -1) {
                return; // ยังไม่ได้เลือกปี
            }

            if (monthBox.getSelectedIndex() == -1) {
                return; // ยังไม่ได้เลือกเดือน
            }
            int year = (Integer) yearBox.getSelectedItem();
            Month m = Month.valueOf((String) monthBox.getSelectedItem());
            updateSummaryAndChart(year, m);
        });
    }

    // เติมรายชื่อปีจากโฟลเดอร์ Logs (ถ้าไม่มีให้fallback เป็นปีปัจจุบัน)
    private void populateYearsFromStorage() {
        yearBox.removeAllItems();
        try {
            List<Integer> years = appContext.getStorage().listExistingLogYears();
            if (years == null || years.isEmpty()) {
                years = java.util.List.of(java.time.Year.now().getValue());
            }
            for (Integer y : years)
                yearBox.addItem(y);
        } catch (Exception e) {
            yearBox.addItem(java.time.Year.now().getValue());
            System.err.println("Failed to load log years: " + e.getMessage());
        }
    }

    // เติมรายชื่อเดือนตามไฟล์ที่มี (ถ้าไม่มีให้ fallback เป็นเดือนปัจจุบัน)
    private void populateMonthsFromStorage(int year) {
        suppressMonthEvents = true; // กัน ActionEvent เด้งตอนเติม
        try {
            monthBox.removeAllItems();
            monthBox.setSelectedIndex(-1);

            List<Integer> months = appContext.getStorage().listExistingMonths(year);

            if (months == null || months.isEmpty()) {
                int currentYear = java.time.Year.now().getValue();
                int currentMonth = java.time.LocalDate.now().getMonthValue();
                if (year == currentYear) {
                    monthBox.addItem(java.time.Month.of(currentMonth).name());
                }
                return;
            }

            for (Integer m : months) {
                Month mm = Month.of(m);
                monthBox.addItem(mm.name());
            }
        } catch (Exception e) {
            System.err.println("Failed to load months for year " + year + ": " + e.getMessage());
            int currentYear = java.time.Year.now().getValue();
            int currentMonth = java.time.LocalDate.now().getMonthValue();
            if (year == currentYear) {
                monthBox.addItem(java.time.Month.of(currentMonth).name());
            }
        } finally {
            // ปล่อย event อีกครั้ง หลังเติมเสร็จ
            suppressMonthEvents = false;
        }
    }

    private JLabel makeValueLabel(Color c, int x, int y) {
        JLabel lbl = new JLabel("0", SwingConstants.LEFT);
        lbl.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        lbl.setForeground(c);
        lbl.setBounds(x, y, 200, 30);
        return lbl;
    }

    private JLabel makeTitleLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text, SwingConstants.LEFT);
        lbl.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        lbl.setForeground(Color.WHITE);
        lbl.setBounds(x, y, 200, 30);
        return lbl;
    }

    private JSeparator makeLine(int x, int y) {
        JSeparator line = new JSeparator(SwingConstants.HORIZONTAL);
        line.setBounds(x, y, 300, 5);
        return line;
    }

    private void updateSummaryAndChart(int year, Month month) {
        try {
            YearMonth ym = YearMonth.of(year, month);
            MonthlySummary summary = appContext.getMonthlySummary(ym);

            double totalSpent = summary.getTotalSpent();
            double remaining = summary.getRemainingEnd();
            double income = remaining + totalSpent;
            int trans = summary.getTransactions();

            totalSpendLabel.setText(String.format("%,.2f", totalSpent));
            incomeLabel.setText(String.format("%,.2f", income));
            saveLabel.setText(String.format("%,.2f", remaining));
            transactionLabel.setText(String.valueOf(trans));

            chartPanel.removeAll();
            PieChart chart = createPieChartForMonth(ym);
            chartPanel.add(chart, BorderLayout.CENTER);
            chartPanel.revalidate();
            chartPanel.repaint();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private PieChart createPieChartForMonth(YearMonth ym) throws Exception {
        PieChart pie = new PieChart();
        pie.setChartType(PieChart.PeiChartType.DEFAULT);
        pie.setOpaque(false);

        java.util.List<CategorySlice> slices = appContext.getMonthlyCategorySlices(ym);
        if (slices == null || slices.isEmpty()) {
            pie.addData(new ModelPieChart("No Data", 1, Color.LIGHT_GRAY));
            return pie;
        }
        for (CategorySlice s : slices) {
            pie.addData(new ModelPieChart(s.getCategory(), s.getAmount(), generateColorFromName(s.getCategory())));
        }
        return pie;
    }

    private Color generateColorFromName(String name) {
        int hash = Math.abs(name.hashCode() * 31 + name.length() * 97);
        float hue = ((hash % 1000) / 1000f); // (0.0–1.0)
        float saturation = 0.55f + ((hash % 300) / 1000f); // 0.55–0.85
        float brightness = 0.75f + ((hash % 200) / 1000f); // 0.75–0.95

        // ทำให้สีแตกต่างกันมากขึ้นในแต่ละชื่อ
        hue = (float) ((hue + Math.sin(hash)) % 1.0);

        // ป้องกัน hue เป็นค่าลบ
        if (hue < 0){
            hue += 1.0f;
        }
        return Color.getHSBColor(hue, saturation, brightness);
    }

    private void clearSummaryDisplay() {
        totalSpendLabel.setText("0");
        incomeLabel.setText("0");
        saveLabel.setText("0");
        transactionLabel.setText("0");

        chartPanel.removeAll();
        chartPanel.revalidate();
        chartPanel.repaint();

        monthBox.removeAllItems();
        yearBox.removeAllItems();
        populateYearsFromStorage();
        monthBox.setSelectedIndex(-1);
        yearBox.setSelectedIndex(-1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(w, h);
        float[] dist = { 0.0f, 0.5f, 1.0f };
        Color[] colors = { new Color(0x4A5C58), new Color(0x0A5C36), new Color(0x1F2C2E) };
        LinearGradientPaint lgp = new LinearGradientPaint(start, end, dist, colors);
        g2d.setPaint(lgp);
        g2d.fillRect(0, 0, w, h);
    }
}
