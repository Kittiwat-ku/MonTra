package Page;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

import Controller.AppController;
import ButtonDesign.*;

public class More extends JPanel {

    public More(AppController controller){
        setLayout(null);

        JButton l1 = new JButton("← Back");
        l1.setBounds(0, 0, 100, 30);
        l1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l1.setForeground(Color.BLACK);
        add(l1);

        int cardW = 220, cardH = 220, radius = 30;
        int imgW = 160, imgH = 160;

        JButton sumbt = new MenuCardButton("/image/sum.png", imgW, imgH, radius);
        sumbt.setBounds(75, 50,  cardW, cardH);
        add(sumbt);

        JButton setbt = new MenuCardButton("/image/set.png", imgW, imgH, radius);
        setbt.setBounds(75, 300, cardW, cardH);
        add(setbt);

        JButton exbt  = new MenuCardButton("/image/ex.png",  imgW, imgH, radius);
        exbt.setBounds(75, 550, cardW, cardH);
        add(exbt);

        // Action
        sumbt.setToolTipText("Summary");
        setbt.setToolTipText("Setting");
        exbt.setToolTipText("Export");

        l1.addActionListener(e -> controller.showPage("Home"));
        sumbt.addActionListener(e -> controller.showPage("Sumpath"));
        setbt.addActionListener(e -> controller.showPage("Setting"));
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
