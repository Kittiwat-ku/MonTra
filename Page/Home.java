package Page;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import javax.swing.*;

import ButtonDesign.CircleButton;
import ButtonDesign.RoundedPanel;
import Controller.AppController;
import Expense.Expense;
import Expense.CategorySlice;
import Service.AppContext;
import chart.ModelPieChart;
import chart.PieChart;

import java.util.List;

public class Home extends JPanel {
    private final AppContext appContext;
    private JScrollPane scroll;
    private RoundedPanel chartPanel;

    public Home(AppController controller, AppContext appContext) {
        this.appContext = appContext;

        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel contentPanel = new JPanel(null);
        contentPanel.setOpaque(false);

        CircleButton settingsBtn = new CircleButton("🗃️");
        settingsBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        settingsBtn.setBounds(30, 30, 40, 40);
        contentPanel.add(settingsBtn);

        JLabel currency = new JLabel("฿");
        JLabel balanceLabel = new JLabel(" ");
        JLabel balanceTitle = new JLabel("Balance", SwingConstants.CENTER);

        double balance = appContext.getBalance();
        balanceLabel.setText(String.format("%,.2f", balance));

        currency.setFont(new Font("Segoe UI", Font.BOLD, 30));
        currency.setForeground(Color.WHITE);
        currency.setBounds(90, 90, 200, 60);

        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        balanceLabel.setForeground(findcolor(balance));
        balanceLabel.setBounds(120, 90, 200, 60);

        balanceTitle.setFont(new Font("Segoe UI", Font.BOLD, 25));
        balanceTitle.setForeground(Color.WHITE);
        balanceTitle.setBounds(100, 150, 150, 30);

        contentPanel.add(currency);
        contentPanel.add(balanceLabel);
        contentPanel.add(balanceTitle);

        JSeparator line = new JSeparator(SwingConstants.HORIZONTAL);
        line.setForeground(Color.WHITE);
        line.setBounds(80, 145, 200, 5);
        contentPanel.add(line);

        JLabel totalSpend = new JLabel("Total Spend: 0 ", SwingConstants.CENTER);
        totalSpend.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalSpend.setText("Total Spend: " + String.format("%,.2f", appContext.getSpentToday()));

        chartPanel = new RoundedPanel(30, 30, new Color(255, 255, 255, 153), Color.GRAY, 1);
        chartPanel.setLayout(new BorderLayout());
        chartPanel.setBounds(65, 200, 230, 230);

        PieChart pieChart = createPieChartFromContext();
        chartPanel.add(pieChart, BorderLayout.CENTER);
        chartPanel.add(totalSpend, BorderLayout.SOUTH);
        contentPanel.add(chartPanel);

        JLabel listTitle = new JLabel("List:");
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 40));
        listTitle.setForeground(Color.WHITE);
        listTitle.setBounds(150, 435, 100, 60);
        contentPanel.add(listTitle);

        JPanel listPanel = new JPanel(new BorderLayout());
        JPanel head = new JPanel(new GridLayout(1, 3));
        head.setBackground(Color.LIGHT_GRAY);
        head.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));
        JLabel h1 = new JLabel("Description", SwingConstants.CENTER);
        JLabel h2 = new JLabel("Type", SwingConstants.CENTER);
        JLabel h3 = new JLabel("Price", SwingConstants.CENTER);
        Font hf = new Font("Segoe UI", Font.BOLD, 16);
        h1.setFont(hf);
        h2.setFont(hf);
        h3.setFont(hf);
        head.add(h1);
        head.add(h2);
        head.add(h3);
        listPanel.add(head, BorderLayout.NORTH);

        scroll = buildListFromContext();
        listPanel.add(scroll, BorderLayout.CENTER);
        listPanel.setBounds(40, 500, 280, 180);
        contentPanel.add(listPanel);

        JLabel time = new JLabel();
        time.setFont(new Font("Segoe UI", Font.BOLD, 15));
        time.setForeground(new Color(255, 255, 224));
        time.setHorizontalAlignment(SwingConstants.RIGHT);
        time.setBounds(45, 0, 300, 40);
        contentPanel.add(time);

        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
            time.setText(sdf.format(new java.util.Date()));
        });
        timer.start();

        add(contentPanel, BorderLayout.CENTER);

        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        navBar.setOpaque(false);

        CircleButton homebt = new CircleButton("📊");
        homebt.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 23));
        homebt.setPreferredSize(new Dimension(50, 50));

        CircleButton morebt = new CircleButton("...");
        morebt.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 23));
        morebt.setPreferredSize(new Dimension(50, 50));

        CircleButton addbt = new CircleButton("➕");
        addbt.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 35));
        addbt.setPreferredSize(new Dimension(80, 80));

        navBar.add(homebt);
        navBar.add(addbt);
        navBar.add(morebt);
        add(navBar, BorderLayout.SOUTH);

        settingsBtn.addActionListener(e -> controller.showPage("Setting"));
        homebt.addActionListener(e -> controller.showPage("Summary"));
        addbt.addActionListener(e -> controller.showPage("Add"));
        morebt.addActionListener(e -> controller.showPage("More"));
        // listener จาก context
        appContext.addListener(evt -> {
            if ("reload".equals(evt.getPropertyName())) {
                double b = appContext.getBalance();
                balanceLabel.setText(String.format("%,.2f", b));
                balanceLabel.setForeground(findcolor(b));

                totalSpend.setText("Total Spend: " + String.format("%,.2f", appContext.getSpentToday()));

                // อัปเดตลิสต์จาก context
                reloadListFromContext();

                // อัปเดตกราฟจาก context
                chartPanel.removeAll();
                PieChart newChart = createPieChartFromContext();
                chartPanel.add(newChart, BorderLayout.CENTER);
                chartPanel.add(totalSpend, BorderLayout.SOUTH);
                chartPanel.revalidate();
                chartPanel.repaint();
            }
        });
    }

    //PieChart ดึงจาก AppContext 
    private PieChart createPieChartFromContext() {
        PieChart pieChart = new PieChart();
        pieChart.setChartType(PieChart.PeiChartType.DEFAULT);
        pieChart.setOpaque(false);

        List<CategorySlice> slices = appContext.getTodayCategorySlices();

        if (slices == null || slices.isEmpty()) {
            pieChart.addData(new ModelPieChart("No Data", 1, Color.LIGHT_GRAY));
        } else {
            for (CategorySlice s : slices) {
                Color color = generateColorFromName(s.getCategory());
                pieChart.addData(new ModelPieChart(s.getCategory(), s.getAmount(), color));
            }
        }

        double remaining = appContext.getBalance();
        if (remaining > 0) {
            pieChart.addData(new ModelPieChart("Remaining", remaining, new Color(70, 130, 180)));
        } else if (remaining < 0) {
            pieChart.addData(new ModelPieChart("Over", Math.abs(remaining), Color.RED));
        }
        return pieChart;
    }

    private Color generateColorFromName(String name) {
        int hash = Math.abs(name.hashCode());
        float hue = (hash % 360) / 360f;
        float saturation = 0.6f + ((hash % 100) / 500f);
        float brightness = 0.85f;
        return Color.getHSBColor(hue, saturation, brightness);
    }

    
    private Color findcolor(double remaining) {
        if (remaining <= 0)
            return Color.RED;
        if (remaining < 500)
            return Color.YELLOW;
        return Color.WHITE;
    }

    private JScrollPane buildListFromContext() {
        DefaultListModel<Expense> model = new DefaultListModel<>();
        for (Expense e : appContext.getTodayExpenses()) {
            model.addElement(e);
        }

        JList<Expense> list = new JList<>(model);
        list.setFixedCellHeight(40);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        list.setBackground(new Color(245, 245, 245));
        list.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        list.setCellRenderer((lst, value, index, isSelected, cellHasFocus) -> {
            JPanel row = new JPanel(new BorderLayout());
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

            JPanel infoPanel = new JPanel(new GridLayout(1, 3));
            infoPanel.setOpaque(false);
            infoPanel.add(new JLabel(value.getDescription(), SwingConstants.CENTER));
            infoPanel.add(new JLabel(value.getCategory(), SwingConstants.CENTER));
            infoPanel.add(new JLabel(String.format("%,.2f", value.getAmount()), SwingConstants.CENTER));
            row.add(infoPanel, BorderLayout.CENTER);

            if (isSelected) {
                JButton delete = new JButton("X");
                delete.setFocusable(false);
                delete.setBorderPainted(false);
                delete.setContentAreaFilled(false);
                delete.setForeground(Color.RED);
                delete.setCursor(new Cursor(Cursor.HAND_CURSOR));
                row.add(delete, BorderLayout.EAST);
            }

            if (isSelected)
                row.setBackground(new Color(230, 240, 255));
            else
                row.setBackground(index % 2 == 0 ? new Color(250, 250, 250) : new Color(235, 235, 235));

            return row;
        });

        final int DELETE_HITBOX_WIDTH = 36;
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int idx = list.locationToIndex(e.getPoint());
                if (idx < 0)
                    return;

                Rectangle bounds = list.getCellBounds(idx, idx);
                if (bounds == null)
                    return;

                int clickX = e.getX();
                int deleteZoneStartX = bounds.x + bounds.width - DELETE_HITBOX_WIDTH;
                boolean isSelected = (list.getSelectedIndex() == idx);

                if (isSelected && clickX >= deleteZoneStartX) {
                    try {
                        appContext.removeExpense(idx);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(list, "Delete failed: " + ex.getMessage());
                    }
                }
            }
        });

        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private void reloadListFromContext() {
        JScrollPane newScroll = buildListFromContext();
        scroll.setViewportView(newScroll.getViewport().getView());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(w, h);

        float[] dist = { 0.0f, 0.5f, 1.0f };
        Color[] colors = {
                new Color(0x4A5C58),
                new Color(0x0A5C36),
                new Color(0x1F2C2E)
        };

        LinearGradientPaint lgp = new LinearGradientPaint(start, end, dist, colors);
        g2d.setPaint(lgp);
        g2d.fillRect(0, 0, w, h);
    }
}
