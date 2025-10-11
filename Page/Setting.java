package Page;

import java.awt.*;
import java.awt.geom.Point2D;
import javax.swing.*;
import ButtonDesign.MenuCardButton;
import ButtonDesign.PillButton;
import Controller.AppController;

public class Setting extends JPanel{

    public Setting(AppController controller){
        setLayout(null);

        JButton b1 = new PillButton("← Back"); 
        b1.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        b1.setBounds(0, 10, 100, 30); 
        ((PillButton) b1).setButtonStyle(PillButton.Style.OUTLINE);
        b1.setForeground(Color.WHITE); 
        add(b1);
        
        JLabel l1 = new JLabel(" Add Income "); 
        l1.setFont(new Font("Segoe UI", Font.BOLD, 30)); 
        l1.setForeground(new Color(255, 255, 224)); 
        l1.setBounds(90, 90, 400, 50); 
        add(l1); 

        JLabel l2 = new JLabel(" Remove Income "); 
        l2.setFont(new Font("Segoe UI", Font.BOLD, 30)); 
        l2.setForeground(new Color(255, 255, 224)); 
        l2.setBounds(55, 390, 400, 50); 
        add(l2); 

        int cardW = 220, cardH = 220, radius = 30;
        int imgW = 160, imgH = 160;

        JButton budget = new MenuCardButton("/image/budget.png", imgW, imgH, radius);
        budget.setBounds(75, 150,  cardW, cardH);
        add(budget);

        JButton removebudget = new MenuCardButton("/image/spend.png", imgW, imgH, radius);
        removebudget.setBounds(75, 450, cardW, cardH);
        add(removebudget);

        b1.addActionListener(e -> controller.showPage("Home")); 
        budget.addActionListener(e -> controller.showPage("Budget"));
        removebudget.addActionListener(e -> controller.showPage("RemoveBud"));

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
