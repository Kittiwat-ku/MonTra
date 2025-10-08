package ButtonDesign;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MenuCardButton extends RoundedButton {

    public MenuCardButton(String resourcePath, int targetW, int targetH, int radius) {
        super(radius);
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255, 153));
        setForeground(Color.DARK_GRAY);
        setBorderColor(Color.GRAY);
        setBorderThickness(1);

        JLabel label = new JLabel(loadScaledIcon(resourcePath, targetW, targetH));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.CENTER);

        getModel().addChangeListener(e -> {
            ButtonModel m = getModel();
            int alpha = m.isPressed() ? 200 : m.isRollover() ? 180 : 153;
            setBackground(new Color(255, 255, 255, alpha));
            repaint();
        });
    }

    private ImageIcon loadScaledIcon(String path, int targetW, int targetH) {
        URL url = getClass().getResource(path);
        if (url == null) return null;

        ImageIcon raw = new ImageIcon(url);
        if (raw.getIconWidth() <= 0 || raw.getIconHeight() <= 0) return null;

        double scale = Math.min(targetW / (double) raw.getIconWidth(),
                                targetH / (double) raw.getIconHeight());

        int newW = Math.max(1, (int) (raw.getIconWidth() * scale));
        int newH = Math.max(1, (int) (raw.getIconHeight() * scale));

        Image scaled = raw.getImage().getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}
