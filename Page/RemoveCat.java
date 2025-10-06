package Page;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

import Controller.AppController;

public class RemoveCat extends JPanel{
    
    public RemoveCat(AppController controller){
        setLayout(null);

        JButton b1 = new JButton("← Back"); 
        b1.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        b1.setBounds(0, 0, 100, 30); 
        b1.setForeground(Color.BLACK); 
        add(b1);

        JComboBox<String> c = province_to_combobox(Arrays.asList("Food", "Transport", "Utilities", "Entertainment", "Education"));
        c.setBounds(57, 150, 250, 75); 
        add(c);


        JButton b2 = new JButton(" Remove ");
        b2.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        b2.setBounds(100, 450, 150, 60); 
        b2.setForeground(Color.BLACK); 
        add(b2); 

        b1.addActionListener(e -> controller.showPage("CategoryPath"));
            
    

    }
    private JComboBox<String> province_to_combobox(List<String> s){
            JComboBox<String> tmp = new JComboBox<>();
            for (String string : s) {
                tmp.addItem(string);
            }
            return tmp;
            
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
        Point2D end   = new Point2D.Float(w, h);

        float[] dist = {0.0f, 0.5f, 1.0f};

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
