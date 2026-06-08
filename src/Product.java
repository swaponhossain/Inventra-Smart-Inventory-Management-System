import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Product extends JFrame {

    JTextField name, buyingPrice, price, qty, search, discount;
    JComboBox<String> category; 
    JTable table;
    DefaultTableModel model;
    Connection con;
    
    
    boolean isRowSelecting = false; 

    public Product() {

        setTitle("Inventra - Products Management");
        setSize(980, 560);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ===== WHITE THEME =====
        Color bg = Color.WHITE;
        Color accent = new Color(0, 120, 255);
        Color softGray = new Color(245, 245, 245);

        getContentPane().setBackground(bg);

        // DB CONNECT
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/inventra",
                    "root",
                    ""
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed!");
        }

        // ===== LABELS FOR INPUTS =====
        addLabel("Name", 20, 0);
        addLabel("Category", 150, 0);
        addLabel("Price", 280, 0);
        addLabel("Qty", 410, 0);
        addLabel("Discount", 540, 0);

        // ===== INPUTS & DROPDOWN =====
        name = createField(20, 20);
        
        String[] categories = {
            "All Categories", "Snacks & Beverages", "Toiletries", "Grocery", 
            "Baby Care", "Healthcare", "Cosmetics", "Bakery & Frozen", 
            "Household", "Gadgets", "Stationery"
        };
        category = new JComboBox<>(categories);
        category.setBounds(150, 20, 120, 30);
        category.setBackground(softGray);
        category.setForeground(Color.BLACK);
        add(category);
        
        price = createField(280, 20);
        qty = createField(410, 20);
        discount = createField(540, 20);

        // ===== BUTTONS =====
        JButton addBtn = createButton("Add", accent, 680, 20);
        JButton updateBtn = createButton("Update", new Color(0, 180, 120), 760, 20);
        JButton deleteBtn = createButton("Delete", new Color(255, 80, 80), 840, 20);

        add(addBtn); add(updateBtn); add(deleteBtn);

        // ===== SEARCH AREA & BUYING PRICE =====
        search = new JTextField();
        search.setBounds(20, 70, 200, 30); 
        search.setBackground(softGray);
        add(search);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBounds(230, 70, 100, 30); 
        searchBtn.setBackground(accent);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        add(searchBtn);

        // Buying Price
        JLabel lblBuying = new JLabel("Buying Tk:");
        lblBuying.setBounds(360, 70, 80, 30);
        lblBuying.setForeground(Color.BLACK);
        add(lblBuying);

        buyingPrice = new JTextField();
        buyingPrice.setBounds(440, 70, 120, 30);
        buyingPrice.setBackground(softGray);
        buyingPrice.setForeground(Color.BLACK);
        buyingPrice.setCaretColor(Color.BLACK);
        add(buyingPrice);

        // ===== TABLE =====
        table = new JTable();
        model = new DefaultTableModel(
                new String[]{"ID","Name","Category","Buying Price","Price","Qty","Discount"}, 0
        );
        table.setModel(model);

        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setRowHeight(25);
        table.setGridColor(new Color(220, 220, 220));

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 110, 920, 380); 
        add(sp);

        loadData("", "All Categories");

        category.addActionListener(e -> {
            if (!isRowSelecting) { 
                loadData(search.getText().trim(), category.getSelectedItem().toString());
            }
        });

        searchBtn.addActionListener(e -> {
            loadData(search.getText().trim(), category.getSelectedItem().toString());
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { 
                int row = table.getSelectedRow();
                if (row != -1) {
                    isRowSelecting = true; 
                    
                    name.setText(model.getValueAt(row,1).toString());
                    category.setSelectedItem(model.getValueAt(row,2).toString());
                    buyingPrice.setText(model.getValueAt(row,3).toString());
                    price.setText(model.getValueAt(row,4).toString());
                    qty.setText(model.getValueAt(row,5).toString());

                    String d = model.getValueAt(row,6).toString();
                    discount.setText(d.equals("No Discount") ? "" : d.replace("% Discount", "").trim());
                    
                    isRowSelecting = false; 
                }
            }
        });

        // ===== ADD =====
        addBtn.addActionListener(e -> {
            try {
                if(name.getText().isEmpty() || price.getText().isEmpty() || buyingPrice.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all required fields!");
                    return;
                }
                if (category.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(this, "Please select a valid product category!");
                    return;
                }

                String disText = discount.getText().trim().replace("%", "");
                double dis = disText.isEmpty() ? 0 : Double.parseDouble(disText);

                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO products(name,category,buying_price,price,quantity,discount) VALUES(?,?,?,?,?,?)"
                );

                ps.setString(1, name.getText());
                ps.setString(2, category.getSelectedItem().toString());
                ps.setDouble(3, Double.parseDouble(buyingPrice.getText()));
                ps.setDouble(4, Double.parseDouble(price.getText()));
                ps.setInt(5, Integer.parseInt(qty.getText()));
                ps.setDouble(6, dis);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Product Added!");
                clearFields();
                loadData("", "All Categories");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Number Format in Prices, Qty or Discount!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // ===== UPDATE =====
        updateBtn.addActionListener(e -> {
            try {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a product to update!");
                    return;
                }
                int id = Integer.parseInt(model.getValueAt(row,0).toString());
                String disText = discount.getText().trim().replace("%", "");
                double dis = disText.isEmpty() ? 0 : Double.parseDouble(disText);

                PreparedStatement ps = con.prepareStatement(
                        "UPDATE products SET name=?,category=?,buying_price=?,price=?,quantity=?,discount=? WHERE id=?"
                );

                ps.setString(1, name.getText());
                ps.setString(2, category.getSelectedItem().toString());
                ps.setDouble(3, Double.parseDouble(buyingPrice.getText()));
                ps.setDouble(4, Double.parseDouble(price.getText()));
                ps.setInt(5, Integer.parseInt(qty.getText()));
                ps.setDouble(6, dis);
                ps.setInt(7, id);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Updated!");
                clearFields();
                loadData("", "All Categories");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // ===== DELETE =====
        deleteBtn.addActionListener(e -> {
            try {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a product to delete!");
                    return;
                }
                int id = Integer.parseInt(model.getValueAt(row,0).toString());
                PreparedStatement ps = con.prepareStatement("DELETE FROM products WHERE id=?");
                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Deleted!");
                clearFields();
                loadData("", "All Categories");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        setVisible(true);
    }

    // ===== UI HELPERS =====
    void addLabel(String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setBounds(x, y, 100, 20);
        add(l);
    }

    JTextField createField(int x, int y) {
        JTextField f = new JTextField();
        f.setBounds(x, y, 120, 30);
        f.setBackground(new Color(245, 245, 245));
        f.setForeground(Color.BLACK);
        f.setCaretColor(Color.BLACK);
        add(f);
        return f;
    }

    JButton createButton(String text, Color c, int x, int y) {
        JButton b = new JButton(text);
        b.setBounds(x, y, 90, 30);
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    void loadData(String key, String cat) {
        try {
            model.setRowCount(0);
            PreparedStatement ps;
            if (cat.equals("All Categories") || cat.isEmpty()) {
                ps = con.prepareStatement("SELECT * FROM products WHERE name LIKE ?");
                ps.setString(1, "%" + key + "%");
            } else {
                ps = con.prepareStatement("SELECT * FROM products WHERE name LIKE ? AND category = ?");
                ps.setString(1, "%" + key + "%");
                ps.setString(2, cat);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                double dis = rs.getDouble("discount"); 
                String discountText = (dis > 0) ? (dis + "% Discount") : "No Discount";
                model.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("name"), rs.getString("category"),
                        rs.getDouble("buying_price"), rs.getDouble("price"),
                        rs.getInt("quantity"), discountText
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void clearFields() {
        isRowSelecting = true; 
        name.setText("");
        category.setSelectedIndex(0); 
        buyingPrice.setText("");
        price.setText("");
        qty.setText("");
        discount.setText("");
        search.setText(""); 
        table.clearSelection();
        isRowSelecting = false;
    }

    public static void main(String[] args) {
        new Product();
    }
}