package Page;

import java.awt.*;
import java.awt.geom.Point2D;
import javax.swing.*;
import ButtonDesign.LabeledInputCard;
import Controller.AppController;

public class Budget extends JPanel{
    
    public Budget(AppController controller){
        setLayout( null);

        JButton b1 = new JButton("← Back"); 
        b1.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        b1.setBounds(0, 0, 100, 30); 
        b1.setForeground(Color.BLACK); 
        add(b1);
        
        LabeledInputCard description = new LabeledInputCard("Budget", "Set Budget");
        description.setBounds(30, 200, 300, 100);
        add(description);

        JButton b2 = new JButton(" Comfirm ");
        b2.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        b2.setBounds(100, 400, 150, 60); 
        b2.setForeground(Color.BLACK); 
        add(b2); 

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
