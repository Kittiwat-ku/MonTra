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

    public Summary(AppController controller, AppContext appContext) {
        this.appContext = appContext;
        setLayout(null);

        // Back
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

        // Value labels
        totalSpendLabel = makeValueLabel(Color.RED,   120, 476);
        incomeLabel     = makeValueLabel(Color.YELLOW,120, 551);
        saveLabel       = makeValueLabel(Color.GREEN, 120, 626);
        transactionLabel= makeValueLabel(Color.WHITE, 160, 701);
        add(totalSpendLabel); add(incomeLabel); add(saveLabel); add(transactionLabel);

        // Titles
        add(makeTitleLabel("Select Month:",65,400));
        add(makeTitleLabel("Total Spend:",10,475));
        add(makeTitleLabel("Income:",10,550));
        add(makeTitleLabel("Save:",10,625));
        add(makeTitleLabel("Transaction:", 10,700));

        // Lines
        add(makeLine(0, 510)); add(makeLine(0, 585));
        add(makeLine(0, 660)); add(makeLine(0, 735));

        // Chart panel
        chartPanel = new RoundedPanel(30, 30, new Color(255, 255, 255, 153), Color.GRAY, 1);
        chartPanel.setBounds(30, 100, 300, 300);
        chartPanel.setLayout(new BorderLayout());
        add(chartPanel);

        // Month & Year selectors
        monthBox = new JComboBox<>();
        for (Month m : Month.values()) monthBox.addItem(m.name());
        monthBox.setBounds(180, 402, 100, 30);
        add(monthBox);

        yearBox = new JComboBox<>();
        int currentYear = java.time.Year.now().getValue();
        for (int y = currentYear - 3; y <= currentYear + 1; y++) yearBox.addItem(y);
        yearBox.setBounds(65, 402, 100, 30);
        add(yearBox);

        // Setให้ไปเลือกช่องว่าง
        monthBox.setSelectedIndex(-1);
        yearBox.setSelectedIndex(-1);

        // อัปเดตเมื่อเลือกครบทั้งสอง
        ActionListener refreshAction = e -> {
            if (yearBox.getSelectedIndex() != -1 && monthBox.getSelectedIndex() != -1) {
                int year = (Integer) yearBox.getSelectedItem();
                Month m = Month.valueOf((String) monthBox.getSelectedItem());
                updateSummaryAndChart(year, m);
            }
        };
        monthBox.addActionListener(refreshAction);
        yearBox.addActionListener(refreshAction);
    }

    // ===== helpers =====
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

    // ===== core =====
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

        java.util.List<Expense.CategorySlice> slices = appContext.getMonthlyCategorySlices(ym);
        if (slices == null || slices.isEmpty()) {
            pie.addData(new chart.ModelPieChart("No Data", 1, Color.LIGHT_GRAY));
            return pie;
        }
        for (Expense.CategorySlice s : slices) {
            pie.addData(new ModelPieChart(s.getCategory(), s.getAmount(), colorFromName(s.getCategory())));
        }
        return pie;
    }

    private Color colorFromName(String name) {
        int hash = Math.abs(name.hashCode());
        float hue = (hash % 360) / 360f;
        float sat = 0.6f + ((hash % 100) / 500f);
        float bri = 0.85f;
        return Color.getHSBColor(hue, sat, bri);
    }

    // เคลียร์ค่าทั้งหมด + เคลียร์กราฟ + reset combo
    private void clearSummaryDisplay() {
        totalSpendLabel.setText("0");
        incomeLabel.setText("0");
        saveLabel.setText("0");
        transactionLabel.setText("0");

        chartPanel.removeAll();
        chartPanel.revalidate();
        chartPanel.repaint();

        monthBox.setSelectedIndex(-1);
        yearBox.setSelectedIndex(-1);
    }

    // ===== background =====
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        Point2D start = new Point2D.Float(0, 0);
        Point2D end   = new Point2D.Float(w, h);
        float[] dist = {0.0f, 0.5f, 1.0f};
        Color[] colors = { new Color(0x4A5C58), new Color(0x0A5C36), new Color(0x1F2C2E) };
        LinearGradientPaint lgp = new LinearGradientPaint(start, end, dist, colors);
        g2d.setPaint(lgp);
        g2d.fillRect(0, 0, w, h);
    }
}
