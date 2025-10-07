package Page;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.*;
import ButtonDesign.*;
import Controller.AppController;
import Service.AppContext;

public class Home extends JPanel {

    public Home(AppController controller,AppContext appContext) {
        setLayout(new BorderLayout());
        setOpaque(false);    
        JPanel contentPanel = new JPanel(null);
        contentPanel.setOpaque(false);

        JLabel budgetl1 = new JLabel("฿");
        JLabel budgetl2 = new JLabel(" ");
        JLabel remainl1 = new JLabel("฿");
        JLabel remainl2 = new JLabel(" ");
        try {
            double initBudget = appContext.getCategoryService().getDailyBudget();
            budgetl2.setText(String.format("%,.2f", initBudget));
            remainl2.setText(String.format("%,.2f", initBudget));
        } catch (Exception e) {
            
        }

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

        JLabel totalSpend = new JLabel("Total Spend: 0 ", SwingConstants.CENTER);
        totalSpend.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chartPanel.add(totalSpend, BorderLayout.SOUTH);

        contentPanel.add(chartPanel);

        JLabel list = new JLabel("List:");
        list.setFont(new Font("Segoe UI", Font.BOLD, 40));
        list.setForeground(Color.WHITE);
        list.setBounds(150, 435, 100, 60);
        contentPanel.add(list);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BorderLayout());

        JPanel head = new JPanel(new GridLayout(1, 3));
        head.setBackground(Color.LIGHT_GRAY);
        head.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));
        JLabel h1 = new JLabel("Describtion", SwingConstants.CENTER);
        JLabel h2 = new JLabel("Type", SwingConstants.CENTER);
        JLabel h3 = new JLabel("Price", SwingConstants.CENTER);
        Font hf = new Font("Segoe UI", Font.BOLD, 16);
        h1.setFont(hf); h2.setFont(hf); h3.setFont(hf);
        head.add(h1); head.add(h2); head.add(h3);
        listPanel.add(head, BorderLayout.NORTH);

        JPanel listContent = showlist("null");
        JScrollPane scroll = new JScrollPane(listContent);
        scroll.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        listPanel.add(scroll, BorderLayout.CENTER);
        listPanel.setBounds(50, 500, 280, 180);
        contentPanel.add(listPanel);

        JLabel time = new JLabel();
        time.setFont(new Font("Segoe UI", Font.BOLD, 18));
        time.setForeground(new Color(255, 255, 224));
        time.setHorizontalAlignment(SwingConstants.RIGHT);
        time.setBounds(-25, 150, 300, 40);
        contentPanel.add(time);

        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
            time.setText(sdf.format(new java.util.Date()));});
        timer.start();

        add(contentPanel, BorderLayout.CENTER);

        //Navbar
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        navBar.setOpaque(false); 

        CircleButton homebt = new CircleButton("📊");
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
        homebt.setToolTipText("Summary");
        addbt.setToolTipText("Add");
        morebt.setToolTipText("More");
        
        homebt.addActionListener(e -> controller.showPage("Sumpath"));
        addbt.addActionListener(e -> controller.showPage("Add"));
        morebt.addActionListener(e -> controller.showPage("More"));

        appContext.addListener(evt -> {
        if ("reload".equals(evt.getPropertyName())) {
            remainl2.setText(String.format("%,.2f", appContext.getRemining()));
            budgetl2.setText(String.format("%,.2f", appContext.getCategoryService().getDailyBudget()));
            totalSpend.setText("Total Spend: "+String.format("%,.2f",appContext.getDailyExpense().getSpent()));

            double tmp = (appContext.getRemining() / appContext.getCategoryService().getDailyBudget()) *100; // find percent of remaining
            if (tmp < 50 && tmp > 0) {
                remainl2.setForeground(Color.yellow);
            } else if(tmp <= 0){
                remainl2.setForeground(Color.red);
            }
            
        }
       });

    }
    private JPanel showlist(String filePath) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int index = 1;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String Describtion = parts[0].trim();
                    String type = parts[1].trim();
                    String price = parts[2].trim();

                    JPanel row = new JPanel(new GridLayout(1, 3));
                    row.setBackground(index % 2 == 0 ? new Color(250, 250, 250) : new Color(235, 235, 235));
                    row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

                    JLabel l1 = new JLabel(Describtion, SwingConstants.CENTER);
                    JLabel l2 = new JLabel(type, SwingConstants.CENTER);
                    JLabel l3 = new JLabel(price, SwingConstants.CENTER);

                    Font f = new Font("Segoe UI", Font.PLAIN, 15);
                    l1.setFont(f); l2.setFont(f); l3.setFont(f);

                    row.add(l1);
                    row.add(l2);
                    row.add(l3);

                    panel.add(row);
                    index++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JLabel error = new JLabel(" NO Data use ", SwingConstants.CENTER);
            error.setFont(new Font("Segoe UI", Font.BOLD, 14));
            panel.add(error);
        }

        return panel;
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