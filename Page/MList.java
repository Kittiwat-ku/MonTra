package Page;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.geom.Point2D;
import java.time.YearMonth;
import javax.swing.*;
import ButtonDesign.PillButton;
import ButtonDesign.RoundedPanel;
import Controller.AppController;
import Expense.Expense;
import Service.AppContext;

public class MList extends JPanel {
    private final AppContext appContext;
    private RoundedPanel MListPanel;
    private JScrollPane scroll;

    public MList(AppController controller, AppContext appContext) {
        this.appContext = appContext;
        setLayout(null);

        JButton b1 = new PillButton("← Back");
        b1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b1.setBounds(0, 10, 100, 30);
        ((PillButton) b1).setButtonStyle(PillButton.Style.OUTLINE);
        b1.setForeground(Color.WHITE);
        add(b1);
        b1.addActionListener(e -> controller.showPage("Summary"));

        MListPanel = new RoundedPanel(90, 0, new Color(255, 255, 255), Color.GRAY, 1);
        MListPanel.setBounds(30, 150, 300, 600);
        MListPanel.setLayout(new BorderLayout());
        add(MListPanel);

        JPanel header = new JPanel(new GridLayout(1, 2));
        header.setBackground(new Color(240, 240, 240));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        JLabel left = new JLabel("Desc. / Cate. / Time", SwingConstants.CENTER);
        left.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel right = new JLabel("Amount", SwingConstants.CENTER);
        right.setFont(new Font("Segoe UI", Font.BOLD, 14));

        header.add(left);
        header.add(right);
        MListPanel.add(header, BorderLayout.NORTH);

        scroll = MonthlyList();
        MListPanel.add(scroll, BorderLayout.CENTER);

        appContext.addListener(evt -> {
            if ("reload".equals(evt.getPropertyName())) {
                reloadList();
            }
        });
    }


    private JScrollPane MonthlyList() {
        DefaultListModel<Expense> model = new DefaultListModel<>();
        try {
            YearMonth month = YearMonth.now();
            for (Expense e : appContext.getMonthlyExpenses(month)) {
                model.addElement(e);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading monthly data: " + ex.getMessage());
        }

        JList<Expense> list = new JList<>(model);
        list.setFixedCellHeight(45);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        list.setBackground(Color.WHITE);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        list.setCellRenderer((lst, value, index, isSelected, cellHasFocus) -> {
            JPanel row = new JPanel(new BorderLayout());
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

            String dateTime = formatDateTime(value.getDate());

            JPanel left = new JPanel(new GridLayout(2, 1));
            left.setOpaque(false);

            JLabel desc = new JLabel(value.getDescription(), SwingConstants.LEFT);
            desc.setFont(new Font("Segoe UI", Font.BOLD, 14));

            JLabel cat = new JLabel(value.getCategory() + " • " + dateTime, SwingConstants.LEFT);
            cat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            cat.setForeground(Color.GRAY);

            left.add(desc);
            left.add(cat);

            JLabel amount = new JLabel(String.format("%,.2f", value.getAmount()), SwingConstants.RIGHT);
            amount.setFont(new Font("Segoe UI", Font.BOLD, 14));
            amount.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

            row.add(left, BorderLayout.CENTER);
            row.add(amount, BorderLayout.EAST);

            if (isSelected)
                row.setBackground(new Color(230, 240, 255));
            else
                row.setBackground(index % 2 == 0 ? new Color(250, 250, 250) : new Color(235, 235, 235));

            return row;
        });

        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return sp;
    }


    private void reloadList() {
        JScrollPane newScroll = MonthlyList();
        MListPanel.remove(scroll);
        scroll = newScroll;
        MListPanel.add(scroll, BorderLayout.CENTER);
        MListPanel.revalidate();
        MListPanel.repaint();
    }

    private String formatDateTime(String raw) {
        try {
            java.time.LocalDateTime dt = java.time.LocalDateTime.parse(raw);
            java.time.format.DateTimeFormatter fmt =
                    java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
            return dt.format(fmt);
        } catch (Exception e) {
            return raw;
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
        float[] dist = {0.0f, 0.5f, 1.0f};
        Color[] colors = {new Color(0x4A5C58), new Color(0x0A5C36), new Color(0x1F2C2E)};
        LinearGradientPaint lgp = new LinearGradientPaint(start, end, dist, colors);
        g2d.setPaint(lgp);
        g2d.fillRect(0, 0, w, h);
    }
}
