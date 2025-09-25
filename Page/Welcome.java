package Page;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.Point2D;
import java.util.Map;

import Controller.*;

public class Welcome extends JPanel {

    @SuppressWarnings("unchecked")
    public Welcome(AppController controller) {
        setLayout(new BorderLayout());

        ImageIcon icon = new ImageIcon("image/logo.png");
        JLabel logoLabel = new JLabel(icon, SwingConstants.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int newW = getWidth() / 2;
                int newH = getHeight() / 4;
                Image scaled = icon.getImage().getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(scaled));
            }
        });

        JLabel l1 = new JLabel(" MonTra ", SwingConstants.CENTER);
        l1.setFont(new Font("Courier New", Font.BOLD, 60));
        l1.setForeground(new Color(255, 255, 224));

        JLabel l3 = new JLabel(" click anywhere to continue", SwingConstants.CENTER);
        Font font3 = new Font("Arial", Font.PLAIN, 15);
        @SuppressWarnings("rawtypes")
        Map attributes3 = font3.getAttributes();
        attributes3.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        l3.setFont(font3.deriveFont(attributes3));
        l3.setForeground(Color.GRAY);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        GridBagConstraints grid = new GridBagConstraints();
        grid.gridx = 0;
        grid.anchor = GridBagConstraints.CENTER;

        grid.gridy = 0;
        grid.insets = new Insets(-50, 0, 100, 0);
        centerPanel.add(logoLabel, grid);

        grid.gridy = 1;
        grid.insets = new Insets(-125, 0, 10, 0);
        centerPanel.add(l1, grid);

        add(centerPanel, BorderLayout.CENTER);
        add(l3, BorderLayout.SOUTH);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.showPage("Home");
            }
        });
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        Point2D start = new Point2D.Float(0, 0);
        Point2D end   = new Point2D.Float(w, h);

        float[] dist = {0.0f, 0.5f, 1.0f};

        Color[] colors = {
            new Color(0x4A5C58),
            new Color(0x0A5C36),
            new Color(0x1F2C2E)
        };

        LinearGradientPaint lgp = new LinearGradientPaint(start, end, dist, colors);
        g2d.setPaint(lgp);
        g2d.fillRect(0, 0, w, h);
    }
}