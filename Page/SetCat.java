package Page;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import javax.swing.*;

import ButtonDesign.LabeledInputCard;
import ButtonDesign.PillButton;
import Controller.AppController;
import Service.AppContext;

public class SetCat extends JPanel {
    LabeledInputCard description;
    private final JLabel errorLabel;

    public SetCat(AppController controller, AppContext appContext) {
        setLayout(null);

        JButton b1 = new PillButton("← Back");
        b1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b1.setBounds(0, 10, 100, 30);
        ((PillButton) b1).setButtonStyle(PillButton.Style.OUTLINE);
        b1.setForeground(Color.WHITE);
        add(b1);

        description = new LabeledInputCard("Category", "Set Category");
        description.setBounds(30, 200, 300, 100);
        add(description);

        // error label
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        errorLabel.setBounds(30, 320, 320, 30);
        add(errorLabel);

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
                controller.showPage("CategoryPath");
            }
            
        });

        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String raw = description.getText();
                if (raw == null) raw = "";

                
                raw.trim();

                
                String trimmed = raw.trim();

                if (trimmed.isEmpty()) {
                    showError("Input cannot be empty");
                    return;
                }
                // if (hasLeadingOrTrailingSpace) {
                //     showError("Leading/Trailing spaces are not allowed");
                //     return;
                // }
                if (trimmed.contains(",")) {
                    showError("Comma ( , ) is not allowed");
                    return;
                }

                try {
                    // All passed
                    appContext.addCategory(trimmed);
                    showPassed("[ "+trimmed+" ] has been added");
                    clear();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    showError("Cannot save category. Please try again.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Unexpected error. Please try again.");
                }
            }
        });
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setForeground(Color.red);
    }
    public void showPassed(String msg){
        errorLabel.setText(msg);
        errorLabel.setForeground(Color.GREEN);
    }

    private void clear(){
        description.setText("");
    }
    private void clearError() {
        errorLabel.setText("");
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
