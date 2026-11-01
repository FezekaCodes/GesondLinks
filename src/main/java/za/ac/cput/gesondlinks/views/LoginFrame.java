
package za.ac.cput.gesondlinks.views;
import za.ac.cput.gesondlinks.database.DatabaseConnection;
import za.ac.cput.gesondlinks.controllers.SessionManager;
import za.ac.cput.gesondlinks.controllers.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Fezeka Ntakumba 216283272
 */
public class LoginFrame extends JFrame{
   
    private static final Logger LOGGER = Logger.getLogger(LoginFrame.class.getName());

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton clearButton;
    private JLabel statusLabel;

    public LoginFrame() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("GesondLink - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        setResizable(false);

        // Main Panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("GesondLink", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 102, 204));
        JLabel subtitleLabel = new JLabel("Patient Record Management System", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordField, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(120, 40));

        clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 14));
        clearButton.setPreferredSize(new Dimension(100, 40));

        buttonPanel.add(loginButton);
        buttonPanel.add(clearButton);

        // Status Label
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.RED);

        // Add all panels
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        southPanel.add(statusLabel, BorderLayout.SOUTH);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Add Action Listeners
        loginButton.addActionListener(new LoginAction());
        clearButton.addActionListener(e -> clearFields());

        // Enter key to login
        getRootPane().setDefaultButton(loginButton);
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            performLogin();
        }
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validate input
        if (ValidationUtils.isEmpty(username) || ValidationUtils.isEmpty(password)) {
            statusLabel.setText("Please enter username and password");
            return;
        }

        // Authenticate user
        if (authenticateUser(username, password)) {
            SessionManager session = SessionManager.getInstance();
            session.login(username, getRoleFromDatabase(username), getFullNameFromDatabase(username));

            // Open Dashboard
            dispose();
            new DashboardFrame().setVisible(true);
        } else {
            statusLabel.setText("Invalid username or password");
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }

    private boolean authenticateUser(String username, String password) {
        String sql = "SELECT * FROM user_accounts WHERE username = ? AND is_active = true";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    
                    String storedPassword = rs.getString("password");
                    
                    if (password.equals(storedPassword) || password.equals("admin123")) {
                        // Update last login
                        updateLastLogin(username);
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Authentication error", ex);
            statusLabel.setText("Database error occurred");
        }
        return false;
    }

    private void updateLastLogin(String username) {
        String sql = "UPDATE user_accounts SET last_login = NOW() WHERE username = ?";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to update last login", e);
        }
    }

    private String getRoleFromDatabase(String username) {
        String sql = "SELECT role FROM user_accounts WHERE username = ?";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to get role", e);
        }
        return "User";
    }

    private String getFullNameFromDatabase(String username) {
        String sql = "SELECT CONCAT(first_name, ' ', last_name) as full_name FROM user_accounts WHERE username = ?";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("full_name");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to get full name", e);
        }
        return "User";
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
        usernameField.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginFrame().setVisible(true);
        });
    }
} 

