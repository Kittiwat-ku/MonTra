package Page;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import javax.swing.*;

import ButtonDesign.LabeledInputCard;
import ButtonDesign.PillButton;
import Controller.AppController;
import Service.AppContext;

public class Ep extends JPanel {
    private final JLabel errorLabel;
    private final LabeledInputCard filename;

    public Ep(AppController controller, AppContext appContext) {
        setLayout(null);

        JButton back = new PillButton("← Back");
        back.setFont(new Font("Segoe UI", Font.BOLD, 16));
        back.setBounds(0, 10, 100, 30);
        ((PillButton) back).setButtonStyle(PillButton.Style.OUTLINE);
        back.setForeground(Color.WHITE);
        add(back);

        filename = new LabeledInputCard("Export", "Insert filename");
        filename.setBounds(30, 200, 300, 100);
        add(filename);

        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        errorLabel.setBounds(30, 320, 320, 20);
        add(errorLabel);

        PillButton confirm = new PillButton(" Confirm ");
        confirm.setFont(new Font("Segoe UI", Font.BOLD, 16));
        confirm.setBounds(100, 450, 175, 60);
        ((PillButton) confirm).setButtonStyle(PillButton.Style.HYBRID);
        confirm.setForeground(Color.BLACK);
        add(confirm);

        back.addActionListener(e -> {
            clearError();
            filename.setText("");
            controller.showPage("Home");
        });
        
        // ตรวจสอบชื่อไฟล์และส่งออก เมื่อกดปุ่มcomfirm
        confirm.addActionListener(e -> {
            String raw = filename.getText();
            if (raw == null) raw = "";
            String trimmed = raw.trim();
            if (trimmed.isEmpty()) { showError("Filename cannot be empty"); return; }
            String safe = trimmed.replaceAll("[\\\\/:*?\"<>|]", "_");

            try {
                clearError();
                appContext.exportCustom(safe);
                filename.setText("");
                controller.showPage("Home");
            } catch (IllegalStateException ex) {
                showError("Cannot export: today list is empty");
            } catch (Exception ex) {
                showError("Export failed: " + ex.getMessage());
            }
        });
    }

    private void showError(String msg) { errorLabel.setText(msg); }
    private void clearError() { errorLabel.setText(""); }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        Point2D start = new Point2D.Float(0, 0);
        Point2D end   = new Point2D.Float(w, h);
        float[] dist  = {0.0f, 0.5f, 1.0f};
        Color[] colors= { new Color(0x4A5C58), new Color(0x0A5C36), new Color(0x1F2C2E) };
        LinearGradientPaint lgp = new LinearGradientPaint(start, end, dist, colors);
        g2d.setPaint(lgp);
        g2d.fillRect(0, 0, w, h);
    }
}
