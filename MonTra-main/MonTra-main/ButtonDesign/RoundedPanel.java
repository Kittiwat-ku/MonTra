package ButtonDesign;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedPanel extends JPanel {
    private int arcWidth;
    private int arcHeight;
    private Color backgroundColor;
    private Color borderColor;
    private int borderThickness;

    public RoundedPanel(int arcWidth, int arcHeight, Color backgroundColor, Color borderColor, int borderThickness) {
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        this.backgroundColor = backgroundColor == null ? getBackground() : backgroundColor;
        this.borderColor = borderColor;
        this.borderThickness = borderThickness;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, w, h, arcWidth, arcHeight);

        if (borderColor != null && borderThickness > 0) {
            g2.setStroke(new BasicStroke(borderThickness));
            g2.setColor(borderColor);
            int half = borderThickness / 2;
            g2.drawRoundRect(half, half, w - borderThickness, h - borderThickness, arcWidth, arcHeight);
        }

        Shape clip = new RoundRectangle2D.Float(0, 0, w, h, arcWidth, arcHeight);
        g2.setClip(clip);

        super.paintComponent(g2);
        g2.dispose();
    }
}
