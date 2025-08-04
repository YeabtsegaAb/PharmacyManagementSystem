import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SupplierTab extends AbstractTabPanel {

    // Private instance variables for components
    private JPanel panel;
    private JLabel nameLabel, contactLabel, addressLabel, phoneLabel, emailLabel;
    private JTextField nameField, contactField, addressField, phoneField, emailField;
    private JButton addButton;

    @Override
    public JPanel getMainPanel() {
        panel = new JPanel(new GridLayout(8, 2, 10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 90, 20, 20));

        // Initialize Supplier Fields
        nameLabel = new JLabel("Supplier Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 28));
        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 20));

        contactLabel = new JLabel("Contact Name:");
        contactLabel.setFont(new Font("Arial", Font.BOLD, 28));
        contactField = new JTextField();
        contactField.setFont(new Font("Arial", Font.PLAIN, 20));

        addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Arial", Font.BOLD, 28));
        addressField = new JTextField();
        addressField.setFont(new Font("Arial", Font.PLAIN, 20));

        phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 28));
        phoneField = new JTextField();
        phoneField.setFont(new Font("Arial", Font.PLAIN, 20));

        emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 28));
        emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.PLAIN, 20));

        // Add Supplier Button
        addButton = new JButton("Add Supplier");
        addButton.setFont(new Font("Arial", Font.BOLD, 24));
        addButton.setBackground(new Color(100, 200, 100));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add components to the panel
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(contactLabel);
        panel.add(contactField);
        panel.add(addressLabel);
        panel.add(addressField);
        panel.add(phoneLabel);
        panel.add(phoneField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(new JLabel());  // Spacer
        panel.add(addButton);

        // Set action listener for the button
        addButton.addActionListener(e -> insertSupplierIntoDatabase(
                nameField.getText(),
                contactField.getText(),
                addressField.getText(),
                phoneField.getText(),
                emailField.getText(),
                nameField,
                contactField,
                addressField,
                phoneField,
                emailField
        ));

        return panel;
    }

    // Method to handle adding a supplier to the database
    private void insertSupplierIntoDatabase(String name, String contact, String address, String phone, String email,
                                            JTextField nameField, JTextField contactField, JTextField addressField, 
                                            JTextField phoneField, JTextField emailField) {

        // Validate fields
        if (name.isEmpty() || contact.isEmpty() || address.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Email validation
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            JOptionPane.showMessageDialog(null, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Phone number validation
        String phoneRegex = "^\\d{10}$"; // Adjust this regex if needed
        if (!phone.matches(phoneRegex)) {
            JOptionPane.showMessageDialog(null, "Invalid phone number format! Must be 10 digits.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call the database insertion method
        insertSupplierToDatabaseAndClearFields(name, contact, address, phone, email, nameField, contactField, addressField, phoneField, emailField);
    }

    // Method to insert the supplier data into the database and clear fields
    private void insertSupplierToDatabaseAndClearFields(String name, String contact, String address, String phone, String email,
                                                        JTextField nameField, JTextField contactField, JTextField addressField, 
                                                        JTextField phoneField, JTextField emailField) {
        // Database insertion
        try (PreparedStatement statement = DatabaseConnection.connection.prepareStatement(
                "INSERT INTO Suppliers (Name, PhoneNumber, ContactName, Address, Email) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setString(2, phone);
            statement.setString(3, contact);
            statement.setString(4, address);
            statement.setString(5, email);
            statement.executeUpdate();

            // Get the generated Supplier ID
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int supplierId = generatedKeys.getInt(1); // Get the Supplier ID
                // Display success message with Supplier ID
                displaySuccessMessage(supplierId, name, panel);
                // Log activity to file
                logActivity("Supplier added: Name=" + name + ", SupplierID=" + supplierId);
            }

            // Clear input fields after successful addition
            nameField.setText("");
            contactField.setText("");
            addressField.setText("");
            phoneField.setText("");
            emailField.setText("");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Log activity to activity_log.txt
    private void logActivity(String message) {
        try (java.io.FileWriter fw = new java.io.FileWriter("data/activity_log.txt", true);
             java.io.BufferedWriter bw = new java.io.BufferedWriter(fw)) {
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            bw.write("[" + timestamp + "] " + message);
            bw.newLine();
        } catch (Exception e) {
            // Optionally show error or ignore
        }
    }

    // Method to display success message after adding the supplier
    private void displaySuccessMessage(int supplierId, String name, JPanel panel) {
        String successMessage = "Supplier added successfully!" + "Supplier Name: " + name +"Your Supplier ID: " + supplierId;
        JOptionPane.showMessageDialog(panel, successMessage, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
