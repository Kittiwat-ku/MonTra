package chart;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * คลาสแสดงกราฟวงกลม (Pie Chart) แบบกำหนดเอง
 * - รองรับทั้งแบบเต็มวง และแบบโดนัท
 * - แสดง popup tooltip เมื่อ hover
 * - คลิกเพื่อเลือก slice ได้
 */
public class PieChart extends JComponent {
    
    /**
     * ข้อมูลของกราฟวงกลม
     * -models รายการข้อมูลของกราฟไว้เก็บข้อมูลของ slice แต่ละอัน
     * -format รูปแบบการแสดงตัวเลข
     * -chartType ประเภทของกราฟ (เต็มวง หรือ โดนัท)
     * -selectedIndex ตำแหน่งของ slice ที่ถูกเลือก
     * -hoverIndex ตำแหน่งของ slice ที่ถูก hover
     * -borderHover ระยะขอบเมื่อ hover
     * -padding ระยะห่างรอบกราฟ
     * -popupLabel ป้ายแสดงข้อมูลเมื่อ hover
     */
    private final List<ModelPieChart> models;
    private final DecimalFormat format = new DecimalFormat("#,##0.#");
    private PeiChartType chartType = PeiChartType.DEFAULT;
    private int selectedIndex = -1;
    private int hoverIndex = -1;
    private float borderHover = 0.05f;
    private float padding = 0.2f;
    private JLabel popupLabel;

    // constructor
    public PieChart() {
        models = new ArrayList<>();
        setForeground(new Color(60, 60, 60));
        
        // mouse event
        MouseAdapter mouseEvent = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = checkMouseHover(e.getPoint());
                if (index != hoverIndex) {
                    hoverIndex = index;
                    if (hoverIndex >= 0) {

                        ModelPieChart data = models.get(hoverIndex);
                        showPopup(e.getLocationOnScreen(), data);
                    } else {

                        hidePopup();
                    }
                    repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int index = checkMouseHover(e.getPoint());
                    if (index != -1) {
                        selectedIndex = (index != selectedIndex) ? index : -1;
                        repaint();
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hidePopup();
                hoverIndex = -1;
                repaint();
            }
        };
        addMouseListener(mouseEvent);
        addMouseMotionListener(mouseEvent);

        // popup label
        popupLabel = new JLabel();
        popupLabel.setOpaque(true);
        popupLabel.setBackground(new Color(255, 255, 255, 230));
        popupLabel.setForeground(new Color(40, 40, 40));
        popupLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        popupLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        popupLabel.setVisible(false);
    }
    // วาดกราฟ
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double width = getWidth();
        double height = getHeight();
        double size = Math.min(width, height);
        size -= (size * borderHover) + padding * size;
        double x = (width - size) / 2;
        double y = (height - size) / 2;
        double centerX = width / 2;
        double centerY = height / 2;
        double totalValue = getTotalvalue();
        double drawAngle = 90;

        if (hoverIndex >= 0) {
            g2.setColor(models.get(hoverIndex).getColor());
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2.fill(createShape(hoverIndex, 0, borderHover));
        }

        if (selectedIndex >= 0) {
            g2.setColor(models.get(selectedIndex).getColor());
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2.fill(createShape(selectedIndex, 0.018f, borderHover));
        }

        // วาด slice ของกราฟ
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        for (int i = 0; i < models.size(); i++) {
            ModelPieChart data = models.get(i);
            double angle = data.getValues() * 360 / totalValue;
            Area area = new Area(new Arc2D.Double(x, y, size, size, drawAngle, -angle, Arc2D.PIE));
            if (chartType == PeiChartType.DONUT_CHART) {
                double s1 = size * 0.5f;
                double x1 = (width - s1) / 2;
                double y1 = (height - s1) / 2;
                area.subtract(new Area(new Ellipse2D.Double(x1, y1, s1, s1)));
            }
            g2.setColor(data.getColor());
            g2.fill(area);
            g2.setColor(Color.WHITE);
            g2.draw(area);
            drawAngle -= angle;
        }

        // วาดข้อความเปอร์เซ็นต์บน slice
        drawAngle = 90;
        for (int i = 0; i < models.size(); i++) {
            ModelPieChart data = models.get(i);
            double angle = data.getValues() * 360 / totalValue;
            double textSize = size / 2 * 0.75f;
            double textAngle = -(drawAngle - angle / 2);
            double cosX = Math.cos(Math.toRadians(textAngle));
            double sinY = Math.sin(Math.toRadians(textAngle));
            String text = getPercentage(data.getValues()) + "%";
            g2.setFont(getFont().deriveFont((float) (getFont().getSize() * size * 0.0045f)));
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D r = fm.getStringBounds(text, g2);
            double textX = centerX + cosX * textSize - r.getWidth() / 2;
            double textY = centerY + sinY * textSize + fm.getAscent() / 2;
            g2.setColor(Color.WHITE);
            g2.drawString(text, (float) textX, (float) textY);
            drawAngle -= angle;
        }

