import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ProductTab extends AbstractTabPanel {

    private JPanel productsPanel;
    private JTextField productNameField;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> supplierComboBox;
    private JTextField unitPriceField;
    private JTextArea descriptionArea;
    private JButton addButton;

    @Override
    public JPanel getMainPanel() {
        productsPanel = new JPanel(new GridLayout(6, 2, 10, 20));
        productsPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        // Initializing UI Components directly
        JLabel nameLabel = new JLabel("Product Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 28));
        productNameField = new JTextField();
        productNameField.setFont(new Font("Arial", Font.PLAIN, 20));

        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 28));
        categoryComboBox = new JComboBox<>();

        JLabel supplierLabel = new JLabel("Supplier:");
        supplierLabel.setFont(new Font("Arial", Font.BOLD, 28));
        supplierComboBox = new JComboBox<>();

        JLabel priceLabel = new JLabel("Unit Price:");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 28));
        unitPriceField = new JTextField();
        unitPriceField.setFont(new Font("Arial", Font.PLAIN, 20));

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 28));
        descriptionArea = new JTextArea();
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 20));

        // Populate ComboBoxes from Database
        populateComboBoxFromDB("SELECT DISTINCT Category FROM Products", categoryComboBox);
        populateSupplierComboBox();

        // Create Add Product button
        addButton = new JButton("Add Product");
        addButton.setFont(new Font("Arial", Font.BOLD, 20));
        addButton.setBackground(new Color(100, 200, 100));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> handleAddProduct(
            productNameField.getText().trim(),
            (String) categoryComboBox.getSelectedItem(),
            unitPriceField.getText().trim(),
        descriptionArea.getText().trim(),
        (String) supplierComboBox.getSelectedItem()
));

        // Add components to the panel
        productsPanel.add(nameLabel);
        productsPanel.add(productNameField);
        productsPanel.add(categoryLabel);
        productsPanel.add(categoryComboBox);
        productsPanel.add(supplierLabel);
        productsPanel.add(supplierComboBox);
        productsPanel.add(priceLabel);
        productsPanel.add(unitPriceField);
        productsPanel.add(descriptionLabel);
        productsPanel.add(new JScrollPane(descriptionArea));
        productsPanel.add(new JLabel()); // Spacer
        productsPanel.add(addButton);

        return productsPanel; // Return the panel after creation
    }

    // Populates a combo box with values from a database query
    private void populateComboBoxFromDB(String query, JComboBox<String> comboBox) {
        try (PreparedStatement stmt = DatabaseConnection.connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                comboBox.addItem(rs.getString(1));
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }

    // Populates supplier combo box with supplier names
    private void populateSupplierComboBox() {
        try (PreparedStatement stmt = DatabaseConnection.connection.prepareStatement("SELECT Name FROM Suppliers");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                supplierComboBox.addItem(rs.getString("Name"));
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }

    // Handles adding a new product to the database
    private void handleAddProduct(String productName, String category, String unitPrice, String description, String supplierName) {
        // Validation: Ensure all fields are filled
        if (productName.isEmpty() || category == null || unitPrice.isEmpty() || description.isEmpty() || supplierName == null) {
            showErrorMessage("All fields are required!");
            return;
        }
    
        // Validate unit price
        double parsedPrice;
        try {
            parsedPrice = Double.parseDouble(unitPrice);
        } catch (NumberFormatException ex) {
            showErrorMessage("Invalid unit price format!");
            return;
        }
    
        // Get supplier ID
        int supplierID = getSupplierID(supplierName);
        if (supplierID == -1) {
            showErrorMessage("Supplier not found!");
            return;
        }
    
        // Insert product into database
        try (PreparedStatement stmt = DatabaseConnection.connection.prepareStatement(
                "INSERT INTO Products (ProductName, Category, UnitPrice, Description, SupplierID) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, productName);
            stmt.setString(2, category);
            stmt.setDouble(3, parsedPrice);
            stmt.setString(4, description);
            stmt.setInt(5, supplierID);
    
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(productsPanel, "Product added successfully!");
                resetFields();
            } else {
                showErrorMessage("Failed to add product.");
            }
        } catch (SQLException ex) {
            showErrorMessage("Database error: " + ex.getMessage());
        }
    }
    

    // Method to get Supplier ID based on supplier name
    private int getSupplierID(String supplierName) {
        int supplierID = -1; // Default to -1 if not found
        try (PreparedStatement stmt = DatabaseConnection.connection.prepareStatement("SELECT SupplierID FROM Suppliers WHERE Name = ?")) {
            stmt.setString(1, supplierName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    supplierID = rs.getInt("SupplierID");
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
        return supplierID;
    }

    // Displays an error message in a dialog box
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(productsPanel, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Clears all input fields after successful product addition
    private void resetFields() {
        productNameField.setText("");
        categoryComboBox.setSelectedIndex(0);
        supplierComboBox.setSelectedIndex(0);
        unitPriceField.setText("");
        descriptionArea.setText("");
    }
}
