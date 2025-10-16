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
            resetSelectors();
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
        totalSpendLabel = ValueLabel(Color.RED, 120, 476);
        incomeLabel = ValueLabel(Color.YELLOW, 120, 551);
        saveLabel = ValueLabel(Color.GREEN, 120, 626);
        transactionLabel = ValueLabel(Color.WHITE, 160, 701);
        add(totalSpendLabel);
        add(incomeLabel);
        add(saveLabel);
        add(transactionLabel);

        // Title label
        add(TitleLabel("Total Spend:", 10, 475));
        add(TitleLabel("Income:", 10, 550));
        add(TitleLabel("Save:", 10, 625));
        add(TitleLabel("Transaction:", 10, 700));

        // เส้นคั่น
        add(Line(0, 510));
        add(Line(0, 585));
        add(Line(0, 660));
        add(Line(0, 735));

        // Chart panel
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

        // ค่าเริ่มต้น: เติม "ปี" (รวมปีปัจจุบันเสมอ) แต่ "ไม่เลือก" อัตโนมัติ 
        suppressEvents = true;
        populateYearsCurrent();
        yearBox.setSelectedIndex(-1); // ไม่เลือกปี
        monthBox.removeAllItems(); // เดือนเริ่มว่างจนกว่าจะเลือกปี
        monthBox.setSelectedIndex(-1); // ไม่เลือกเดือน
        suppressEvents = false;

        // เมื่อเลือกปีแล้ว ให้อัปเดตรายชื่อเดือน (รวมเดือนปัจจุบันถ้าเป็นปีนี้) แต่ยังไม่เลือก
        yearBox.addActionListener(e -> {
            if (suppressEvents) return;
            Integer y = (Integer) yearBox.getSelectedItem();
            suppressEvents = true;
            if (y != null) {
                populateMonthsCurrent(y);
                monthBox.setSelectedIndex(-1); // ไม่เลือกเดือนอัตโนมัติ
            } else {
                monthBox.removeAllItems();
                monthBox.setSelectedIndex(-1);
            }
            suppressEvents = false;
            clearSummaryDisplay(); // เคลียร์ข้อมูลจนกว่าจะเลือกทั้งปีและเดือน
        });

        // เมื่อเลือกเดือนและปีครบจะให้แสดงสรุป
        monthBox.addActionListener(e2 -> {
            if (suppressEvents) return;
            Integer y = (Integer) yearBox.getSelectedItem();
            String mText = (String) monthBox.getSelectedItem();
            if (y != null && mText != null) {
                updateSummaryAndChart(y, java.time.Month.valueOf(mText));
            } else {
                clearSummaryDisplay();
            }
        });
    }

    // Helpers สำหรับ Year/Month ที่ "รวมปัจจุบันเสมอ"
    private void populateYearsCurrent() {
        yearBox.removeAllItems();
        java.util.Set<Integer> years = new java.util.TreeSet<>();
        try {
            List<Integer> fromStorage = appContext.getStorage().listExistingLogYears();
            if (fromStorage != null) years.addAll(fromStorage);
        } catch (Exception ignored) {}
        years.add(java.time.Year.now().getValue()); // รวมปีปัจจุบันเสมอ

        for (Integer y : years) yearBox.addItem(y);
    }

    private void populateMonthsCurrent(int year) {
        monthBox.removeAllItems();
        java.util.Set<Integer> months = new java.util.TreeSet<>();
        try {
            List<Integer> fromStorage = appContext.getStorage().listExistingMonths(year);
            if (fromStorage != null) months.addAll(fromStorage);
        } catch (Exception ignored) {}

        java.time.YearMonth now = java.time.YearMonth.now();
        if (year == now.getYear()) {
            months.add(now.getMonthValue()); // รวมเดือนปัจจุบันเสมอถ้าเป็นปีนี้
        }

        for (Integer m : months) {
            monthBox.addItem(java.time.Month.of(m).name());
        }
    }

    /**
     * สำหรับสร้าง JLabel แสดงค่า
     * @param c สีตัวอักษร
     * @param x ตำแหน่งแกน x
     * @param y ตำแหน่งแกน y
     * @return JLabel ที่สร้างขึ้น
     */
    private JLabel ValueLabel(Color c, int x, int y) {
        JLabel lbl = new JLabel("0", SwingConstants.LEFT);
        lbl.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        lbl.setForeground(c);
        lbl.setBounds(x, y, 200, 30);
        return lbl;
    }

    private JLabel TitleLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text, SwingConstants.LEFT);
        lbl.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        lbl.setForeground(Color.WHITE);
        lbl.setBounds(x, y, 200, 30);
        return lbl;
    }

    private JSeparator Line(int x, int y) {
        JSeparator line = new JSeparator(SwingConstants.HORIZONTAL);
        line.setBounds(x, y, 300, 5);
        return line;
    }

    // อัปเดตข้อมูลสรุปและกราฟ
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
            PieChart chart = createPieChart(ym);
            chartPanel.add(chart, BorderLayout.CENTER);
            chartPanel.revalidate();
            chartPanel.repaint();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // สร้าง PieChart จากข้อมูลหมวดหมู่รายจ่าย
    private PieChart createPieChart(YearMonth ym) throws Exception {
        PieChart pie = new PieChart();
        pie.setChartType(PieChart.PeiChartType.DEFAULT);
        pie.setOpaque(false);

        List<CategorySlice> slices = appContext.getMonthlyCategorySlices(ym);
        if (slices == null || slices.isEmpty()) {
            pie.addData(new ModelPieChart("No Data", 1, Color.LIGHT_GRAY));
            return pie;
        }
        for (CategorySlice s : slices) {
            pie.addData(new ModelPieChart(s.getCategory(), s.getAmount(), generateColor(s.getCategory())));
        }
        return pie;
    }

    // สร้างสีจากชื่อหมวดหมู่ (เพื่อให้แต่ละหมวดมีสีประจำตัว)
    private Color generateColor(String name) {
        int hash = Math.abs(name.hashCode() * 31 + name.length() * 97);
        float hue = ((hash % 1000) / 1000f);
        float saturation = 0.55f + ((hash % 300) / 1000f);
        float brightness = 0.75f + ((hash % 200) / 1000f);

        // ทำให้สีแตกต่างกันมากขึ้นในแต่ละชื่อ
        hue = (float) ((hue + Math.sin(hash)) % 1.0);

        // ป้องกัน hue เป็นค่าลบ
        if (hue < 0){
            hue += 1.0f;
        }
        return Color.getHSBColor(hue, saturation, brightness);
    }

    // เคลียร์ค่าแสดงผลและรีเซ็ต combobox เป็น -1 (ไม่เลือก) แต่ยังคงรายการปี/เดือนปัจจุบันใน list
    private void resetSelectors() {
        suppressEvents = true;
        // เคลียร์ค่าแสดงผล
        totalSpendLabel.setText("0");
        incomeLabel.setText("0");
        saveLabel.setText("0");
        transactionLabel.setText("0");

        // เคลียร์กราฟ
        chartPanel.removeAll();
        chartPanel.revalidate();
        chartPanel.repaint();

        // ปี คงรายการเดิมไว้แต่ตั้งไม่เลือก
        if (yearBox.getItemCount() == 0) {
            populateYearsCurrent();
        }
        yearBox.setSelectedIndex(-1);

        // เดือน ล้างรายการและตั้งไม่เลือก
        monthBox.removeAllItems();
        monthBox.setSelectedIndex(-1);

        suppressEvents = false;
    }

    // ใช้ตอนเปลี่ยนปี/เดือนไม่ครบ เพื่อเคลียร์ค่าหน้าจอ โดย "ไม่" ยุ่งกับรายการใน combobox
    private void clearSummaryDisplay() {
        totalSpendLabel.setText("0");
        incomeLabel.setText("0");
        saveLabel.setText("0");
        transactionLabel.setText("0");

        chartPanel.removeAll();
        chartPanel.revalidate();
        chartPanel.repaint();
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
