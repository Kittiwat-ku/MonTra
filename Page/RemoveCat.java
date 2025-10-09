package Page;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;

import javax.swing.*;

import Controller.AppController;
import Service.AppContext;

public class RemoveCat extends JPanel {

    private final JLabel errorLabel;

    public RemoveCat(AppController controller, AppContext appContext) {
        setLayout(null);

        JButton b1 = new JButton("← Back");
        b1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b1.setBounds(0, 0, 100, 30);
        b1.setForeground(Color.BLACK);
        add(b1);

        JComboBox<String> c = province_to_combobox(appContext.getCategoryService().getCategory());
        c.setBounds(57, 150, 250, 50);
        c.setSelectedIndex(-1); // เริ่มต้น: ไม่เลือกอะไร -> getSelectedItem() จะเป็น null
        add(c);

        // error label
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        errorLabel.setBounds(57, 230, 280, 20);
        add(errorLabel);

        JButton b2 = new JButton(" Remove ");
        b2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b2.setBounds(100, 450, 150, 60);
        b2.setForeground(Color.BLACK);
        add(b2);

        b1.addActionListener(e -> controller.showPage("CategoryPath"));

        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object selected = c.getSelectedItem();
                if (selected == null) {
                    showError("Input cannot be null"); // ยังไม่เลือกหมวดหมู่
                    return;
                }
                String cat = selected.toString().trim();
                if (cat.isEmpty()) {
                    showError("Input cannot be null");
                    return;
                }

                try {
                    clearError();
                    appContext.RemoveCat(cat);
                    // รีเฟรชคอมโบหลังลบ (ให้ไปอยู่ที่ไม่เลือกอะไรอีกครั้ง)
                    refreshComboItems(c, appContext);
                    c.setSelectedIndex(-1);
                    // กลับหน้าเดิมถ้าต้องการ:
                    // controller.showPage("CategoryPath");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    showError("Cannot remove category. Please try again.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Unexpected error. Please try again.");
                }
            }
        });

        appContext.addListener(evt -> {
            if ("UpdateCatList".equals(evt.getPropertyName())) {
                refreshComboItems(c, appContext);
                c.setSelectedIndex(-1);
            }
        });
    }

    private void refreshComboItems(JComboBox<String> combo, AppContext appContext) {
        List<String> tmp = appContext.getCategoryService().getCategory();
        combo.removeAllItems();
        for (String s : tmp) {
            combo.addItem(s);
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
    }

    private void clearError() {
        errorLabel.setText("");
    }

    private JComboBox<String> province_to_combobox(List<String> s) {
        JComboBox<String> tmp = new JComboBox<>();
        for (String string : s) tmp.addItem(string);
        return tmp;
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
        Point2D end = new Point2D.Float(w, h);

        float[] dist = { 0.0f, 0.5f, 1.0f };

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
