import javax.swing.*;

import Controller.AppController;
import Page.*;
import Service.AppContext;

import java.awt.*;
import java.io.IOException;

public class Main extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private AppController controller;

    private AppContext appContext; // แหล่งรวมบริการทั้งหมด

    public Main() {
        // สร้าง AppContext ครั้งเดียวให้ทั้งแอป
        try {
            appContext = new AppContext(); // ภายในจัดการ Storage/Config/Temp/Lifecycle ให้แล้ว
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Failed to initialize application context:\n" + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }

        setTitle("Montra");
        setSize(375, 812);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Layout หลัก + Controller
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        try {
            controller = new AppController(cardLayout, mainPanel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ไอคอน (ถ้า resource ไม่มี ให้ห่อไว้กัน NPE)
        try {
            Image icon = new ImageIcon(getClass().getResource("/image/logo1.png")).getImage();
            setIconImage(icon);
        } catch (Exception ignore) { /* ไม่มีไอคอนไม่เป็นไร */ }

        // --- Register Pages (ทุกหน้าใช้ appContext ชุดเดียว) ---
        mainPanel.add(new Welcome(controller, appContext),     "Welcome");
        mainPanel.add(new Home(controller, appContext),        "Home");
        mainPanel.add(new More(controller),                    "More");
        mainPanel.add(new Add(controller, appContext),         "Add");
        mainPanel.add(new Setting(controller),                 "Setting");
        mainPanel.add(new Budget(controller, appContext),      "Budget");
        mainPanel.add(new Summary(controller,appContext),      "Summary");
        mainPanel.add(new CategoryPath(controller),            "CategoryPath");
        mainPanel.add(new SetCat(controller, appContext),      "SetCat");
        mainPanel.add(new RemoveCat(controller, appContext),   "RemoveCat");
        mainPanel.add(new Ep(controller, appContext),          "Ep");
        mainPanel.add(new FBudget(controller, appContext),     "FBudget");
        mainPanel.add(new RemoveBud(controller, appContext),     "RemoveBud");

        add(mainPanel);

        // หน้าแรก
        cardLayout.show(mainPanel, "Welcome");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}
