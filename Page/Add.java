package Page;

import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import ButtonDesign.*;
import Controller.AppController;
import Service.AppContext;

public class Add extends JPanel {

    JLabel errorLabel;

    public Add(AppController controller, AppContext appContext) {
        setLayout(null);

        JLabel l1 = new JLabel(" Add Your Transaction ");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 30));
        l1.setForeground(new Color(255, 255, 224));
        l1.setBounds(20, 100, 400, 50);
        add(l1);

        JButton b1 = new PillButton("← Back");
        b1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b1.setBounds(0, 10, 100, 30);
        ((PillButton) b1).setButtonStyle(PillButton.Style.OUTLINE);
        b1.setForeground(Color.WHITE);
        add(b1);

        LabeledInputCard description = new LabeledInputCard("Description", "Example: Tinoy, Shabu");
        description.setBounds(30, 200, 300, 100);
        add(description);

        LabeledInputCard amount = new LabeledInputCard("Amount", "Example: 500, 1000");
        amount.setBounds(30, 350, 300, 100);
        add(amount);

        JComboBox<String> c = province_to_combobox(appContext.getCategoryService().getCategory());
        c.setBounds(57, 500, 250, 50);
        // ให้เริ่มต้น "ไม่เลือกอะไร" เพื่อให้ตรวจ null ได้จริง
        c.setSelectedIndex(-1);
        add(c);

        PillButton b2 = new PillButton(" Comfirm ");
        b2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b2.setBounds(100, 600, 175, 60);
        ((PillButton) b2).setButtonStyle(PillButton.Style.HYBRID);
        b2.setForeground(Color.BLACK);
        add(b2);

        // ----- error label -----
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        errorLabel.setBounds(30, 465, 300, 20);
        add(errorLabel);

        // Action
        b1.addActionListener(e -> controller.showPage("Home"));

        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String rawAmount = amount.getText();
                String rawDesc   = description.getText();

                if (rawAmount == null) rawAmount = "";
                if (rawDesc == null) rawDesc = "";

                rawAmount = rawAmount.replace(",", "").trim();
                rawDesc   = rawDesc.replace(",", "").trim();

                if (rawAmount.isEmpty() || rawDesc.isEmpty()) {
                    showError("Input cannot be empty");
                    return;
                }

                //  check category จาก JComboBox: ถ้าไม่เลือก = null
                Object selected = c.getSelectedItem();
                if (selected == null) {
                    showError("Input cannot be null");
                    return;
                }
                String category = selected.toString().trim();
                if (category.isEmpty()) {
                    showError("Input cannot be null");
                    return;
                }

                try {
                    double num = Double.parseDouble(rawAmount);
                    if (num <= 0) {
                        showError("Amount Cannot be 0 and negative");
                        return;
                    }

                    // ok
                    appContext.addExpense(rawDesc, num, category);
                    clearError();

                    // clear
                    description.setText("");
                    amount.setText("");
                    c.setSelectedIndex(-1);

                    controller.showPage("Home");

                } catch (NumberFormatException ex) {
                    showError("Input must be all number");
                } catch (NullPointerException ex) {
                    showError("Input cannot be null");
                } catch (Exception ex) {
                    showError("Other error");
                    ex.printStackTrace();
                }
            }
        });

        appContext.addListener(evt -> {
            if ("UpdateCatList".equals(evt.getPropertyName())) {
                List<String> tmp = appContext.getCategoryService().getCategory();
                c.removeAllItems();
                for (String string : tmp) {
                    c.addItem(string);
                }
                c.setSelectedIndex(-1);
            }
        });
    }

    private void clearError() {
        errorLabel.setText("");
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
    }

    private JComboBox<String> province_to_combobox(List<String> s) {
        JComboBox<String> tmp = new JComboBox<>();
        for (String string : s) {
            tmp.addItem(string);
        }
        return tmp;
    }

    // Background Color
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
