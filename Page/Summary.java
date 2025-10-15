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
    private boolean suppressEvents = false; // ป้องกัน event วิ่งซ้ำ

    public Summary(AppController controller, AppContext appContext) {
        this.appContext = appContext;
        setLayout(null);

        // ปุ่มย้อนกลับ
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

        // List button
        JButton viewList = new PillButton("View List");
        viewList.setFont(new Font("Segoe UI", Font.BOLD, 16));
        viewList.setBounds(260, 10, 100, 30);
        ((PillButton) viewList).setButtonStyle(PillButton.Style.OUTLINE);
        viewList.setForeground(Color.WHITE);
        add(viewList);
        viewList.addActionListener(e -> controller.showPage("MList"));


        // Label แสดงค่า
        totalSpendLabel = makeValueLabel(Color.RED, 120, 476);
        incomeLabel = makeValueLabel(Color.YELLOW, 120, 551);
        saveLabel = makeValueLabel(Color.GREEN, 120, 626);
        transactionLabel = makeValueLabel(Color.WHITE, 160, 701);
        add(totalSpendLabel);
        add(incomeLabel);
        add(saveLabel);
        add(transactionLabel);

        // Title label
        add(makeTitleLabel("Total Spend:", 10, 475));
        add(makeTitleLabel("Income:", 10, 550));
        add(makeTitleLabel("Save:", 10, 625));
        add(makeTitleLabel("Transaction:", 10, 700));

        // เส้นคั่น
        add(makeLine(0, 510));
        add(makeLine(0, 585));
        add(makeLine(0, 660));
        add(makeLine(0, 735));

        // กราฟ
        chartPanel = new RoundedPanel(30, 30, new Color(255, 255, 255, 153), Color.GRAY, 1);
        chartPanel.setBounds(30, 100, 300, 300);
        chartPanel.setLayout(new BorderLayout());
        add(chartPanel);

        // Combobox
        monthBox = new JComboBox<>();
        monthBox.setBounds(180, 402, 100, 30);
        add(monthBox);

        yearBox = new JComboBox<>();
        yearBox.setBounds(65, 402, 100, 30);
        add(yearBox);

        // เตรียมค่าเริ่มต้น
        suppressEvents = true;
        boolean usedYearFallback = populateYearsFromStorage();

        if (usedYearFallback) {
            Integer currentYear = java.time.Year.now().getValue();
            yearBox.setSelectedItem(currentYear);
            boolean usedMonthFallback = populateMonthsFromStorage(currentYear);
            if (usedMonthFallback) {
                Month currentMonth = java.time.LocalDate.now().getMonth();
                monthBox.setSelectedItem(currentMonth.name());
                suppressEvents = false;
                updateSummaryAndChart(currentYear, currentMonth);
            } else {
                monthBox.setSelectedIndex(-1);
                suppressEvents = false;
            }
        } else {
            monthBox.removeAllItems();
            yearBox.setSelectedIndex(-1);
            monthBox.setSelectedIndex(-1);
            suppressEvents = false;
        }

        // เมื่อเลือกปีแล้ว ให้อัปเดตรายชื่อเดือน แต่ไม่อัปเดตกราฟ
        yearBox.addActionListener(e -> {
            if (suppressEvents) {
                return;
            }
            if (yearBox.getSelectedIndex() != -1) {
                Integer year = (Integer) yearBox.getSelectedItem();
                suppressEvents = true;
                populateMonthsFromStorage(year);
                monthBox.setSelectedIndex(-1); 
                suppressEvents = false;
            } else {
                suppressEvents = true;
                monthBox.removeAllItems();
                monthBox.setSelectedIndex(-1);
                suppressEvents = false;
            }
        });

        // เมื่อเลือกเดือน + ปีครบ ให้แสดงสรุป
        monthBox.addActionListener(e2 -> {
            if (suppressEvents) {
                return;
            }
            if (yearBox.getSelectedIndex() != -1 && monthBox.getSelectedIndex() != -1) {
                int year = (Integer) yearBox.getSelectedItem();
                Month m = Month.valueOf((String) monthBox.getSelectedItem());
                updateSummaryAndChart(year, m);
            }
        });
    }

    private boolean populateYearsFromStorage() {
        yearBox.removeAllItems();
        try {
            List<Integer> years = appContext.getStorage().listExistingLogYears();
            if (years == null || years.isEmpty()) {
                yearBox.addItem(java.time.Year.now().getValue());
                return true;
            }
            for (Integer y : years) {
                yearBox.addItem(y);
            }
            return false;
        } catch (Exception e) {
            yearBox.addItem(java.time.Year.now().getValue());
            return true;
        }
    }

    private boolean populateMonthsFromStorage(int year) {
        monthBox.removeAllItems();
        try {
            List<Integer> months = appContext.getStorage().listExistingMonths(year);
            if (months == null || months.isEmpty()) {
                int currentYear = java.time.Year.now().getValue();
                int currentMonth = java.time.LocalDate.now().getMonthValue();
                if (year == currentYear) {
                    monthBox.addItem(java.time.Month.of(currentMonth).name());
                    return true;
                }
                return false;
            }
            for (Integer m : months) {
                Month mm = Month.of(m);
                monthBox.addItem(mm.name());
            }
            return false;
        } catch (Exception e) {
            int currentYear = java.time.Year.now().getValue();
            int currentMonth = java.time.LocalDate.now().getMonthValue();
            if (year == currentYear) {
                monthBox.addItem(java.time.Month.of(currentMonth).name());
                return true;
            }
            return false;
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

        List<CategorySlice> slices = appContext.getMonthlyCategorySlices(ym);
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

        suppressEvents = true;
        monthBox.removeAllItems();
        yearBox.removeAllItems();

        boolean usedYearFallback = populateYearsFromStorage();
        if (usedYearFallback) {
            Integer currentYear = java.time.Year.now().getValue();
            yearBox.setSelectedItem(currentYear);
            boolean usedMonthFallback = populateMonthsFromStorage(currentYear);
            if (usedMonthFallback) {
                Month currentMonth = java.time.LocalDate.now().getMonth();
                monthBox.setSelectedItem(currentMonth.name());
                suppressEvents = false;
                updateSummaryAndChart(currentYear, currentMonth);
                return;
            } else {
                monthBox.setSelectedIndex(-1);
            }
        } else {
            yearBox.setSelectedIndex(-1);
            monthBox.setSelectedIndex(-1);
        }
        suppressEvents = false;
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
