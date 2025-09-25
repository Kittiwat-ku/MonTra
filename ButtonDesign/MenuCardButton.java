package ButtonDesign;

import java.awt.*;
import java.net.URL;
import javax.swing.*;

public class MenuCardButton extends RoundedButton {

    public MenuCardButton(String resourcePath, int targetW, int targetH, int radius) {
        super(radius);

        setBackground(new Color(255, 255, 255, 153));
        setForeground(Color.DARK_GRAY);
        setBorderColor(Color.GRAY);
        setBorderThickness(1);
        setLayout(new BorderLayout());

        ImageIcon icon = loadIconScaledToFit(resourcePath, targetW, targetH);
        JLabel label = (icon != null) ? new JLabel(icon) : new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.CENTER);

        getModel().addChangeListener(e -> {
            ButtonModel m = getModel();
            if (m.isPressed())      setBackground(new Color(255, 255, 255, 200));
            else if (m.isRollover()) setBackground(new Color(255, 255, 255, 180));
            else                      setBackground(new Color(255, 255, 255, 153));
            repaint();
        });
    }

    private ImageIcon loadIconScaledToFit(String resourcePath, int targetW, int targetH) {
        URL url = MenuCardButton.class.getResource(resourcePath);
        if (url == null) return null;

        ImageIcon raw = new ImageIcon(url);
        if (raw.getIconWidth() <= 0 || raw.getIconHeight() <= 0) return null;

        double sx = targetW / (double) raw.getIconWidth();
        double sy = targetH / (double) raw.getIconHeight();
        double s  = Math.min(sx, sy);

        int newW = Math.max(1, (int)Math.round(raw.getIconWidth()  * s));
        int newH = Math.max(1, (int)Math.round(raw.getIconHeight() * s));

        Image scaled = raw.getImage().getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}