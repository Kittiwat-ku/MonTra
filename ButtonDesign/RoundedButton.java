package ButtonDesign;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {
    private int radius;
    private Color borderColor = Color.GRAY;
    private int borderThickness = 1;

    public RoundedButton(int radius) {
        this.radius = radius;
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setRolloverEnabled(true);
        setMargin(new Insets(0, 0, 0, 0));
    }

    public void setBorderColor(Color c) { borderColor = c; repaint(); }
    public void setBorderThickness(int t) { borderThickness = t; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillRoundRect(3, 4, w - 6, h - 6, radius, radius);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);

        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(borderThickness));
        g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }
}
