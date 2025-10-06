package ButtonDesign;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;

public class LabeledInputCard extends JPanel {
    private final Color bg = new Color(0xE9,0xE9,0xEE);
    private final Color textColor = new Color(0x32,0x32,0x32);
    private final Color lineIdle  = new Color(0xC0,0xC0,0xC8);
    private final Color lineFocus = new Color(0x8A,0x8A,0xA0);
    private Color currentLine = lineIdle;
    private final int arc = 18;

    private final Placeholder field;

    public LabeledInputCard(String title, String placeholder){
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16,20,16,20));

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(textColor);

        field = new Placeholder(placeholder);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setForeground(textColor);
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(8,0,8,0));
        field.setCaretColor(textColor);

        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e){ currentLine = lineFocus; repaint(); }
            @Override public void focusLost(FocusEvent e){ currentLine = lineIdle;  repaint(); }
        });

        add(label, BorderLayout.NORTH);
        add(field, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),arc,arc));
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(currentLine);
        g2.drawLine(8, getHeight()-2, getWidth()-8, getHeight()-2);
        g2.dispose();
    }

    public JTextField getTextField(){ return field; }
    public String getText(){ return field.getText(); }
    public void setText(String t){ field.setText(t); }
}