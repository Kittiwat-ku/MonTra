package Controller;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;

/**
 *  ทำหน้าที่จัดการการเปลี่ยนหน้า ของแอปพลิเคชั่นที่ใช้ cardLayout นี้
 */
public class AppController {
    
    // เก็บตัวแปร Layout
    private CardLayout cardLayout;
    // เก็บตัวแปร mainPanal
    private JPanel mainPanel;

    // ควบคุมการเปลี่ยนหน้าของ Page
    public AppController(CardLayout cardLayout, JPanel mainPanel) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
    }

    // สำหรับการสลับเปลี่ยนหน้า ตามชื่อที่กำหนด
    public void showPage(String name) {
        cardLayout.show(mainPanel, name);
    }
}
