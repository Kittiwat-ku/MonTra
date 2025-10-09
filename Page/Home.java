package Page;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import javax.swing.*;
import ButtonDesign.*;
import Controller.AppController;
import Service.AppContext;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
public class Home extends JPanel {
    private final AppContext appContext;
    private JScrollPane scroll;

    public Home(AppController controller, AppContext appContext) {
        this.appContext = appContext;
        setLayout(new BorderLayout());
        setOpaque(false);
        JPanel contentPanel = new JPanel(null);
        contentPanel.setOpaque(false);

        JLabel budgetl1 = new JLabel("฿");
        JLabel budgetl2 = new JLabel(" ");
        JLabel remainl1 = new JLabel("฿");
        JLabel remainl2 = new JLabel(" ");
        JLabel totalSpend = new JLabel("Total Spend: 0 ", SwingConstants.CENTER);

        try {
            double initBudget = appContext.getCategoryService().getDailyBudget();
            budgetl2.setText(String.format("%,.2f", initBudget));
            remainl2.setText(String.format("%,.2f", appContext.getRemining()));
            totalSpend.setText("Total Spend: " + String.format("%,.2f", appContext.getDailyExpense().getSpent()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel budgetl3 = new JLabel("Budget");
        budgetl1.setFont(new Font("Segoe UI", Font.BOLD, 30));
        budgetl1.setForeground(Color.WHITE);
        budgetl1.setBounds(10, 40, 200, 60);

        budgetl2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        budgetl2.setForeground(Color.WHITE);
        budgetl2.setBounds(30, 40, 200, 60);

        budgetl3.setFont(new Font("Segoe UI", Font.BOLD, 16));
        budgetl3.setForeground(Color.WHITE);
        budgetl3.setBounds(20, 100, 150, 30);
        contentPanel.add(budgetl1);
        contentPanel.add(budgetl2);
        contentPanel.add(budgetl3);

        JLabel remainl3 = new JLabel("Remain");
        remainl1.setFont(new Font("Segoe UI", Font.BOLD, 30));
        remainl1.setForeground(Color.WHITE);
        remainl1.setBounds(230, 40, 200, 60);

        remainl2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        remainl2.setForeground(findcolor(appContext.getRemining(), appContext.getCategoryService().getDailyBudget()));
        remainl2.setBounds(250, 40, 200, 60);

        remainl3.setFont(new Font("Segoe UI", Font.BOLD, 16));
        remainl3.setForeground(Color.WHITE);
        remainl3.setBounds(285, 100, 150, 30);
        contentPanel.add(remainl1);
        contentPanel.add(remainl2);
        contentPanel.add(remainl3);

        JSeparator line1 = new JSeparator(SwingConstants.HORIZONTAL);
        line1.setForeground(Color.WHITE);
        line1.setBounds(0, 100, 122, 5);
        contentPanel.add(line1);

        JSeparator line2 = new JSeparator(SwingConstants.HORIZONTAL);
        line2.setForeground(Color.WHITE);
        line2.setBounds(240, 100, 150, 5);
        contentPanel.add(line2);

        RoundedPanel chartPanel = new RoundedPanel(30, 30, new Color(255, 255, 255, 153), Color.GRAY, 1);
        chartPanel.setLayout(new BorderLayout());
        chartPanel.setBounds(75, 200, 220, 220);

        totalSpend.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chartPanel.add(totalSpend, BorderLayout.SOUTH);
        contentPanel.add(chartPanel);

        JLabel list = new JLabel("List:");
        list.setFont(new Font("Segoe UI", Font.BOLD, 40));
        list.setForeground(Color.WHITE);
        list.setBounds(150, 435, 100, 60);
        contentPanel.add(list);

        JPanel listPanel = new JPanel(new BorderLayout());

        JPanel head = new JPanel(new GridLayout(1, 3));
        head.setBackground(Color.LIGHT_GRAY);
        head.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));
        JLabel h1 = new JLabel("Description", SwingConstants.CENTER);
        JLabel h2 = new JLabel("Type", SwingConstants.CENTER);
        JLabel h3 = new JLabel("Price", SwingConstants.CENTER);
        Font hf = new Font("Segoe UI", Font.BOLD, 16);
        h1.setFont(hf); h2.setFont(hf); h3.setFont(hf);
        head.add(h1); head.add(h2); head.add(h3);
        listPanel.add(head, BorderLayout.NORTH);

        scroll = showlist("./File/Temp/TodayTemp.csv");
        listPanel.add(scroll, BorderLayout.CENTER);
        listPanel.setBounds(40, 500, 280, 180);
        contentPanel.add(listPanel);

        JLabel time = new JLabel();
        time.setFont(new Font("Segoe UI", Font.BOLD, 18));
        time.setForeground(new Color(255, 255, 224));
        time.setHorizontalAlignment(SwingConstants.RIGHT);
        time.setBounds(-25, 150, 300, 40);
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

        homebt.addActionListener(e -> controller.showPage("Sumpath"));
        addbt.addActionListener(e -> controller.showPage("Add"));
        morebt.addActionListener(e -> controller.showPage("More"));

        appContext.addListener(evt -> {
            if ("reload".equals(evt.getPropertyName())) {
                remainl2.setText(String.format("%,.2f", appContext.getRemining()));
                budgetl2.setText(String.format("%,.2f", appContext.getCategoryService().getDailyBudget()));
                totalSpend.setText("Total Spend: " + String.format("%,.2f", appContext.getDailyExpense().getSpent()));
                remainl2.setForeground(findcolor(appContext.getRemining(), appContext.getCategoryService().getDailyBudget()));
                reloadList(scroll, "./File/Temp/TodayTemp.csv");
            }
        });
    }

    Color findcolor(double remaining, double budget) {
        double tmp = (remaining / budget) * 100;
        if (tmp < 50 && tmp > 0) return Color.YELLOW;
        else if (tmp <= 0) return Color.RED;
        return Color.WHITE;
    }

    private JScrollPane showlist(String filePath) {
    DefaultListModel<String[]> model = new DefaultListModel<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;
        reader.readLine(); // skip header
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                model.addElement(new String[]{parts[0].trim(), parts[1].trim(), parts[2].trim()});
            }
        }
    } catch (IOException e) {
        model.addElement(new String[]{"NO Data", "", ""});
    }

    JList<String[]> list = new JList<>(model);
    list.setFixedCellHeight(40);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    list.setBackground(new Color(245, 245, 245));

    list.setCellRenderer((lst, value, index, isSelected, cellHasFocus) -> {
        JPanel row = new JPanel(new BorderLayout());
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JPanel infoPanel = new JPanel(new GridLayout(1, 3));
        infoPanel.setOpaque(false);
        infoPanel.add(new JLabel(value[0], SwingConstants.CENTER));
        infoPanel.add(new JLabel(value[1], SwingConstants.CENTER));
        infoPanel.add(new JLabel(value[2], SwingConstants.CENTER));
        row.add(infoPanel, BorderLayout.CENTER);

        // แสดงปุ่ม X เฉพาะตอนเลือก
        if (isSelected) {
            JButton delete = new JButton("X");
            delete.setFocusable(false);
            delete.setBorderPainted(false);
            delete.setContentAreaFilled(false);
            delete.setForeground(Color.RED);
            delete.setCursor(new Cursor(Cursor.HAND_CURSOR));
            row.add(delete, BorderLayout.EAST);
        }

        if (isSelected) row.setBackground(new Color(230, 240, 255));
        else row.setBackground(index % 2 == 0 ? new Color(250, 250, 250) : new Color(235, 235, 235));

        return row;
    });

    // เพิ่ม mouse listener เพื่อจับคลิกใน "โซนลบ" ทางขวา
    final int DELETE_HITBOX_WIDTH = 36; 
    list.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            int idx = list.locationToIndex(e.getPoint());
            if (idx < 0) return;

            Rectangle bounds = list.getCellBounds(idx, idx);
            if (bounds == null) return;

            int clickX = e.getX();
            int deleteZoneStartX = bounds.x + bounds.width - DELETE_HITBOX_WIDTH;

            // ให้กดลบได้เฉพาะตอนที่ item นั้นถูก select
            boolean isSelected = (list.getSelectedIndex() == idx);

            if (isSelected && clickX >= deleteZoneStartX) {
                try {
                    appContext.removeExpense(idx); // เรียกลบตาม index
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(list, "Delete failed: " + ex.getMessage());
                }
            }
        }
    });

    JScrollPane scroll = new JScrollPane(list);
    scroll.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    return scroll;
}

    private void reloadList(JScrollPane scroll, String filePath) {
        JScrollPane newScroll = showlist(filePath);
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

        float[] dist = {0.0f, 0.5f, 1.0f};
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
