package ButtonDesign;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LabeledInputCard extends JPanel {
    private final Color bg = new Color(0xE9E9EE);
    private final Color textColor = new Color(0x323232);
    private final Color lineIdle  = new Color(0xC0C0C8);
    private final Color lineFocus = new Color(0x8A8AA0);
    private Color currentLine = lineIdle;

    private final Placeholder field;

    public LabeledInputCard(String title, String placeholder) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(textColor);

        field = new Placeholder(placeholder);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setForeground(textColor);
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(8, 0, 8, 0));
        field.setCaretColor(textColor);

        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { currentLine = lineFocus; repaint(); }
            @Override public void focusLost(FocusEvent e)  { currentLine = lineIdle;  repaint(); }
        });

        add(label, BorderLayout.NORTH);
        add(field, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 18, 18));
        g2.setColor(currentLine);
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(8, getHeight() - 2, getWidth() - 8, getHeight() - 2);
        g2.dispose();
    }

    public String getText() { return field.getText(); }
    public void setText(String text) { field.setText(text); }
    public JTextField getTextField() { return field; }
}
