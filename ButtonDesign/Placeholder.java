package ButtonDesign;

import javax.swing.*;
import java.awt.*;

public class Placeholder extends JTextField {
    private String hint;
    private Color hintColor = new Color(0x7A7A85);

    public Placeholder(String hint) { this.hint = hint; }

    public void setHint(String hint) {
        this.hint = hint;
        repaint();
    }

    public void setHintColor(Color color) {
        this.hintColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty() && !isFocusOwner() && hint != null && !hint.isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(hintColor);
            g2.drawString(hint, getInsets().left + 2, getBaseline(getWidth(), getHeight()));
            g2.dispose();
        }
    }
}
