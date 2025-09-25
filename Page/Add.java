package Page; 

import java.awt.*; 
import java.awt.geom.*; 
import java.io.*; 
import javax.swing.*;
import ButtonDesign.*;
import Controller.AppController; 

public class Add extends JPanel{ 
    public Add(AppController controller){ 
        setLayout(null); 
        
        JLabel l1 = new JLabel(" Add Your Transaction "); 
        l1.setFont(new Font("Segoe UI", Font.BOLD, 30)); 
        l1.setForeground(new Color(255, 255, 224)); 
        l1.setBounds(20, 100, 400, 50); 
        add(l1); 
        
        JButton b1 = new JButton("← Back"); 
        b1.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        b1.setBounds(0, 0, 100, 30); 
        b1.setForeground(Color.BLACK); 
        add(b1); 

        LabeledInputCard description = new LabeledInputCard("Description", "Example: Tinoy, Shabu");
        description.setBounds(30, 200, 300, 100);
        add(description);

        LabeledInputCard amount = new LabeledInputCard("Amount", "Example: 500, 1000");
        amount.setBounds(30, 350, 300, 100);
        add(amount);
        
        JComboBox<String> c = province_to_combobox(); 
        c.setBounds(57, 500, 250, 50); 
        add(c); 

        JButton b2 = new JButton(" Comfirm ");
        b2.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        b2.setBounds(100, 600, 150, 60); 
        b2.setForeground(Color.BLACK); 
        add(b2); 
        
        // Action
        b1.addActionListener(e -> controller.showPage("Home")); 
    }
    
    // Test
    JComboBox<String> province_to_combobox(){
        JComboBox<String> tmp = new JComboBox<>();
        File f = null;
        FileReader fr = null;
        BufferedReader br = null;
        try{
            f = new File("./File/Catagoly.csv");
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            String s;
            br.readLine();
            while ((s=br.readLine()) !=null){
                String arr[] = s.split(",");
                tmp.addItem( arr[1]) ;
            }
            tmp.setSelectedItem("Chooes your Catagoly");
        }catch(Exception e){
            System.out.println(e);
        }finally{
            try{
                br.close();fr.close();
            }catch(Exception e){
                System.out.println(e);
            }
        }
        return tmp;
    }

    // Background Color
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