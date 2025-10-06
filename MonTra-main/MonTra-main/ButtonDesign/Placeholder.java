package ButtonDesign;

import java.awt.*;
import javax.swing.*;

public class Placeholder extends JTextField {
    private String hint;
    private Color hintColor = new Color(0x7A,0x7A,0x85);

    public Placeholder(String hint){ this.hint = hint; }
    public void setHint(String h){ this.hint = h; repaint(); }
    public void setHintColor(Color c){ this.hintColor = c; repaint(); }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if (getText().isEmpty() && !isFocusOwner() && hint != null && !hint.isEmpty()){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = getInsets();
            FontMetrics fm = g2.getFontMetrics(getFont());
            int y = ins.top + fm.getAscent();
            g2.setColor(hintColor);
            g2.drawString(hint, ins.left + 2, y);
            g2.dispose();
        }
    }
}
