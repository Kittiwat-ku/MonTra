import javax.swing.*;
import Controller.AppController;
import java.awt.*;
import Page.*;

public class Main extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private AppController controller;

    public Main() {
        setTitle("Montra");
        setSize(375, 812);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        controller = new AppController(cardLayout, mainPanel);

        // Page
        mainPanel.add(new Welcome(controller), "Welcome");
        mainPanel.add(new Home(controller), "Home");
        mainPanel.add(new More(controller), "More");
        mainPanel.add(new Add(controller), "Add");
        mainPanel.add(new Summary(controller), "Summary");
        mainPanel.add(new Setting(controller), "Setting");
        mainPanel.add(new Export(controller), "Export");
        mainPanel.add(new Budget(controller), "Budget");
        mainPanel.add(new Sumpath(controller), "Sumpath");

        add(mainPanel);
        cardLayout.show(mainPanel, "Welcome");
        
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}