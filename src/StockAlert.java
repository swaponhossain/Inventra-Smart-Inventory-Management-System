import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class StockAlert extends JFrame {

    JTable table;
    DefaultTableModel model;

    public StockAlert() {
        setTitle("Low Stock Alert");
        setSize(600, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(231, 76, 60)); // লাল রঙ দিয়ে অ্যালার্ট বোঝানো হয়েছে
        JLabel title = new JLabel("LOW STOCK ALERT (Threshold < 10)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        model = new DefaultTableModel();
        model.addColumn("Product Name");
        model.addColumn("Current Stock");

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadStockData();
        setVisible(true);
    }

    void loadStockData() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventra", "root", "");
            
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT name, quantity FROM products WHERE quantity < 10");

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                Vector<String> row = new Vector<>();
                row.add(rs.getString("name"));
                row.add(rs.getString("quantity"));
                model.addRow(row);
            }
            
            if(!hasData) {
                JOptionPane.showMessageDialog(this, "All stocks are sufficient!");
                this.dispose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new StockAlert();
    }
}
