import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Login {
    // Method to create the login panel and frame
    public static void createLoginPanel() {
        // Create a JFrame for the login page
        JFrame frame = new JFrame("Pharmacy Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300); // Adjusted window size to be more compact
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setResizable(false);

        // MAIN-PANEL [ BoxLayout ]
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // Adjusted padding for a clean look
        loginPanel.setBackground(new Color(255, 255, 255)); // Default white background (no gray)

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome to the Pharmacy System", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(0, 102, 204)); // Soft blue color for the title
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(welcomeLabel);

        // Spacer
        loginPanel.add(Box.createVerticalStrut(20));

        // USERNAME PANEL [ label + field ]
        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setPreferredSize(new Dimension(200, 30));

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameLabel.setForeground(new Color(0, 102, 204)); // Same color as welcome label
        
        JPanel usernamePanel = new JPanel(new BorderLayout(10, 10));
        usernamePanel.add(usernameLabel, BorderLayout.WEST);
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        usernamePanel.setBackground(new Color(255, 255, 255)); // Ensure background is white

        loginPanel.add(usernamePanel);

        // Spacer
        loginPanel.add(Box.createVerticalStrut(15)); // Spacing between fields

        // PASSWORD PANEL [ field + label ]
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setPreferredSize(new Dimension(200, 30));

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordLabel.setForeground(new Color(0, 102, 204)); // Same color as welcome label

        JPanel passwordPanel = new JPanel(new BorderLayout(10, 10));
        passwordPanel.add(passwordLabel, BorderLayout.WEST);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.setBackground(new Color(255, 255, 255)); // Ensure background is white

        loginPanel.add(passwordPanel);

        // Spacer
        loginPanel.add(Box.createVerticalStrut(20)); // Spacing before buttons

        // LOGIN BUTTON
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10)); // Fixed flow layout issues
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(100, 149, 237)); // Soft blue color for the button
        loginButton.setForeground(Color.WHITE);
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginButton);

        // Message label
        JLabel messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);

        // Add components to the main panel
        loginPanel.add(buttonPanel);
        loginPanel.add(Box.createVerticalStrut(10)); // Spacing before the message label
        loginPanel.add(messageLabel);

        // Add main panel to the frame
        frame.add(loginPanel);
        frame.setVisible(true);

        // Action listener for the login button
        loginButton.addActionListener(e -> handleLogin(usernameField, passwordField, messageLabel, frame));
    }

    // Method to handle login validation and logic
    private static void handleLogin(JTextField usernameField, JPasswordField passwordField, JLabel messageLabel, JFrame frame) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (validateLogin(username, password)) {
            frame.dispose(); // Close the login window
            // If login is successful, start the main application
            new PharmacyManagementApp();
        } else {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Invalid Username or Password.");
        }
    }

    // Method to validate login credentials from the database
    private static boolean validateLogin(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement statement = DatabaseConnection.connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Error validating login: " + e.getMessage());
            return false;
        }
    }
}
