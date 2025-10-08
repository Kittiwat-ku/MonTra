package ButtonDesign;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedPanel extends JPanel {
    private int arcW, arcH, borderThickness;
    private Color bgColor, borderColor;

    public RoundedPanel(int arcW, int arcH, Color bgColor, Color borderColor, int borderThickness) {
        this.arcW = arcW;
        this.arcH = arcH;
        this.bgColor = (bgColor != null) ? bgColor : getBackground();
        this.borderColor = borderColor;
        this.borderThickness = borderThickness;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int half = borderThickness / 2;

        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, w, h, arcW, arcH);

        if (borderColor != null && borderThickness > 0) {
            g2.setStroke(new BasicStroke(borderThickness));
            g2.setColor(borderColor);
            g2.drawRoundRect(half, half, w - borderThickness, h - borderThickness, arcW, arcH);
        }

        g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, arcW, arcH));

        super.paintComponent(g2);
        g2.dispose();
    }
}
