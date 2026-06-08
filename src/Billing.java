import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Billing extends JFrame {

    Connection con;
    JTextArea billArea;
    JComboBox<String> productBox;
    JTextField qtyField, discountField, nameField, phoneField; 

    double grandTotal = 0;

    public Billing() {

        setTitle("Inventra Billing System");
        setSize(550, 700);
        setLayout(null);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 248, 255));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 12);
        Font btnFont = new Font("Segoe UI", Font.BOLD, 13);

        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventra", "root", "");
        } catch(Exception e){ e.printStackTrace(); }

        // Customer Details
        JLabel nLabel = new JLabel("Customer Name:");
        nLabel.setBounds(20, 20, 100, 30);
        add(nLabel);
        nameField = new JTextField();
        nameField.setBounds(120, 20, 150, 30);
        add(nameField);

        JLabel pLabel = new JLabel("Phone:");
        pLabel.setBounds(280, 20, 50, 30);
        add(pLabel);
        phoneField = new JTextField();
        phoneField.setBounds(330, 20, 180, 30);
        add(phoneField);

        // Product selection
        productBox = new JComboBox<>();
        productBox.setBounds(20, 70, 180, 30);
        add(productBox);

        qtyField = new JTextField();
        qtyField.setBounds(210, 70, 80, 30);
        add(qtyField);
        
        // Qty Label 
        JLabel qLabel = new JLabel("Qty");
        qLabel.setBounds(210, 55, 80, 15);
        qLabel.setFont(labelFont);
        add(qLabel);

        discountField = new JTextField();
        discountField.setBounds(300, 70, 80, 30);
        add(discountField);

        JLabel dLabel = new JLabel("Discount %");
        dLabel.setBounds(300, 55, 100, 15);
        dLabel.setFont(labelFont);
        add(dLabel);

        JButton addBtn = new JButton("Add Item");
        addBtn.setBounds(390, 70, 120, 30);
        addBtn.setBackground(new Color(46, 134, 222));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(btnFont);
        add(addBtn);

        // Bill Area
        billArea = new JTextArea();
        billArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        JScrollPane sp = new JScrollPane(billArea);
        sp.setBounds(20, 120, 490, 400);
        add(sp);

        // Buttons
        JButton clearBtn = new JButton("Clear");
        clearBtn.setBounds(20, 550, 120, 35);
        clearBtn.setBackground(new Color(231, 76, 60));
        clearBtn.setForeground(Color.WHITE);
        add(clearBtn);

        JButton printBtn = new JButton("Print Bill");
        printBtn.setBounds(160, 550, 140, 35);
        printBtn.setBackground(new Color(39, 174, 96));
        printBtn.setForeground(Color.WHITE);
        add(printBtn);

        JLabel totalLabel = new JLabel("Total: 0.0");
        totalLabel.setBounds(320, 550, 200, 35);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(totalLabel);

        loadProducts();

        addBtn.addActionListener(e -> {
            double t = addToBill();
            grandTotal += t;
            totalLabel.setText("Total: " + String.format("%.2f", grandTotal));
        });

        // PRINT & SAVE LOGIC
        printBtn.addActionListener(e -> {
            if(nameField.getText().isEmpty() || phoneField.getText().isEmpty()){
                JOptionPane.showMessageDialog(this, "Please fill Customer Name and Phone!");
                return;
            }

            try {
                String query = "INSERT INTO sales_history (customer_name, customer_phone, total_amount) VALUES (?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, nameField.getText());
                ps.setString(2, phoneField.getText());
                ps.setDouble(3, grandTotal);
                ps.executeUpdate();

                String billHeader = "Customer: " + nameField.getText() + "\nPhone: " + phoneField.getText() + "\n------------------------\n";
                billArea.insert(billHeader, 0);
                billArea.append("\n========================\nGrand Total: " + String.format("%.2f", grandTotal));
                billArea.print();
            } catch(Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving report: " + ex.getMessage());
            }
        });

        clearBtn.addActionListener(e -> {
            billArea.setText("");
            nameField.setText("");
            phoneField.setText("");
            qtyField.setText("");
            discountField.setText("");
            grandTotal = 0;
            totalLabel.setText("Total: 0.0");
        });

        setVisible(true);
    }

    void loadProducts() {
        try {
            productBox.removeAllItems();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT name FROM products");
            while(rs.next()){ productBox.addItem(rs.getString(1)); }
        } catch(Exception e){ e.printStackTrace(); }
    }

    double addToBill() {
        try {
            if(productBox.getSelectedItem() == null || qtyField.getText().isEmpty()) return 0;
            int qty = Integer.parseInt(qtyField.getText());
            double discount = discountField.getText().isEmpty() ? 0 : Double.parseDouble(discountField.getText());
            String product = productBox.getSelectedItem().toString();

            PreparedStatement ps = con.prepareStatement("SELECT price, quantity, buying_price FROM products WHERE name=?");
            ps.setString(1, product);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                double price = rs.getDouble(1);
                int stock = rs.getInt(2);
                double buyingPrice = rs.getDouble(3);

                if(qty > stock){ JOptionPane.showMessageDialog(this,"Not Enough Stock!"); return 0; }

                double total = price * qty;
                double finalTotal = total - (total * discount / 100);
                double profit = finalTotal - (buyingPrice * qty);

                billArea.append(product + " | Qty: " + qty + " | Final: " + finalTotal + "\n");
                
                PreparedStatement ps2 = con.prepareStatement("UPDATE products SET quantity = quantity - ?, total_sales = total_sales + ?, total_profit = total_profit + ? WHERE name=?");
                ps2.setInt(1, qty); ps2.setDouble(2, finalTotal); ps2.setDouble(3, profit); ps2.setString(4, product);
                ps2.executeUpdate();

                qtyField.setText(""); discountField.setText("");
                return finalTotal;
            }
        } catch(Exception e){ e.printStackTrace(); }
        return 0;
    }

    public static void main(String[] args) {
        new Billing();
    }
}