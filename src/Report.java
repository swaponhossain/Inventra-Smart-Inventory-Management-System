import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class Report extends JFrame {

    JTable table;
    DefaultTableModel model;

    public Report() {
        setTitle("Sales History Report");
        setSize(750, 500);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        // Header Panel (Title)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 120, 255)); // Theme Blue
        headerPanel.setPreferredSize(new Dimension(750, 50));
        JLabel title = new JLabel("SALES  REPORT HISTORY");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        headerPanel.add(title);
        add(headerPanel, BorderLayout.NORTH);

        // Table Model
        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Customer Name");
        model.addColumn("Phone");
        model.addColumn("Total Amount");
        model.addColumn("Date");

        table = new JTable(model);
        
        // Table Styling
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(230, 242, 255));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(245, 247, 250));
        header.setForeground(new Color(44, 62, 80));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sp.getViewport().setBackground(Color.WHITE);
        add(sp, BorderLayout.CENTER);

        loadData();
        setVisible(true);
    }

    void loadData() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventra", "root", "");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM sales_history ORDER BY id DESC");

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("customer_name"));
                row.add(rs.getString("customer_phone"));
                row.add(rs.getString("total_amount"));
                row.add(rs.getString("sale_date"));
                model.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Report();
    }
}