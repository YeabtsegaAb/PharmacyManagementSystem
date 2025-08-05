import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class InventoryTab extends AbstractTabPanel {

    @Override
    public JPanel getMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        panel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Inventory Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(50, 50, 50));
        panel.add(titleLabel);
        
        panel.add(Box.createVerticalStrut(10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);

        // Components
        JLabel productLabel = new JLabel("Product:");
        JComboBox<String> productComboBox = new JComboBox<>();
        populateProductList(productComboBox);

        JLabel batchNumberLabel = new JLabel("Batch Number:");
        JTextField batchNumberField = new JTextField(15);

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField(15);

        JLabel expiryDateLabel = new JLabel("Expiry Date (yyyy-MM-dd):");
        JTextField expiryDateField = new JTextField(15);

        JLabel priceLabel = new JLabel("Set Price:");
        JTextField priceField = new JTextField(15);

        JLabel supplierLabel = new JLabel("Supplier:");
        JComboBox<String> supplierComboBox = new JComboBox<>();
        populateSupplierList(supplierComboBox);

        // Add components to form panel
        formPanel.add(productLabel);
        formPanel.add(productComboBox);
        formPanel.add(batchNumberLabel);
        formPanel.add(batchNumberField);
        formPanel.add(quantityLabel);
        formPanel.add(quantityField);
        formPanel.add(expiryDateLabel);
        formPanel.add(expiryDateField);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(supplierLabel);
        formPanel.add(supplierComboBox);

        panel.add(formPanel);
        panel.add(Box.createVerticalStrut(10));

        // Add Button
        JButton addButton = new JButton("Add Inventory");
        addButton.setFont(new Font("Arial", Font.BOLD, 18));
        addButton.setBackground(new Color(30, 144, 255));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addButton);

        panel.add(buttonPanel);

        // Add inventory logic
        addButton.addActionListener(e -> {
            String productName = (String) productComboBox.getSelectedItem();
            String batchNumber = batchNumberField.getText().trim();
            String quantityStr = quantityField.getText().trim();
            String expiryDate = expiryDateField.getText().trim();
            String priceStr = priceField.getText().trim();
            String supplierName = (String) supplierComboBox.getSelectedItem();

            // Validation
            if (productName == null || productName.isEmpty() ||
                batchNumber.isEmpty() || quantityStr.isEmpty() || expiryDate.isEmpty() ||
                priceStr.isEmpty() || supplierName == null || supplierName.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int quantity;
            double setPrice;
            try {
                quantity = Integer.parseInt(quantityStr);
                setPrice = Double.parseDouble(priceStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Quantity and Set Price must be valid numbers!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Validate date format (yyyy-MM-dd)
            if (!expiryDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(panel, "Expiry Date must be in yyyy-MM-dd format!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Fetch ProductID and SupplierID
            int productId = getProductIdByName(productName);
            int supplierId = getSupplierIdByName(supplierName);
            if (productId == -1 || supplierId == -1) {
                JOptionPane.showMessageDialog(panel, "Invalid Product or Supplier!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert into Purchases table first
            double totalAmount = setPrice * quantity;
            String purchaseDate = java.time.LocalDate.now().toString();
            int purchaseId = -1;
            String purchaseSql = "INSERT INTO Purchases (SupplierId, PurchaseDate, TotalAmount) VALUES (?, ?, ?)";
            try (PreparedStatement purchaseStmt = DatabaseConnection.connection.prepareStatement(purchaseSql, Statement.RETURN_GENERATED_KEYS)) {
                purchaseStmt.setInt(1, supplierId);
                purchaseStmt.setString(2, purchaseDate);
                purchaseStmt.setDouble(3, totalAmount);
                int rows = purchaseStmt.executeUpdate();
                if (rows > 0) {
                    ResultSet keys = purchaseStmt.getGeneratedKeys();
                    if (keys.next()) {
                        purchaseId = keys.getInt(1);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panel, "Database error (Purchases): " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (purchaseId == -1) {
                JOptionPane.showMessageDialog(panel, "Failed to record purchase.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert into Inventory
            String sql = "INSERT INTO Inventory (ProductID, BatchNumber, Quantity, ExpiryDate, SetPrice, SupplierID) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = DatabaseConnection.connection.prepareStatement(sql)) {
                stmt.setInt(1, productId);
                stmt.setString(2, batchNumber);
                stmt.setInt(3, quantity);
                stmt.setString(4, expiryDate);
                stmt.setDouble(5, setPrice);
                stmt.setInt(6, supplierId);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(panel, "Inventory and purchase added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    batchNumberField.setText("");
                    quantityField.setText("");
                    expiryDateField.setText("");
                    priceField.setText("");
                    // Log activity to file
                    logActivity("Inventory added: Product=" + productName + ", BatchNumber=" + batchNumber + ", Quantity=" + quantity + ", Supplier=" + supplierName);
                } else {
                    JOptionPane.showMessageDialog(panel, "Failed to add inventory.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panel, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
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

    // Helper to get ProductID by name
    private int getProductIdByName(String productName) {
        String query = "SELECT ProductID FROM Products WHERE ProductName = ?";
        try (PreparedStatement stmt = DatabaseConnection.connection.prepareStatement(query)) {
            stmt.setString(1, productName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ProductID");
            }
        } catch (SQLException e) {
            // ignore, handled by caller
        }
        return -1;
    }

    // Helper to get SupplierID by name
    private int getSupplierIdByName(String supplierName) {
        String query = "SELECT SupplierID FROM Suppliers WHERE Name = ?";
        try (PreparedStatement stmt = DatabaseConnection.connection.prepareStatement(query)) {
            stmt.setString(1, supplierName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("SupplierID");
            }
        } catch (SQLException e) {
            // ignore, handled by caller
        }
        return -1;
    }

    private void populateProductList(JComboBox<String> productComboBox) {
        String query = "SELECT ProductName FROM Products";
        try (PreparedStatement stmt = DatabaseConnection.connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                productComboBox.addItem(rs.getString("ProductName"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching products: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateSupplierList(JComboBox<String> supplierComboBox) {
        String query = "SELECT Name FROM Suppliers";
        try (Statement stmt = DatabaseConnection.connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                supplierComboBox.addItem(rs.getString("Name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching suppliers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
