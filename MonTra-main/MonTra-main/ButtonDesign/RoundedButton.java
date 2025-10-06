package ButtonDesign;

import java.awt.*;
import javax.swing.*;

public class RoundedButton extends JButton {
    private int radius;
    private Color borderColor = Color.GRAY;
    private int borderThickness = 1;

    public RoundedButton(int radius) {
        this.radius = radius;
        setMargin(new Insets(0,0,0,0));
        setFocusable(true);
        setRolloverEnabled(true);
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    public void setBorderColor(Color c) { this.borderColor = c; }
    public void setBorderThickness(int t) { this.borderThickness = t; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        g2.setComposite(AlphaComposite.SrcOver.derive(0.15f));
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(4, 6, w - 8, h - 8, radius, radius);

        g2.setComposite(AlphaComposite.SrcOver);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);

        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(borderThickness));
        g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }
}