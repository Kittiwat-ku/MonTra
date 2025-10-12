package Page;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Month;

import javax.swing.*;
import ButtonDesign.PillButton;
import ButtonDesign.RoundedPanel;
import Controller.AppController;
import Service.AppContext;
import chart.ModelPieChart;
import chart.PieChart;

public class Summary extends JPanel{
    private final AppContext appContext;
    private RoundedPanel chartPanel;

    public Summary(AppController controller, AppContext appContext) {
        this.appContext = appContext;
        setLayout(null);

        JButton b1 = new PillButton("← Back"); 
        b1.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        b1.setBounds(0, 10, 100, 30); 
        ((PillButton) b1).setButtonStyle(PillButton.Style.OUTLINE);
        b1.setForeground(Color.WHITE); 
        add(b1);

        JLabel TotalSpend = new JLabel("0", SwingConstants.LEFT);
        JLabel Income = new JLabel("0", SwingConstants.LEFT);
        JLabel Save = new JLabel("0", SwingConstants.LEFT);
        JLabel Remain = new JLabel("0", SwingConstants.LEFT);

        TotalSpend.setFont(new Font("Times New Roman",Font.PLAIN,16));
        TotalSpend.setForeground(Color.RED);
        TotalSpend.setBounds(120, 476, 200, 30);
        Income.setFont(new Font("Times New Roman",Font.PLAIN,16));
        Income.setForeground(Color.YELLOW);
        Income.setBounds(120, 551, 200, 30);
        Save.setFont(new Font("Times New Roman",Font.PLAIN,16));
        Save.setForeground(Color.GREEN);
        Save.setBounds(120, 626, 200, 30);
        Remain.setFont(new Font("Times New Roman",Font.PLAIN,16));
        Remain.setForeground(Color.WHITE);
        Remain.setBounds(120, 701, 200, 30);
        add(TotalSpend);
        add(Income);
        add(Save);
        add(Remain);

        JLabel l1 = new JLabel("Select Month:",SwingConstants.LEFT);
        l1.setFont(new Font("Times New Roman",Font.PLAIN,20));
        l1.setForeground(Color.WHITE);
        l1.setBounds(65, 400, 200, 30);
        JLabel l2 = new JLabel("Total Spend:",SwingConstants.LEFT);
        l2.setFont(new Font("Times New Roman",Font.PLAIN,20));
        l2.setForeground(Color.WHITE);
        l2.setBounds(10, 475, 200, 30);
        JLabel l3 = new JLabel("Income:",SwingConstants.LEFT);
        l3.setFont(new Font("Times New Roman",Font.PLAIN,20));
        l3.setForeground(Color.WHITE);
        l3.setBounds(10, 550, 200, 30);
        JLabel l4 = new JLabel("Save:",SwingConstants.LEFT);
        l4.setFont(new Font("Times New Roman",Font.PLAIN,20));
        l4.setForeground(Color.WHITE);
        l4.setBounds(10, 625, 200, 30);
        JLabel l5 = new JLabel("Transection:",SwingConstants.LEFT);
        l5.setFont(new Font("Times New Roman",Font.PLAIN,20));
        l5.setForeground(Color.WHITE);
        l5.setBounds(10, 700, 200, 30);
        add(l1);
        add(l2);
        add(l3);
        add(l4);
        add(l5);

        JSeparator line1 = new JSeparator(SwingConstants.HORIZONTAL);
        line1.setBounds(0, 510, 300, 5);        
        JSeparator line2 = new JSeparator(SwingConstants.HORIZONTAL);
        line2.setBounds(0, 585, 300, 5);       
        JSeparator line3 = new JSeparator(SwingConstants.HORIZONTAL);
        line3.setBounds(0, 660, 300, 5);
        JSeparator line4 = new JSeparator(SwingConstants.HORIZONTAL);
        line4.setBounds(0, 735, 300, 5);
        add(line1);
        add(line2);
        add(line3);
        add(line4);
        
        RoundedPanel MPie = new RoundedPanel(30, 30, new Color(255, 255, 255, 153), Color.GRAY, 1);
        MPie.setBounds(30, 100, 300, 300);
        MPie.setLayout(new BorderLayout());
        add(MPie);
        
        JComboBox<String> month = new JComboBox<>();
        for (Month m : Month.values()) {
            month.addItem(m.name());
        }
        month.setBounds(180, 402, 100, 30);
        add(month);

        b1.addActionListener(e -> controller.showPage("Home")); 
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
        Point2D end   = new Point2D.Float(w, h);

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
