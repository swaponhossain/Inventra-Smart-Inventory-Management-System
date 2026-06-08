import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Login extends JFrame {

    JTextField userField;
    JPasswordField passField;
    JComboBox<String> roleBox;

    Color primaryColor = new Color(0, 120, 255);
    Color textDark = new Color(44, 62, 80);

    public Login() {
        setTitle("Inventra - System Login");
        setSize(400, 330);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        JLabel headerLabel = new JLabel("INVENTRA LOGIN");
        headerLabel.setBounds(30, 20, 300, 30);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(primaryColor);
        add(headerLabel);

        // Username
        JLabel u1 = new JLabel("Username");
        u1.setBounds(30, 70, 100, 25);
        add(u1);

        userField = new JTextField();
        userField.setBounds(130, 70, 220, 30);
        add(userField);

        // Password
        JLabel p1 = new JLabel("Password");
        p1.setBounds(30, 115, 100, 25);
        add(p1);

        passField = new JPasswordField();
        passField.setBounds(130, 115, 220, 30);
        add(passField);

        // Role
        JLabel r1 = new JLabel("Select Role");
        r1.setBounds(30, 160, 100, 25);
        add(r1);

        roleBox = new JComboBox<>(new String[]{"Admin", "Employee"});
        roleBox.setBounds(130, 160, 220, 30);
        add(roleBox);

        // LOGIN BUTTON (Center Aligned)
        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(140, 215, 120, 35); 
        loginBtn.setBackground(primaryColor);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        add(loginBtn);

        loginBtn.addActionListener(e -> login());

        setVisible(true);
    }

    void login() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();
        String role = roleBox.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill up all fields!");
            return;
        }

        try {
            try (Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/inventra", "root", "")) {
                
                String sql = "SELECT * FROM users WHERE username=? AND password=? AND role=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, username);
                pst.setString(2, password);
                pst.setString(3, role);
                
                ResultSet rs = pst.executeQuery();
                
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login Successful!");
                    this.dispose();
                    new Dashboard(role);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Username, Password, or Role!");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Connection Error!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}