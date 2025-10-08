package ButtonDesign;

import javax.swing.*;
import java.awt.*;

public class PillButton extends JButton {

    public enum Style { FILLED, OUTLINE, HYBRID }

    private boolean hovering = false;
    private Style style = Style.FILLED;

    public PillButton(String text) {
        super(text);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { hovering = true; repaint(); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { hovering = false; repaint(); }
        });
    }

    public void setButtonStyle(Style s) { style = s; repaint(); }
    public Style getButtonStyle() { return style; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int arc = (int) (h * 1.2);
        float scale = hovering ? 0.93f : 1f;
        int scaledW = (int) (w * scale), scaledH = (int) (h * scale);
        int x = (w - scaledW) / 2, y = (h - scaledH) / 2 + (hovering ? 2 : 0);

        Color base = getBackground();
        Color highlight = blend(base, Color.WHITE, 0.4f);
        Color shadow = blend(base, Color.BLACK, 0.25f);

        if (style != Style.OUTLINE) {
            g2.setColor(new Color(0, 0, 0, 45));
            g2.fillRoundRect(x + 3, y + 4, scaledW - 6, scaledH - 4, arc, arc);
            g2.setPaint(new GradientPaint(0, y, highlight, 0, y + scaledH, shadow));
            g2.fillRoundRect(x, y, scaledW, scaledH, arc, arc);
            g2.setColor(new Color(255, 255, 255, hovering ? 90 : 50));
            g2.fillRoundRect(x + 1, y + 1, scaledW - 3, scaledH - 3, arc, arc);
        }

        if (style != Style.FILLED) {
            g2.setStroke(new BasicStroke(2.3f));
            Color border = (style == Style.HYBRID)
                    ? (hovering ? new Color(255, 244, 204) : new Color(184, 134, 11))
                    : (hovering ? base : highlight);
            g2.setColor(border);
            g2.drawRoundRect(x + 1, y + 1, scaledW - 3, scaledH - 3, arc, arc);
        }

        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(getText())) / 2;
        int ty = (h + fm.getAscent()) / 2 - 2;
        g2.setColor(getForeground());
        g2.drawString(getText(), tx, ty);

        g2.dispose();
    }

    private Color blend(Color c1, Color c2, float ratio) {
        ratio = Math.max(0, Math.min(1, ratio));
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * ratio);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * ratio);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * ratio);
        return new Color(r, g, b);
    }
}
