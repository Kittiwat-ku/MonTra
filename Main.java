import javax.swing.*;

import Config.ConfigManager;
import Controller.AppController;
import Expense.DailyExpense;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import Page.*;
import Service.AppContext;
import Service.CategoryService;

public class Main extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private AppController controller;

    private AppContext appContext;
    private ConfigManager configManager;
    private CategoryService categoryService;
    private DailyExpense dailyExpense;

    public Main() throws IOException {

        configManager = new ConfigManager();
        categoryService = new CategoryService(configManager);
        dailyExpense = new DailyExpense();
        appContext = new AppContext(configManager, categoryService,dailyExpense);


        setTitle("Montra");
        setSize(375, 812);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);


        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        controller = new AppController(cardLayout, mainPanel);

        // Page
        mainPanel.add(new Welcome(controller,appContext), "Welcome");
        mainPanel.add(new Home(controller,appContext), "Home");
        mainPanel.add(new More(controller), "More");
        mainPanel.add(new Add(controller,appContext), "Add");
        mainPanel.add(new Summary(controller), "Summary");
        mainPanel.add(new Setting(controller), "Setting");
        mainPanel.add(new Export(controller), "Export");
        mainPanel.add(new Budget(controller,appContext), "Budget");
        mainPanel.add(new Sumpath(controller), "Sumpath");

        add(mainPanel);
        cardLayout.show(mainPanel, "Welcome");
        
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new Main().setVisible(true);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }
}