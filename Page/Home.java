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
import chart.ModelPieChart;
import chart.PieChart;

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

        JLabel remainl1 = new JLabel("฿");
        JLabel remainl2 = new JLabel(" ");
        JLabel totalSpend = new JLabel("Total Spend: 0 ", SwingConstants.CENTER);

        try {
            double balance = appContext.getBalance();
            remainl2.setText(String.format("%,.2f", balance));
            double spent = appContext.getTodayExpenses().stream().mapToDouble(Expense.Expense::getAmount).sum();
            totalSpend.setText("Total Spend: " + String.format("%,.2f", spent));
        } catch (Exception e) {
            e.printStackTrace();
        }

        CircleButton budget = new CircleButton("🗃️");
        budget.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        budget.setBounds(30, 30, 40, 40);
        contentPanel.add(budget);

        remainl1.setFont(new Font("Segoe UI", Font.BOLD, 30));
        remainl1.setForeground(Color.WHITE);
        remainl1.setBounds(90, 90, 200, 60);

        remainl2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        remainl2.setForeground(findcolor(appContext.getBalance()));
        remainl2.setBounds(120, 90, 200, 60);

        JLabel remainl3 = new JLabel("Balance", SwingConstants.CENTER);
        remainl3.setFont(new Font("Segoe UI", Font.BOLD, 25));
        remainl3.setForeground(Color.WHITE);
        remainl3.setBounds(100, 150, 150, 30);
        contentPanel.add(remainl1);
        contentPanel.add(remainl2);
        contentPanel.add(remainl3);

        JSeparator line2 = new JSeparator(SwingConstants.HORIZONTAL);
        line2.setForeground(Color.WHITE);
        line2.setBounds(80, 145, 200, 5);
        contentPanel.add(line2);

        chartPanel = new RoundedPanel(30, 30, new Color(255, 255, 255, 153), Color.GRAY, 1);
        chartPanel.setLayout(new BorderLayout());
        chartPanel.setBounds(65, 200, 230, 230);

        PieChart pieChart = createPieChartFromFile("./File/Temp/TodayTemp.csv");
        chartPanel.add(pieChart, BorderLayout.CENTER);

        totalSpend.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chartPanel.add(totalSpend, BorderLayout.SOUTH);
        contentPanel.add(chartPanel);

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

        budget.addActionListener(e -> controller.showPage("Setting"));
        homebt.addActionListener(e -> controller.showPage("Sumpath"));
        addbt.addActionListener(e -> controller.showPage("Add"));
        morebt.addActionListener(e -> controller.showPage("More"));

        // ฟัง event จาก AppContext
        appContext.addListener(evt -> {
            if ("reload".equals(evt.getPropertyName())) {
                // รีกับเป็นค่าในGuiห้ตรงกับMem
                double balanceNow = appContext.getBalance();
                remainl2.setText(String.format("%,.2f", balanceNow));
                double spentNow = appContext.getTodayExpenses().stream().mapToDouble(Expense.Expense::getAmount).sum();
                totalSpend.setText("Total Spend: " + String.format("%,.2f", spentNow));
                remainl2.setForeground(findcolor(balanceNow));

                // reload list จากไฟล์ temp
                reloadList(scroll, "./File/Temp/TodayTemp.csv");

                // reload chart
                chartPanel.removeAll();
                PieChart newChart = createPieChartFromFile("./File/Temp/TodayTemp.csv");
                chartPanel.add(newChart, BorderLayout.CENTER);
                chartPanel.add(totalSpend, BorderLayout.SOUTH);
                chartPanel.revalidate();
                chartPanel.repaint();
            }
        });
    }

    private PieChart createPieChartFromFile(String filePath) {
        PieChart pieChart = new PieChart();
        pieChart.setChartType(PieChart.PeiChartType.DEFAULT);
        pieChart.setOpaque(false);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            if (line == null) {
                pieChart.addData(new ModelPieChart("No Data", 1, Color.LIGHT_GRAY));
                return pieChart;
            }

            java.util.Map<String, Double> categorySum = new java.util.LinkedHashMap<>();
            java.util.Map<String, Color> categoryColor = new java.util.LinkedHashMap<>();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String category = parts[1].trim();
                    double amount = 0.0;
                    try {
                        amount = Double.parseDouble(parts[2].trim());
                    } catch (NumberFormatException ignore) {}
                    categorySum.put(category, categorySum.getOrDefault(category, 0.0) + amount);
                    if (!categoryColor.containsKey(category)) {
                        categoryColor.put(category, generateColorFromName(category));
                    }
                }
            }

            double remaining = appContext.getBalance();

            if (!categorySum.isEmpty()) {
                for (String cat : categorySum.keySet()) {
                    double value = categorySum.get(cat);
                    Color color = categoryColor.getOrDefault(cat, Color.GRAY);
                    pieChart.addData(new ModelPieChart(cat, value, color));
                }

                if (remaining > 0) {
                    pieChart.addData(new ModelPieChart("Remaining", remaining, new Color(70, 130, 180)));
                } else if (remaining < 0) {
                    // ใส่ค่าสัมบูรณ์ให้กราฟ
                    pieChart.addData(new ModelPieChart("Over", Math.abs(remaining), Color.RED));
                }

            } else {
                pieChart.addData(new ModelPieChart("No Data", 1, Color.LIGHT_GRAY));
            }

        } catch (Exception e) {
            e.printStackTrace();
            pieChart.addData(new ModelPieChart("No Data", 1, Color.LIGHT_GRAY));
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

    // เวอร์ชันใหม่: ใช้ threshold จาก balance (ไม่มี daily budget แล้ว)
    private Color findcolor(double remaining) {
        if (remaining <= 0) return Color.RED;
        if (remaining < 500) return Color.YELLOW; // ปรับเกณฑ์ได้ตามที่ต้องการ
        return Color.WHITE;
    }

    private JScrollPane showlist(String filePath) {
        DefaultListModel<String[]> model = new DefaultListModel<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
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
        list.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        list.setCellRenderer((lst, value, index, isSelected, cellHasFocus) -> {
            JPanel row = new JPanel(new BorderLayout());
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

            JPanel infoPanel = new JPanel(new GridLayout(1, 3));
            infoPanel.setOpaque(false);
            infoPanel.add(new JLabel(value[0], SwingConstants.CENTER));
            infoPanel.add(new JLabel(value[1], SwingConstants.CENTER));
            infoPanel.add(new JLabel(value[2], SwingConstants.CENTER));
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

            if (isSelected) row.setBackground(new Color(230, 240, 255));
            else row.setBackground(index % 2 == 0 ? new Color(250, 250, 250) : new Color(235, 235, 235));

            return row;
        });

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
