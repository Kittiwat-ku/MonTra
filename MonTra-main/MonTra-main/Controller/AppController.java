package Controller;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;


public class AppController {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public AppController(CardLayout cardLayout, JPanel mainPanel) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
    }

    public void showPage(String name) {
        cardLayout.show(mainPanel, name);
    }
}
