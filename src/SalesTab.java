import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.text.*;
import java.time.LocalDate;

public class SalesTab extends AbstractTabPanel {
    private JTextArea cartTextArea;
    private JTextField totalAmountField;
    private JList<String> customerList;
    private JList<String> productList;
    private JComboBox<Integer> quantityComboBox;
    private JTextField dateField;

    @Override
    public JPanel getMainPanel() {
        JPanel salePanel = new JPanel(new BorderLayout());
        salePanel.setBackground(Color.WHITE); // White background for the main panel

        // Input Panel (BoxLayout for better control of side-by-side arrangement)
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create a top panel for Customer and Product selection (side by side)
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        topPanel.setBackground(Color.WHITE);

        // Customer Selection Panel
        JPanel customerPanel = new JPanel(new BorderLayout());
        JLabel customerLabel = new JLabel("Customer:");
        customerLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Make label bold
        customerPanel.setBackground(Color.WHITE);
        customerList = new JList<>(loadCustomers());
        customerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane customerScroll = new JScrollPane(customerList);
        customerPanel.add(customerLabel, BorderLayout.NORTH);
        customerPanel.add(customerScroll, BorderLayout.CENTER);

        // Product Selection Panel
        JPanel productPanel = new JPanel(new BorderLayout());
        JLabel productLabel = new JLabel("Product:");
        productLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Make label bold
        productPanel.setBackground(Color.WHITE);
        productList = new JList<>(loadProducts());
        productList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane productScroll = new JScrollPane(productList);
        productPanel.add(productLabel, BorderLayout.NORTH);
        productPanel.add(productScroll, BorderLayout.CENTER);

        // Add Customer and Product Panels to topPanel
        topPanel.add(customerPanel);
        topPanel.add(productPanel);

        // Add topPanel to inputPanel
        inputPanel.add(topPanel);

        // Date selection with JTextField formatted as yyyy-MM-dd
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setBackground(Color.WHITE);
        JLabel dateLabel = new JLabel("Date (yyyy-MM-dd):");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Make label bold
        datePanel.add(dateLabel, BorderLayout.NORTH);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateField = new JTextField(10);
        dateField.setText(dateFormat.format(new java.util.Date())); // Set the default current date
        datePanel.add(dateField, BorderLayout.CENTER);
        inputPanel.add(datePanel);

        // Quantity selection (below Customer and Product)
        JPanel midPanel = new JPanel(new BorderLayout());
        midPanel.setBackground(Color.WHITE);
        JLabel quantityLabel = new JLabel("Select Quantity:");
        quantityLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Make label bold
        quantityComboBox = new JComboBox<>();
        for (int i = 1; i <= 100; i++) quantityComboBox.addItem(i);

        // Cart Panel
        JLabel cartLabel = new JLabel("Cart:");
        cartLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Make label bold
        cartTextArea = new JTextArea(5, 20);
        cartTextArea.setEditable(false);
        cartTextArea.setFont(new Font("Arial", Font.PLAIN, 14)); // Set text font for better readability
        JScrollPane cartScrollPane = new JScrollPane(cartTextArea);

        midPanel.add(quantityLabel, BorderLayout.NORTH);
        midPanel.add(quantityComboBox, BorderLayout.WEST);
        midPanel.add(cartLabel, BorderLayout.SOUTH);
        midPanel.add(cartScrollPane, BorderLayout.CENTER);
        inputPanel.add(midPanel);

        // Total Amount Panel (right side of the bottom panel)
        JPanel totalAmountPanel = new JPanel(new BorderLayout());
        totalAmountPanel.setBackground(Color.WHITE);
        JLabel totalAmountLabel = new JLabel("Total Amount:");
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Make label bold
        totalAmountField = new JTextField("0.0");
        totalAmountField.setEditable(false);
        totalAmountPanel.add(totalAmountLabel, BorderLayout.NORTH);
        totalAmountPanel.add(totalAmountField, BorderLayout.CENTER);

        // Create Cart and Total Amount panel (side by side)
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 10)); // Adjusted to use GridLayout for side-by-side
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(new JScrollPane(cartTextArea)); // Display cart
        bottomPanel.add(totalAmountPanel); // Total Amount panel

        // Add bottomPanel to inputPanel
        inputPanel.add(bottomPanel);

        // Action Buttons Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        actionPanel.setBackground(Color.WHITE);
        JButton addItemButton = new JButton("Add Item to Cart");
        JButton cancelItemButton = new JButton("Cancel Item");
        JButton cancelSaleButton = new JButton("Cancel Sale");
        JButton addSaleButton = new JButton("Add Sale");

        actionPanel.add(addItemButton);
        actionPanel.add(cancelItemButton);
        actionPanel.add(cancelSaleButton);
        actionPanel.add(addSaleButton);

        // Add Action Buttons to salePanel
        salePanel.add(inputPanel, BorderLayout.CENTER);
        salePanel.add(actionPanel, BorderLayout.SOUTH);

        // Action Listeners
        cancelItemButton.addActionListener(e -> cancelItemFromCart(cartTextArea, totalAmountField, salePanel));
        addItemButton.addActionListener(e -> addItemToCart(cartTextArea, totalAmountField, productList, quantityComboBox, salePanel));
        cancelSaleButton.addActionListener(e -> cancelSale(cartTextArea, totalAmountField));
        addSaleButton.addActionListener(e -> handleAddSale(cartTextArea, totalAmountField, customerList, productList, quantityComboBox, salePanel));

        return salePanel;
    }

    private void addItemToCart(JTextArea cartTextArea, JTextField totalAmountField, JList<String> productList, JComboBox<Integer> quantityComboBox, JPanel salePanel) {
        String productName = productList.getSelectedValue();
        Integer quantity = (Integer) quantityComboBox.getSelectedItem();
        
        if (productName == null || quantity == null) {
            JOptionPane.showMessageDialog(salePanel, "Please select a product and quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        double price = getProductPrice(productName);
        if (price == 0.0) {
            JOptionPane.showMessageDialog(salePanel, "Failed to retrieve price for the selected product.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        double totalItemPrice = price * quantity;
        String cartEntry = productName + " x" + quantity + " - $" + totalItemPrice;
    
        cartTextArea.append(cartEntry + "\n");
        try {
            double currentTotal = Double.parseDouble(totalAmountField.getText());
            totalAmountField.setText(String.valueOf(currentTotal + totalItemPrice));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(salePanel, "Invalid total amount field value.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private double getProductPrice(String productName) {
        String query = "SELECT UnitPrice FROM Products WHERE ProductName = ?";
        try (PreparedStatement pstmt = DatabaseConnection.connection.prepareStatement(query)) {
            pstmt.setString(1, productName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble("UnitPrice");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching product price: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return 0.0;
    }

    //Method To Cancel Item (a single item from the cart)
private void cancelItemFromCart(JTextArea cartTextArea, JTextField totalAmountField, JPanel salePanel) {
    String[] cartItems = cartTextArea.getText().split("\n"); // Split cart items into an array
    int selectedIndex = cartTextArea.getCaretPosition(); // Get the caret position in the cart

    if (cartItems.length == 0 || selectedIndex < 0) {
        JOptionPane.showMessageDialog(salePanel, "Please select an item to cancel.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Find the specific line in the text area for the selected index
    String selectedItem = "";
    for (String item : cartItems) {
        if (cartTextArea.getText().indexOf(item) <= selectedIndex && selectedIndex <= cartTextArea.getText().indexOf(item) + item.length()) {
            selectedItem = item;
            break;
        }
    }

    if (selectedItem.isEmpty()) {
        JOptionPane.showMessageDialog(salePanel, "No valid item selected.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Remove the selected item from the cart
    String updatedCart = "";  // Initialize as an empty string
    double removedPrice = 0;

    for (String item : cartItems) {
        if (!item.equals(selectedItem)) {
            updatedCart += item + "\n";  // Concatenate using '+' 
        } else {
            // Parse the price from the selected item
            String[] parts = item.split("- \\$");
            if (parts.length == 2) {
                try {
                    removedPrice = Double.parseDouble(parts[1]);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(salePanel, "Error parsing item price.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
    }

    cartTextArea.setText(updatedCart.trim()); // Update the cart display

    // Update the total amount
    try {
        double currentTotal = Double.parseDouble(totalAmountField.getText());
        totalAmountField.setText(String.valueOf(currentTotal - removedPrice));
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(salePanel, "Error updating total amount.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Reset caret position to the end
    cartTextArea.setCaretPosition(cartTextArea.getText().length());
}
private void cancelSale(JTextArea cartTextArea, JTextField totalAmountField) {
    cartTextArea.setText("");
    totalAmountField.setText("0.0");
}
private void handleAddSale(JTextArea cartTextArea, JTextField totalAmountField, JList<String> customerList, JList<String> productList, JComboBox<Integer> quantityComboBox, JPanel salePanel) {
    // This method handles the logic before calling addSale
    addSale(cartTextArea, totalAmountField, customerList, productList, quantityComboBox, salePanel);
}
// Method For add Sale To Sale Table adn Lidtener Table
private void addSale(JTextArea cartTextArea, JTextField totalAmountField, JList<String> customerList, JList<String> productList, JComboBox<Integer> quantityComboBox, JPanel salePanel) {
    String selectedCustomer = customerList.getSelectedValue();
    if (selectedCustomer == null) {
        JOptionPane.showMessageDialog(salePanel, "Please select a customer.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Fetch Customer ID based on the selected customer
    int customerId = getCustomerId(selectedCustomer);
    if (customerId == -1) {
        JOptionPane.showMessageDialog(salePanel, "Customer ID not found. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Fetch total amount from the total field
    String totalAmountText = totalAmountField.getText();
    double totalAmount;
    try {
        totalAmount = Double.parseDouble(totalAmountText);
        if (totalAmount <= 0) {
            JOptionPane.showMessageDialog(salePanel, "Total amount must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(salePanel, "Invalid total amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Fetch product details
    String productName = productList.getSelectedValue();
    if (productName == null) {
        JOptionPane.showMessageDialog(salePanel, "Please select a product.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    int quantity = (int) quantityComboBox.getSelectedItem();

    // Check if the product exists in the inventory
    if (!isProductInInventory(productName)) {
        JOptionPane.showMessageDialog(salePanel, "Error: Product is not in inventory. Sale cannot be processed.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Add sale to the database
    int saleId = addSaleToDatabase(customerId, totalAmount);
    if (saleId != -1) { // If sale is successfully recorded
        boolean detailsAdded = addToSalesDetails(saleId, productName, quantity, totalAmount);
        if (detailsAdded) {
            JOptionPane.showMessageDialog(salePanel, "Sale successfully recorded with details!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(salePanel, "Sale recorded, but details could not be saved.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        totalAmountField.setText("0.0"); // Reset the total amount field
    } else {
        JOptionPane.showMessageDialog(salePanel, "Failed to record the sale. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}


private int getCustomerId(String customerName) {
    String query = "SELECT CustomerID FROM Customers WHERE Name = ?";  // Assuming column name is 'Name'
    try (PreparedStatement pstmt = DatabaseConnection.connection.prepareStatement(query)) {
        pstmt.setString(1, customerName);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getInt("CustomerID");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error fetching customer ID: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return -1; // Return -1 if not found
}



private boolean addToSalesDetails(int saleId, String productName, int quantity, double totalAmount) {
    String query = "INSERT INTO SalesDetails (SaleID, ProductID, BatchNumber, Quantity, TotalAmount) VALUES (?, ?, ?, ?, ?)";
    try (PreparedStatement pstmt = DatabaseConnection.connection.prepareStatement(query)) {
        // Fetch Product ID based on selected product name
        int productId = getProductIdByName(productName); // Already fetched from product selection
        
        // Get Batch Number based on the selected product (assuming it's tied to the selected product)
        String batchNumber = getBatchNumberForProduct(productId); 
        //so farso fggd
        pstmt.setInt(1, saleId);
        pstmt.setInt(2, productId);
        pstmt.setString(3, batchNumber);
        pstmt.setInt(4, quantity);
        pstmt.setDouble(5, totalAmount); // Use already calculated total amount
        
        int rowsInserted = pstmt.executeUpdate();
        return rowsInserted > 0;
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error saving sale details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
}
private String getBatchNumberForProduct(int productId) {
    String batchNumber = null;
    String query = "SELECT BatchNumber FROM Inventory WHERE ProductID = ? LIMIT 1";

    try (PreparedStatement pstmt = DatabaseConnection.connection.prepareStatement(query)) {
        pstmt.setInt(1, productId);  // Set the ProductID in the query
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                batchNumber = rs.getString("BatchNumber");  // Fetch the batchNumber
            } else {
                JOptionPane.showMessageDialog(null, "No batch number found for the selected product", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error fetching batch number: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    return batchNumber;
}


// Modified to return saleId instead of boolean
private int addSaleToDatabase(int customerId, double totalAmount) {
    String query = "INSERT INTO Sales (CustomerID, SaleDate, TotalAmount) VALUES (?, ?, ?)";
    String currentDate = LocalDate.now().toString();

    try (PreparedStatement pstmt = DatabaseConnection.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        pstmt.setInt(1, customerId);
        pstmt.setString(2, currentDate);
        pstmt.setDouble(3, totalAmount);
        int rowsInserted = pstmt.executeUpdate();
        
        if (rowsInserted > 0) {
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1); // Return the generated SaleID
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error saving sale to database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
    return -1; // Indicate failure
}



// Method to check if a product exists in the inventory
    private boolean isProductInInventory(String productName) {
        
    String query = "SELECT COUNT(*) FROM Inventory WHERE ProductID = ?";
    try (PreparedStatement pstmt = DatabaseConnection.connection.prepareStatement(query)) {
        int productId = getProductIdByName(productName);
        pstmt.setInt(1, productId);

        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0; // Return true if product exists
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error checking product inventory: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
    return false; // Default to not found
}

private int getProductIdByName(String productName) {
    String query = "SELECT ProductID FROM Products WHERE ProductName = ?";
    try (PreparedStatement pstmt = DatabaseConnection.connection.prepareStatement(query)) {
        pstmt.setString(1, productName);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("ProductID");
            } else {
                JOptionPane.showMessageDialog(null, "Product not found: " + productName, "Error", JOptionPane.ERROR_MESSAGE);
                return -1;
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error fetching product ID: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        return -1;
    }
}
private String[] loadProducts() {
    try {
        Statement stmt = DatabaseConnection.connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT ProductName FROM Products");
        
        // Initialize an array with an initial size (e.g., 100)
        String[] products = new String[100];
        int index = 0;
        
        while (rs.next()) {
            if (index >= products.length) {
                // Resize the array when needed
                String[] newArray = new String[products.length * 2];
                System.arraycopy(products, 0, newArray, 0, products.length);
                products = newArray;
            }
            products[index++] = rs.getString("ProductName");
        }
        
        // Return a trimmed array containing only the used elements
        return Arrays.copyOf(products, index);
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error loading products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return new String[0];
}

private String[] loadCustomers() {
    try {
        Statement stmt = DatabaseConnection.connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT Name FROM Customers");
        
        // Initialize an array with an initial size (e.g., 100)
        String[] customers = new String[100];
        int index = 0;
        
        while (rs.next()) {
            if (index >= customers.length) {
                // Resize the array when needed
                String[] newArray = new String[customers.length * 2];
                System.arraycopy(customers, 0, newArray, 0, customers.length);
                customers = newArray;
            }
            customers[index++] = rs.getString("Name");
        }
        
        // Return a trimmed array containing only the used elements
        return Arrays.copyOf(customers, index);
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error loading customers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return new String[0];
}
}