        g2.dispose();
        super.paintComponent(g);
    }

    // แสดง popup เมื่อ hover
    private void showPopup(Point screenPoint, ModelPieChart data) {
        SwingUtilities.invokeLater(() -> {
            if (popupLabel.getParent() == null) {
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window instanceof JFrame frame) {
                    JComponent glass = (JComponent) frame.getGlassPane();
                    glass.setLayout(null);
                    glass.add(popupLabel);
                    glass.setVisible(true);
                }
            }

            String text = "<html><b>" + data.getName() + "</b><br>" +
                          format.format(data.getValues()) + "</html>";
            popupLabel.setText(text);

            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                Point glassPoint = new Point(screenPoint);
                SwingUtilities.convertPointFromScreen(glassPoint, ((JFrame) window).getGlassPane());
                popupLabel.setSize(popupLabel.getPreferredSize());
                popupLabel.setLocation(glassPoint.x + 12, glassPoint.y - popupLabel.getHeight() - 6);
                popupLabel.setVisible(true);
                ((JFrame) window).getGlassPane().repaint();
            }
        });
    }

    private void hidePopup() {
        SwingUtilities.invokeLater(() -> popupLabel.setVisible(false));
    }
    
    // สร้างรูปร่างของ slice ที่เลือกเมื่อ hover หรือ selected
    private Shape createShape(int index, float a, float p) {
        Shape shape = null;
        double width = getWidth();
        double height = getHeight();
        double size = Math.min(width, height);
        size -= (size * a) + (padding * size);
        double x = (width - size) / 2;
        double y = (height - size) / 2;
        double totalValue = getTotalvalue();
        double drawAngle = 90;
        for (int i = 0; i < models.size(); i++) {
            double angle = models.get(i).getValues() * 360 / totalValue;
            if (index == i) {
                Area area = new Area(new Arc2D.Double(x, y, size, size, drawAngle, -angle, Arc2D.PIE));
                size -= size * p - size * a * 2;
                x = (width - size) / 2;
                y = (height - size) / 2;
                area.subtract(new Area(new Arc2D.Double(x, y, size, size, drawAngle, -angle, Arc2D.PIE)));
                shape = area;
                break;
            }
            drawAngle -= angle;
        }
        return shape;
    }

    // คำนวณเปอร์เซ็นต์ของ slice
    private String getPercentage(double value) {
        double total = getTotalvalue();
        return format.format(value * 100 / total);
    }

    // ตรวจสอบตำแหน่งเมาส์ว่าอยู่บน slice ไหน
    private int checkMouseHover(Point point) {
        int index = -1;
        double width = getWidth();
        double height = getHeight();
        double size = Math.min(width, height);
        size -= (size * borderHover) + padding * size;
        double x = (width - size) / 2;
        double y = (height - size) / 2;
        double totalValue = getTotalvalue();
        double drawAngle = 90;
        for (int i = 0; i < models.size(); i++) {
            ModelPieChart data = models.get(i);
            double angle = data.getValues() * 360 / totalValue;
            Area area = new Area(new Arc2D.Double(x, y, size, size, drawAngle, -angle, Arc2D.PIE));
            if (chartType == PeiChartType.DONUT_CHART) {
                double s1 = size * 0.5f;
                double x1 = (width - s1) / 2;
                double y1 = (height - s1) / 2;
                area.subtract(new Area(new Ellipse2D.Double(x1, y1, s1, s1)));
            }
            if (area.contains(point)) {
                index = i;
                break;
            }
            drawAngle -= angle;
        }
        return index;
    }

    // คำนวณค่ารวมของข้อมูลทั้งหมด
    private double getTotalvalue() {
        double max = 0;
        for (ModelPieChart data : models) {
            max += data.getValues();
        }
        return max;
    }

    /**
     * Getter และ Setter Methods
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        if (selectedIndex >= -1 && selectedIndex < models.size()) {
            this.selectedIndex = selectedIndex;
            repaint();
        }
    }

    public int getHoverIndex() {
        return hoverIndex;
    }

    public float getBorderHover() {
        return borderHover;
    }

    public void setBorderHover(float borderHover) {
        this.borderHover = borderHover;
        repaint();
    }

    public float getPadding() {
        return padding;
    }

    public void setPadding(float padding) {
        this.padding = padding;
        repaint();
    }

    public PeiChartType getChartType() {
        return chartType;
    }

    public void setChartType(PeiChartType chartType) {
        this.chartType = chartType;
        repaint();
    }

    public void clearData() {
        models.clear();
        repaint();
    }

    public void addData(ModelPieChart data) {
        models.add(data);
    }

    public static enum PeiChartType {
        DEFAULT, DONUT_CHART
    }
}
