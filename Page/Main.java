package Page;


import javax.swing.*;

import Config.ConfigManager;
import Controller.AppController;
import Expense.CsvManager;
import Expense.DailyExpense;
import Expense.TempExpenseStore;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;

import Page.*;
import Service.AppContext;
import Service.CategoryService;
import Service.ExpenseService;

public class Main extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private AppController controller;

    private AppContext appContext;
    private ConfigManager configManager;
    private CategoryService categoryService;
    private DailyExpense dailyExpense;
    private ExpenseService expenseService;
    private CsvManager csvManager;
    private TempExpenseStore tempExpenseStore;
    private static LocalDate startDate = LocalDate.now(); 
    

    public Main() throws IOException {

        configManager = new ConfigManager();
        categoryService = new CategoryService(configManager);
        dailyExpense = new DailyExpense();
        csvManager = new CsvManager();
        tempExpenseStore = new TempExpenseStore();
        expenseService = new ExpenseService(csvManager, dailyExpense,tempExpenseStore);
        appContext = new AppContext(configManager, categoryService, dailyExpense, expenseService,tempExpenseStore);
        

        setTitle("Montra");
        setSize(375, 812);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // new Thread(() -> {
        //     while (true) {
        //         try {
        //             Thread.sleep(60_000); // เช็คทุก 1 นาที
        //             LocalDate today = LocalDate.now();

        //             if (!today.equals(startDate)) {
        //                 System.out.println("ข้ามวันแล้ว! กำลังรีเซ็ตระบบ...");
        //                 try {
        //                     tempExpenseStore.resetToday();
        //                 } catch (Exception e) {
        //                     e.printStackTrace();
        //                 }

        //                 // วิธีง่ายที่สุดคือ “ให้ user ปิด–เปิดโปรแกรมใหม่”
        //                 System.out.println("กรุณาปิดแล้วเปิดโปรแกรมใหม่ เพื่อเริ่มวันใหม่");
        //                 break;
        //             } else {
        //                 System.out.println("วันเดิม");
        //             }

        //         } catch (InterruptedException e) {
        //             e.printStackTrace();
        //         }
        //     }
        // }).start();


        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        controller = new AppController(cardLayout, mainPanel);

        Image icon = new ImageIcon(getClass().getResource("/image/logo1.png")).getImage();
        setIconImage(icon);
        setVisible(true);

        // Page
        mainPanel.add(new Welcome(controller, appContext), "Welcome");
        mainPanel.add(new Home(controller, appContext), "Home");
        mainPanel.add(new More(controller), "More");
        mainPanel.add(new Add(controller, appContext), "Add");
        mainPanel.add(new Setting(controller), "Setting");
        mainPanel.add(new Budget(controller, appContext), "Budget");
        mainPanel.add(new Sumpath(controller), "Sumpath");
        mainPanel.add(new CategoryPath(controller), "CategoryPath");
        mainPanel.add(new SetCat(controller, appContext), "SetCat");
        mainPanel.add(new RemoveCat(controller, appContext), "RemoveCat");
        mainPanel.add(new Ep(controller, appContext), "Ep");

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