package Page;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.*;
import ButtonDesign.LabeledInputCard;
import ButtonDesign.PillButton;
import Controller.AppController;

import Service.AppContext;

public class Budget extends JPanel {

    JLabel errorLabel;
    LabeledInputCard description;

    public Budget(AppController controller, AppContext appContext) {
        setLayout(null);

        JButton b1 = new PillButton("← Back");
        b1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b1.setBounds(0, 10, 100, 30);
        ((PillButton) b1).setButtonStyle(PillButton.Style.OUTLINE);
        b1.setForeground(Color.WHITE);
        add(b1);

        // ----- error label -----
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        errorLabel.setBounds(30, 320, 300, 20);
        add(errorLabel);

        description = new LabeledInputCard("Budget", "Set Budget");
        description.setBounds(30, 200, 300, 100);
        add(description);

        PillButton b2 = new PillButton(" Comfirm ");
        b2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b2.setBounds(100, 450, 175, 60);
        ((PillButton) b2).setButtonStyle(PillButton.Style.HYBRID);
        b2.setForeground(Color.BLACK);
        add(b2);

        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
                clearError();
                controller.showPage("Setting");
            }

        });

        b2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String raw = description.getText();
                if (raw == null)
                    raw = "";
                raw = raw.trim();

                // เผื่อผู้ใช้พิมพ์คอมม่า เว้นวรรค
                raw = raw.replace(",", "").trim();

                if (raw.isEmpty()) {
                    showError("Input cannot be empty");
                    return;
                }

                try {
                    double newBudget = Double.parseDouble(raw);
                    if (newBudget <= 0) {
                        showError("Input cannot be 0 and negative");
                        return;
                    }

                    appContext.addIncome(newBudget);
                    clearError();
                    clear();
                    controller.showPage("Home");

                } catch (NumberFormatException ex) {
                    showError("Input must be number");
                } catch (Exception ex) {
                    // other exception
                    showError("Error please try again");
                    ex.printStackTrace();
                }
            }
        });

    }

    private void clear() {
        description.setText("");
    }

    private void clearError() {
        errorLabel.setText("");
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
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
