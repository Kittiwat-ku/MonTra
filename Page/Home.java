package Page;

import java.awt.*;
import java.awt.geom.Point2D;
import javax.swing.*;
import ButtonDesign.*;
import Controller.AppController;

public class Home extends JPanel {

    public Home(AppController controller) {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel contentPanel = new JPanel(null);
        contentPanel.setOpaque(false);

        JLabel budgetl1 = new JLabel("฿");
        JLabel budgetl2 = new JLabel(" ");
        JLabel budgetl3 = new JLabel("Budget");
        budgetl1.setFont(new Font("Segoe UI", Font.BOLD, 30));
        budgetl1.setForeground(Color.WHITE);
        budgetl1.setBounds(20, 40, 200, 60);

        budgetl2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        budgetl2.setForeground(Color.WHITE);
        budgetl2.setBounds(50, 40, 200, 60);
        
        budgetl3.setFont(new Font("Segoe UI", Font.BOLD, 16));
        budgetl3.setForeground(Color.WHITE);
        budgetl3.setBounds(20, 100, 150, 30);
        contentPanel.add(budgetl1);
        contentPanel.add(budgetl2);
        contentPanel.add(budgetl3);

        JLabel remainl1 = new JLabel("฿");
        JLabel remainl2 = new JLabel(" ");
        JLabel remainl3 = new JLabel("Remain");
        remainl1.setFont(new Font("Segoe UI", Font.BOLD, 30));
        remainl1.setForeground(Color.WHITE);
        remainl1.setBounds(250, 40, 200, 60);

        remainl2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        remainl2.setForeground(Color.WHITE);
        remainl2.setBounds(270, 40, 200, 60);
        
        remainl3.setFont(new Font("Segoe UI", Font.BOLD, 16));
        remainl3.setForeground(Color.WHITE);
        remainl3.setBounds(285, 100, 150, 30);
        contentPanel.add(remainl1);
        contentPanel.add(remainl2);
        contentPanel.add(remainl3);

        JSeparator line1 = new JSeparator(SwingConstants.HORIZONTAL);
        line1.setForeground(Color.WHITE);
        line1.setBounds(15, 100, 122, 5);
        contentPanel.add(line1);

        JSeparator line2 = new JSeparator(SwingConstants.HORIZONTAL);
        line2.setForeground(Color.WHITE);
        line2.setBounds(240, 100, 150, 5);
        contentPanel.add(line2);
        

        RoundedPanel chartPanel = new RoundedPanel(30, 30, new Color(255, 255, 255, 153), Color.GRAY, 1);
        chartPanel.setLayout(new BorderLayout());
        chartPanel.setBounds(75, 200, 220, 220);

        JLabel totalSpend = new JLabel("Total Spend: ", SwingConstants.CENTER);
        totalSpend.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chartPanel.add(totalSpend, BorderLayout.SOUTH);

        contentPanel.add(chartPanel);

        JLabel list = new JLabel("List:");
        list.setFont(new Font("Segoe UI", Font.BOLD, 40));
        list.setForeground(Color.WHITE);
        list.setBounds(150, 435, 100, 60);
        contentPanel.add(list);

        JPanel listContent = new JPanel();
        listContent.setLayout(new BoxLayout(listContent, BoxLayout.Y_AXIS));
        listContent.setBackground(new Color(255, 255, 255));
        for (int i = 1; i <= 10; i++) {
            JLabel itemLabel = new JLabel("Tinoy " + i);
            itemLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            listContent.add(itemLabel);
        }

        JScrollPane listPanel = new JScrollPane(listContent);
        listPanel.setBounds(75, 500, 220, 120);
        contentPanel.add(listPanel);

        add(contentPanel, BorderLayout.CENTER);

        //Navbar
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        navBar.setOpaque(false); 

        CircleButton homebt = new CircleButton("🏠");
        homebt.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 23));
        homebt.setPreferredSize(new Dimension(50,50));

        CircleButton morebt = new CircleButton("...");
        morebt.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 23));
        morebt.setPreferredSize(new Dimension(50,50));

        CircleButton addbt = new CircleButton("➕");
        addbt.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 35));
        addbt.setPreferredSize(new Dimension(80,80));

        navBar.add(homebt);
        navBar.add(addbt);
        navBar.add(morebt);
        
        add(navBar, BorderLayout.SOUTH);

        //Action
        homebt.setToolTipText("Home");
        addbt.setToolTipText("Add");
        morebt.setToolTipText("More");
        
        homebt.addActionListener(e -> controller.showPage("Home"));
        addbt.addActionListener(e -> controller.showPage("Add"));
        morebt.addActionListener(e -> controller.showPage("More"));

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
