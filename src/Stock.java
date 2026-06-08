import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Stock extends JFrame {

    JTable table;
    DefaultTableModel model;
    Connection con;

    public Stock() {

        setTitle("Inventra Stock");
        setSize(700, 450);
        setLayout(null);
        setLocationRelativeTo(null);

        // DATABASE CONNECTION
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/inventra",
                    "root",
                    ""
            );
        } catch(Exception e){
            e.printStackTrace();
        }

        JLabel title = new JLabel("Stock Management");
        title.setBounds(20,20,200,30);
        add(title);

        // TABLE
        model = new DefaultTableModel(
                new String[]{"ID","Product","Category","Stock"},0
        );

        table = new JTable(model);

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20,60,640,300);
        add(sp);

        JButton lowStockBtn = new JButton("Low Stock Alert");
        lowStockBtn.setBounds(20,370,150,30);
        add(lowStockBtn);

        loadStock();

        // LOW STOCK ALERT
        lowStockBtn.addActionListener(e -> {

            try {

                Statement st = con.createStatement();

                ResultSet rs = st.executeQuery(
                    "SELECT name, quantity FROM products WHERE quantity < 5"
                );

                String msg = "";

                while(rs.next()) {

                    msg += rs.getString("name")
                            + " = "
                            + rs.getInt("quantity")
                            + " left\n";
                }

                if(msg.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "No Low Stock Product");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "LOW STOCK:\n\n" + msg);
                }

            } catch(Exception ex){
                ex.printStackTrace();
            }

        });

        setVisible(true);
    }

    void loadStock() {

        try {

            model.setRowCount(0);

            Statement st = con.createStatement();

            ResultSet rs =
                    st.executeQuery("SELECT * FROM products");

            while(rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("quantity")
                });
            }

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Stock();
    }
}