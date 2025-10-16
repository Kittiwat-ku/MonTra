package Page;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import javax.swing.*;

import Controller.AppController;
import ButtonDesign.*;

/**
 * More page
 */
public class More extends JPanel {

    public More(AppController controller) {
        setLayout(null);

        /**
         * Components
         * - Back Button
         * - Category Card
         * - Export Card
         */
        JButton b1 = new PillButton("← Back"); 
        b1.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        b1.setBounds(0, 10, 100, 30); 
        ((PillButton) b1).setButtonStyle(PillButton.Style.OUTLINE);
        b1.setForeground(Color.WHITE); 
        add(b1);

        JLabel l2 = new JLabel(" Category "); 
        l2.setFont(new Font("Segoe UI", Font.BOLD, 30)); 
        l2.setForeground(new Color(255, 255, 224)); 
        l2.setBounds(125, 90, 400, 50); 
        add(l2);
        
        JLabel l3 = new JLabel(" Export "); 
        l3.setFont(new Font("Segoe UI", Font.BOLD, 30)); 
        l3.setForeground(new Color(255, 255, 224)); 
        l3.setBounds(125, 390, 400, 50); 
        add(l3);

        int cardW = 220, cardH = 220, radius = 30;
        int imgW = 160, imgH = 160;

        JButton setbt = new MenuCard("/image/cat.png", imgW, imgH, radius);
        setbt.setBounds(75, 150, cardW, cardH);
        add(setbt);

        JButton exbt  = new MenuCard("/image/ex.png", imgW, imgH, radius);
        exbt.setBounds(75, 450, cardW, cardH);
        add(exbt);

        // Actions
        b1.setToolTipText("Back");
        setbt.setToolTipText("Category");
        exbt.setToolTipText("Export");
        b1.addActionListener(e -> controller.showPage("Home"));
        setbt.addActionListener(e -> controller.showPage("CategoryPath"));
        exbt.addActionListener(e -> controller.showPage("Ep"));
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

