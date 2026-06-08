import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Dashboard extends JFrame {

    JPanel main;
    Connection con;
    
    JButton productBtn, reportBtn, stockBtn, billBtn, dashboardBtn;

    Color primaryColor = new Color(0, 120, 255);
    Color sidebarBg = new Color(245, 247, 250);
    Color textDark = new Color(44, 62, 80);

    public Dashboard(String role) { 
        setTitle("Inventra Dashboard - Logged in as: " + role);
        setSize(920, 620);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);

        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventra", "root", "");
        } catch(Exception e){ e.printStackTrace(); }

        JPanel menu = new JPanel();
        menu.setBounds(0, 0, 230, 620);
        menu.setBackground(sidebarBg);
        menu.setLayout(null);
        add(menu);

        JLabel title = new JLabel("INVENTRA");
        title.setBounds(30, 25, 170, 35);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(primaryColor);
        menu.add(title);

        JSeparator line = new JSeparator();
        line.setBounds(20, 75, 190, 2);
        menu.add(line);

        dashboardBtn = createMenuButton("Dashboard", 100);
        productBtn = createMenuButton("Products Management", 150);
        stockBtn = createMenuButton("Stock Alerts", 200);
        billBtn = createMenuButton("Billing System", 250);
        reportBtn = createMenuButton("Sales Report", 300);
        JButton logoutBtn = createMenuButton("Logout", 510);
        logoutBtn.setForeground(new Color(231, 76, 60));

        menu.add(dashboardBtn); menu.add(productBtn); menu.add(stockBtn);
        menu.add(billBtn); menu.add(reportBtn); menu.add(logoutBtn);

        if (role.equalsIgnoreCase("Employee")) {
            productBtn.setEnabled(false); reportBtn.setEnabled(false);
            productBtn.setToolTipText("Access Denied: Admin Only");
            reportBtn.setToolTipText("Access Denied: Admin Only");
        }

        main = new JPanel();
        main.setBounds(230, 0, 690, 620);
        main.setBackground(Color.WHITE);
        main.setLayout(null);
        add(main);

        loadHome(role);

        dashboardBtn.addActionListener(e -> loadHome(role));
        productBtn.addActionListener(e -> new Product());
        stockBtn.addActionListener(e -> new StockAlert());
        billBtn.addActionListener(e -> new Billing());
        reportBtn.addActionListener(e -> new Report());

        logoutBtn.addActionListener(e -> {
            this.dispose();
            new Login();
        });

        setVisible(true);
    }

    
    private double getTotalProfit() {
        double profit = 0.0;
        try {
            // Billing 
            String query = "SELECT SUM(total_profit) AS total_profit FROM products";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                profit = rs.getDouble("total_profit");
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return profit;
    }

    void loadHome(String role) {
        main.removeAll();
        
        JLabel t = new JLabel("Welcome, " + role + "!");
        t.setFont(new Font("Segoe UI", Font.BOLD, 24));
        t.setForeground(textDark);
        t.setBounds(30, 25, 300, 35);
        main.add(t);

        
        JButton profitBtn = new JButton("View Total Profit");
        profitBtn.setBounds(30, 80, 200, 40);
        profitBtn.setBackground(new Color(0, 180, 120));
        profitBtn.setForeground(Color.WHITE);
        profitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        profitBtn.setFocusPainted(false);
        
        profitBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Total Calculated Profit: ৳ " + String.format("%.2f", getTotalProfit()));
        });
        
        main.add(profitBtn);
        
        repaint();
        revalidate();
    }

    private JButton createMenuButton(String text, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(20, y, 190, 38);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(Color.WHITE);
        btn.setForeground(textDark);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 240), 1));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }

    public static void main(String[] args) {
        new Dashboard("Admin");
    }
}