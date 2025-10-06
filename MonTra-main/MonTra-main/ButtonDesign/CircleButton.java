package ButtonDesign; 

import javax.swing.*; 
import java.awt.*; 

public class CircleButton extends JButton { public CircleButton(String text) { 
    super(text); 
    setFocusPainted(false); 
    setContentAreaFilled(false); 
    setBorderPainted(false); 
    setOpaque(false); 
    setForeground(Color.DARK_GRAY); 
} 
    public void setBackgroundColor(Color color) { 
        repaint(); 
    } 
    @Override 
    protected void paintComponent(Graphics g) { 
        super.paintComponent(g); 
        int size = Math.min(getWidth(), getHeight()); 
        Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
        
        GradientPaint gp = new GradientPaint( 0, 0, Color.WHITE, 0, size, Color.GRAY); 
        g2.setPaint(gp); 
        g2.fillOval(0, 0, size, size); 
        
        FontMetrics fm = g2.getFontMetrics(); 
        int textWidth = fm.stringWidth(getText()); 
        int textHeight = fm.getAscent(); g2.setColor(getForeground()); 
        g2.drawString(getText(), (size - textWidth) / 2, (size + textHeight) / 2 - 3); 
        g2.dispose(); 
    } 
        @Override protected void paintBorder(Graphics g) { 
        } 
}
