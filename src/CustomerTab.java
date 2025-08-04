import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CustomerTab extends AbstractTabPanel {

    // Private instance variables for input panel, labels, and table
    private JPanel inputPanel;
    private JLabel nameLabel, addressLabel, phoneLabel, emailLabel;
    private JTable customerTable;
    private String[][] customerData = new String[0][5]; // 2D array to store customer data
    private String[] columnNames = {"ID", "Name", "Address", "Phone", "Email"};

    @Override
    public JPanel getMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 245, 245)); // Light gray background for the panel

        // Initialize the input panel
        inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBackground(new Color(245, 245, 245)); // Same background as panel
        inputPanel.setMaximumSize(new Dimension(400, 200)); // Set max width for input area

        // Labels with blue color
        nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Larger font size
        nameLabel.setForeground(new Color(0, 102, 204)); // Blue color

        addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Larger font size
        addressLabel.setForeground(new Color(0, 102, 204)); // Blue color

        phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Larger font size
        phoneLabel.setForeground(new Color(0, 102, 204)); // Blue color

        emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Larger font size
        emailLabel.setForeground(new Color(0, 102, 204)); // Blue color

        // Text fields for user input
        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();

        // Add components to the input panel
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(addressLabel);
        inputPanel.add(addressField);
        inputPanel.add(phoneLabel);
        inputPanel.add(phoneField);
        inputPanel.add(emailLabel);
        inputPanel.add(emailField);

        panel.add(inputPanel);

        // Add Customer Button with rounded corners and a nice background color
        JButton addButton = new JButton("Add Customer");
        addButton.setFont(new Font("Arial", Font.BOLD, 20));
        addButton.setBackground(new Color(70, 150, 70)); // Green background
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.setPreferredSize(new Dimension(200, 50));

        // Add action listener to the button
        addButton.addActionListener(e -> handleAddCustomer(nameField, addressField, phoneField, emailField));

        panel.add(Box.createVerticalStrut(20)); // Add space between form and button
        panel.add(addButton);

        // Table for displaying customer data
        customerTable = new JTable(customerData, columnNames);
        customerTable.setFont(new Font("Arial", Font.PLAIN, 16));
        customerTable.setRowHeight(25); // Increase row height for better readability
        JScrollPane scrollPane = new JScrollPane(customerTable);
        panel.add(scrollPane);

        return panel;
    }

    // Method to handle the customer addition process
    private void handleAddCustomer(JTextField nameField, JTextField addressField, JTextField phoneField, JTextField emailField) {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        // Validate input
        if (name.isEmpty() || address.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Basic phone validation (10 digits)
        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(null, "Phone number must be 10 digits.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Basic email validation
        if (!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            JOptionPane.showMessageDialog(null, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insert into the database and get the generated customer ID
        insertCustomerIntoDatabase(name, address, phone, email);
    }

    // Method to insert customer data into the database and retrieve the generated ID
    private void insertCustomerIntoDatabase(String name, String address, String phone, String email) {
        String sql = "INSERT INTO Customers (Name, Address, PhoneNumber, Email) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = DatabaseConnection.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, phone);
            stmt.setString(4, email);
            stmt.executeUpdate();

            // Get the generated customer ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int customerId = generatedKeys.getInt(1); // The auto-generated ID
                    // Show customer name and ID in the success popup
                    JOptionPane.showMessageDialog(null, "Customer '" + name + "' added successfully. Your ID number is " + customerId + ". Thank you!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Add the new customer to the table
                    addCustomerToTable(customerId, name, address, phone, email);
                } else {
                    JOptionPane.showMessageDialog(null, "Error: Unable to retrieve customer ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to add customer data to the table
    private void addCustomerToTable(int customerId, String name, String address, String phone, String email) {
        // Resize the array to add a new row
        String[][] newCustomerData = new String[customerData.length + 1][5];
        for (int i = 0; i < customerData.length; i++) {
            newCustomerData[i] = customerData[i];
        }

        // Add the new customer data
        newCustomerData[customerData.length] = new String[]{String.valueOf(customerId), name, address, phone, email};
        customerData = newCustomerData; // Update the array with new data

        // Recreate the table with updated data
        customerTable.setModel(new javax.swing.table.DefaultTableModel(customerData, columnNames));
    }
}
