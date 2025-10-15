package Page;

import java.awt.*;
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
    private boolean suppressMonthEvents = false;

    public Summary(AppController controller, AppContext appContext) {
        this.appContext = appContext;
        setLayout(null);

        JButton back = new PillButton("← Back");
        back.setFont(new Font("Segoe UI", Font.BOLD, 16));
        back.setBounds(0, 10, 100, 30);
        ((PillButton) back).setButtonStyle(PillButton.Style.OUTLINE);
        back.setForeground(Color.WHITE);
        add(back);
        back.addActionListener(e -> controller.showPage("Home"));

        JButton viewList = new PillButton("View List");
        viewList.setFont(new Font("Segoe UI", Font.BOLD, 16));
        viewList.setBounds(260, 10, 100, 30);
        ((PillButton) viewList).setButtonStyle(PillButton.Style.OUTLINE);
        viewList.setForeground(Color.WHITE);
        add(viewList);

        // label info
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

        // month/year combo
        monthBox = new JComboBox<>();
        monthBox.setBounds(180, 402, 100, 30);
        add(monthBox);

        yearBox = new JComboBox<>();
        yearBox.setBounds(65, 402, 100, 30);
        add(yearBox);

        populateYearsFromStorage();

        yearBox.addActionListener(e -> {
            if (yearBox.getSelectedIndex() != -1) {
                Integer year = (Integer) yearBox.getSelectedItem();
                populateMonthsFromStorage(year);
                monthBox.setSelectedIndex(-1);
            }
        });

        monthBox.addActionListener(e -> {
            if (suppressMonthEvents) return;
            if (yearBox.getSelectedIndex() == -1 || monthBox.getSelectedIndex() == -1) return;
            int year = (Integer) yearBox.getSelectedItem();
            Month m = Month.valueOf((String) monthBox.getSelectedItem());
            updateSummaryAndChart(year, m);
        });

        /*  ไปหน้า MList พร้อมส่งเดือนที่เลือก
        viewList.addActionListener(e -> {
            if (yearBox.getSelectedIndex() != -1 && monthBox.getSelectedIndex() != -1) {
                int year = (Integer) yearBox.getSelectedItem();
                Month m = Month.valueOf((String) monthBox.getSelectedItem());
                appContext.setSelectedMonth(YearMonth.of(year, m));
            }
            controller.showPage("MList");
        });*/
    }

    private void populateYearsFromStorage() {
        yearBox.removeAllItems();
        try {
            List<Integer> years = appContext.getStorage().listExistingLogYears();
            for (Integer y : years) yearBox.addItem(y);
        } catch (Exception e) {
            yearBox.addItem(YearMonth.now().getYear());
        }
    }

    private void populateMonthsFromStorage(int year) {
    suppressMonthEvents = true;
    try {
        monthBox.removeAllItems();
        List<Integer> months = appContext.getStorage().listExistingMonths(year);
        for (Integer m : months) {
            monthBox.addItem(Month.of(m).name());
        }
    } catch (Exception e) {
        e.printStackTrace(); // หรือ JOptionPane.showMessageDialog(this, e.getMessage());
    } finally {
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

            totalSpendLabel.setText(String.format("%,.2f", summary.getTotalSpent()));
            saveLabel.setText(String.format("%,.2f", summary.getRemainingEnd()));
            incomeLabel.setText(String.format("%,.2f", summary.getRemainingEnd() + summary.getTotalSpent()));
            transactionLabel.setText(String.valueOf(summary.getTransactions()));

            chartPanel.removeAll();
            PieChart chart = new PieChart();
            for (CategorySlice s : appContext.getMonthlyCategorySlices(ym)) {
                chart.addData(new ModelPieChart(s.getCategory(), s.getAmount(),
                        generateColorFromName(s.getCategory())));
            }
            chartPanel.add(chart, BorderLayout.CENTER);
            chartPanel.revalidate();
            chartPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(getWidth(), getHeight());
        float[] dist = {0f, 0.5f, 1f};
        Color[] colors = {new Color(0x4A5C58), new Color(0x0A5C36), new Color(0x1F2C2E)};
        LinearGradientPaint lgp = new LinearGradientPaint(start, end, dist, colors);
        g2.setPaint(lgp);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}
