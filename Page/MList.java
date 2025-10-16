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
import Expense.Expense;
import Expense.MonthlySummary;
import Service.AppContext;

/**
 * หน้าแสดงรายการรายจ่ายรายเดือน
 * -เลือกปีและเดือนเพื่อดูรายการ
 */
public class MList extends JPanel {

    private final AppContext appContext;
    private RoundedPanel MListPanel;
    private JScrollPane scroll;
    private JComboBox<String> monthBox;
    private JComboBox<Integer> yearBox;
    private boolean suppressEvents = false;
    private JList<Expense> list;
    private DefaultListModel<Expense> model;

    public MList(AppController controller, AppContext appContext) {
        this.appContext = appContext;
        setLayout(null);

        // Back button
        JButton b1 = new PillButton("← Back");
        b1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b1.setBounds(0, 10, 100, 30);
        ((PillButton) b1).setButtonStyle(PillButton.Style.OUTLINE);
        b1.setForeground(Color.WHITE);
        add(b1);
        b1.addActionListener(e -> {
            resetSelectors();
            controller.showPage("Summary");
        });

        // List Panel
        MListPanel = new RoundedPanel(30, 30, new Color(255, 255, 255), Color.GRAY, 1);
        MListPanel.setBounds(30, 150, 300, 600);
        MListPanel.setLayout(new BorderLayout());
        add(MListPanel);

        // Header
        JPanel header = new JPanel(new GridLayout(1, 2));
        header.setBackground(new Color(240, 240, 240));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        // Header Labels
        JLabel left = new JLabel("Desc. / Cate. / Time", SwingConstants.CENTER);
        left.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel right = new JLabel("Amount", SwingConstants.CENTER);
        right.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.add(left);
        header.add(right);
        MListPanel.add(header, BorderLayout.NORTH);

        // JList
        model = new DefaultListModel<>();
        list = new JList<>(model);
        list.setFixedCellHeight(45);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        list.setBackground(Color.WHITE);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer((lst, value, index, isSelected, cellHasFocus) -> {
            JPanel row = new JPanel(new BorderLayout());
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

            String dateTime = DateTimeformat(value.getDate());
            JPanel leftP = new JPanel(new GridLayout(2, 1));
            leftP.setOpaque(false);

            JLabel desc = new JLabel(value.getDescription(), SwingConstants.LEFT);
            desc.setFont(new Font("Segoe UI", Font.BOLD, 14));

            JLabel cat = new JLabel(value.getCategory() + " • " + dateTime, SwingConstants.LEFT);
            cat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            cat.setForeground(Color.GRAY);

            leftP.add(desc);
            leftP.add(cat);

            JLabel amount = new JLabel(String.format("%,.2f", value.getAmount()), SwingConstants.RIGHT);
            amount.setFont(new Font("Segoe UI", Font.BOLD, 14));
            amount.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

            row.add(leftP, BorderLayout.CENTER);
            row.add(amount, BorderLayout.EAST);

            if (isSelected) {
                row.setBackground(new Color(230, 240, 255));
            } else {
                row.setBackground(index % 2 == 0 ? new Color(250, 250, 250) : new Color(235, 235, 235));
            }

            return row;
        });

        scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        MListPanel.add(scroll, BorderLayout.CENTER);

        // ComboBox Year/Month
        monthBox = new JComboBox<>();
        monthBox.setBounds(200, 70, 100, 30);
        add(monthBox);

        yearBox = new JComboBox<>();
        yearBox.setBounds(60, 70, 100, 30);
        add(yearBox);

        // ค่าเริ่มต้น: เติม "ปี" (รวมปีปัจจุบันเสมอ) แต่ "ไม่เลือก" อัตโนมัติ
        YearMonthSelect();

        appContext.addListener(evt -> {
            if ("reload".equals(evt.getPropertyName())) {
                reloadList();
            }
        });
    }

    // โหลดปีเริ่มต้น (รวมปีปัจจุบันเสมอ) แต่ไม่เลือกอะไรเลย; เดือนเริ่มว่างจนกว่าจะเลือกปี
    private void YearMonthSelect() {
        suppressEvents = true;
        populateYearsCurrent();
        yearBox.setSelectedIndex(-1); // ไม่เลือกปี
        monthBox.removeAllItems(); // เดือนเริ่มว่าง
        monthBox.setSelectedIndex(-1); // ไม่เลือกเดือน
        suppressEvents = false;

        // เมื่อเลือกปี จะอัปเดตเดือนให้เลือก (ไม่เลือกอัตโนมัติ)
        yearBox.addActionListener(e -> {
            if (suppressEvents) return;
            Integer y = (Integer) yearBox.getSelectedItem();
            suppressEvents = true;
            if (y != null) {
                populateMonthsCurrent(y);
                monthBox.setSelectedIndex(-1);
            } else {
                monthBox.removeAllItems();
                monthBox.setSelectedIndex(-1);
            }
            suppressEvents = false;
            clearList();
        });

        // เมื่อเลือกเดือนและปีครบจะแสดงรายการรายจ่าย
        monthBox.addActionListener(e -> {
            if (suppressEvents) return;
            Integer y = (Integer) yearBox.getSelectedItem();
            String mText = (String) monthBox.getSelectedItem();
            if (y != null && mText != null) {
                updateMonthlyList(y, java.time.Month.valueOf(mText));
            } else {
                clearList();
            }
        });
    }
    // สำหรับ Year/Month ที่ "รวมปัจจุบันเสมอ" 
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
            months.add(now.getMonthValue()); // รวมเดือนปัจจุบันเสมอ
        }

        for (Integer m : months) {
            monthBox.addItem(java.time.Month.of(m).name());
        }
    }

    // โหลดข้อมูลรายจ่ายจาก AppContext.getMonthlySummary
    private void updateMonthlyList(int year, Month month) {
        try {
            model.clear();
            YearMonth ym = YearMonth.of(year, month);
            MonthlySummary summary = appContext.getMonthlySummary(ym);
            List<Expense> expenses = summary.getExpenses();
            if (expenses == null || expenses.isEmpty()) {
                model.addElement(new Expense("(No data)", "", 0.0, ""));
                return;
            }
            for (Expense e : expenses) {
                model.addElement(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reloadList() {
        if (yearBox.getSelectedIndex() != -1 && monthBox.getSelectedIndex() != -1) {
            int year = (Integer) yearBox.getSelectedItem();
            Month month = Month.valueOf((String) monthBox.getSelectedItem());
            updateMonthlyList(year, month);
        } else {
            clearList();
        }
    }

    private void clearList() {
        model.clear();
    }

    // เคลียร์รายการและรีเซ็ต combobox เป็น -1; คงรายการปี (รวมปีปัจจุบัน) ไว้
    private void resetSelectors() {
        suppressEvents = true;
        clearList();

        // ปี คงรายการเดิมไว้ ถ้ายังไม่มีให้เติม แล้วตั้งไม่เลือก
        if (yearBox.getItemCount() == 0) {
            populateYearsCurrent();
        }
        yearBox.setSelectedIndex(-1);

        // เดือน ล้างทิ้งและไม่เลือก
        monthBox.removeAllItems();
        monthBox.setSelectedIndex(-1);

        suppressEvents = false;
    }

    private String DateTimeformat(String raw) {
        try {
            java.time.LocalDateTime dt = java.time.LocalDateTime.parse(raw);
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern(
                "dd MMM yyyy, HH:mm", java.util.Locale.ENGLISH
            );
            return dt.format(fmt);
        } catch (Exception e) {
            return raw; // ถ้า parse ไม่ได้ ก็แสดงตามเดิม
        }
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
