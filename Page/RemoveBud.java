package Page;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.*;
import ButtonDesign.LabelCard;
import ButtonDesign.PillButton;
import Controller.AppController;

import Service.AppContext;

/**
 * RemoveBudget page
 * ไว้ลบรายรับ
 */
public class RemoveBud extends JPanel {
    JLabel errorLabel;
    LabelCard description;

    public RemoveBud(AppController controller, AppContext appContext) {
        setLayout(null);

        JButton b1 = new PillButton("← Back");
        b1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b1.setBounds(0, 10, 100, 30);
        ((PillButton) b1).setButtonStyle(PillButton.Style.OUTLINE);
        b1.setForeground(Color.WHITE);
        add(b1);

        JLabel remainl1 = new JLabel("฿");
        JLabel remainl2 = new JLabel(" ");

        try {
            double balance = appContext.getBalance();
            remainl2.setText(String.format("%,.2f", balance));
        } catch (Exception e) {
            e.printStackTrace();
        }

        remainl1.setFont(new Font("Segoe UI", Font.BOLD, 30));
        remainl1.setForeground(Color.WHITE);
        remainl1.setBounds(90, 90, 200, 60);

        remainl2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        remainl2.setForeground(findcolor(appContext.getBalance()));
        remainl2.setBounds(120, 90, 200, 60);

        JLabel remainl3 = new JLabel("Balance", SwingConstants.CENTER);
        remainl3.setFont(new Font("Segoe UI", Font.BOLD, 25));
        remainl3.setForeground(Color.WHITE);
        remainl3.setBounds(100, 150, 150, 30);
        add(remainl1);
        add(remainl2);
        add(remainl3);

        JSeparator line = new JSeparator(SwingConstants.HORIZONTAL);
        line.setForeground(Color.WHITE);
        line.setBounds(80, 145, 200, 5);
        add(line);

        // error Label
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        errorLabel.setBounds(30, 360, 400, 30);
        add(errorLabel);

        description = new LabelCard("Remove Income", "Remove Your Income");
        description.setBounds(30, 250, 300, 100);
        add(description);

        PillButton b2 = new PillButton(" Remove ");
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

                // ตรวจสอบมี , ไหม
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

                    appContext.removeIncome(newBudget);
                    clearError();
                    clear();
                    controller.showPage("Home");

                } catch (NumberFormatException ex) {
                    showError("Input must be number");
                } catch (Exception ex) {
                    showError("Error please try again");
                    ex.printStackTrace();
                }
            }
        });

        appContext.addListener(evt -> {
            if ("reload".equals(evt.getPropertyName())) {
                double balanceNow = appContext.getBalance();
                remainl2.setText(String.format("%,.2f", balanceNow));
                remainl2.setForeground(findcolor(balanceNow));
        }
        });

    }
    private Color findcolor(double remaining) {
        if (remaining <= 0) return Color.RED;
        if (remaining < 500) return Color.YELLOW; 
        return Color.WHITE;
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
