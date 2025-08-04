import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
public class HistoryTab extends AbstractTabPanel {

    @Override
    public JPanel getMainPanel() {
        JPanel historyPanel = new JPanel();
        historyPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        historyPanel.setBackground(new Color(240, 240, 240));  // Light gray background
        
        // Create buttons
        JButton salesButton = new JButton("Sales History");
        JButton inventoryButton = new JButton("Inventory History");
        JButton productButton = new JButton("Product History");
        JButton customerButton = new JButton("Customer History");
        JButton supplierButton = new JButton("Supplier History");
        JButton purchasesButton = new JButton("Purchases History");
        
        // Set button fonts and sizes
        Font buttonFont = new Font("Arial", Font.BOLD, 16);  // Bigger font for better visibility
        salesButton.setFont(buttonFont);
        inventoryButton.setFont(buttonFont);
        productButton.setFont(buttonFont);
        customerButton.setFont(buttonFont);
        supplierButton.setFont(buttonFont);
        purchasesButton.setFont(buttonFont);
    
        // Add action listeners to call respective helper methods
        salesButton.addActionListener(e -> showSalesHistoryPopUp());
        inventoryButton.addActionListener(e -> showInventoryHistoryPopUp());
        productButton.addActionListener(e -> showProductHistoryPopUp());
        customerButton.addActionListener(e -> showCustomerHistoryPopUp());
        supplierButton.addActionListener(e -> showSupplierHistoryPopUp());
        purchasesButton.addActionListener(e -> showPurchasesHistoryPopUp());
        
        // Add buttons to the panel
        historyPanel.add(salesButton);
        historyPanel.add(inventoryButton);
        historyPanel.add(productButton);
        historyPanel.add(customerButton);
        historyPanel.add(supplierButton);
        historyPanel.add(purchasesButton);
        // Optionally, add a border with a title
        historyPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "History Options"));
        
        return historyPanel;
    }
    private void showSalesHistoryPopUp() {
        // Create the pop-up frame with the name 'popUpSales'
        JFrame popUpSales = new JFrame("Sales History");
        popUpSales.setSize(600, 400);  // Adjust the size for displaying the table
        popUpSales.setLocationRelativeTo(null);
        
        // Define columns for the JTable based on the Sales table (including SaleDate)
        String[] columnNames = {"Sale ID", "Customer ID", "Sale Date", "Total Amount"};
        
        // Create a list to store data rows
        ArrayList<String[]> dataList = new ArrayList<>();
        
        // Fetch the data from the Sales table in the database, sorted by SaleDate (descending)
        String query = "SELECT SaleId, CustomerID, SaleDate, TotalAmount FROM Sales ORDER BY SaleDate DESC";
        
        try (Statement stmt = DatabaseConnection.connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Add data rows directly to the list
            while (rs.next()) {
                String[] row = new String[4];
                row[0] = String.valueOf(rs.getInt("SaleId"));       // Sale ID
                row[1] = String.valueOf(rs.getInt("CustomerID"));   // Customer ID
                row[2] = rs.getString("SaleDate");         // Sale Date (converted to String)
                row[3] = String.valueOf(rs.getDouble("TotalAmount"));// Total Amount
                
                dataList.add(row);  // Add each row to the list
            }
            
        } catch (SQLException e) {
            // Handle any SQL exceptions
            JOptionPane.showMessageDialog(popUpSales, "Error fetching sales data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    
        // Convert the List to a 2D array for JTable
        String[][] data = new String[dataList.size()][4];
        data = dataList.toArray(data);
        
        // Create a JTable with the data and column names
        JTable salesTable = new JTable(data, columnNames);
        
        // Make the table non-editable
        salesTable.setEnabled(false);
        
        // Create a JScrollPane to make the table scrollable
        JScrollPane scrollPane = new JScrollPane(salesTable);
        
        // Create a JPanel to hold the scrollable table
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add the panel to the pop-up frame
        popUpSales.add(panel);
        popUpSales.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popUpSales.setVisible(true);
    }
    
    // Helper method for Inventory History pop-up
    private void showInventoryHistoryPopUp() {
        // Create the pop-up frame with the name 'popUpInventory'
        JFrame popUpInventory = new JFrame("Inventory History");
        popUpInventory.setSize(600, 400);  // Adjust the size for displaying the table
        popUpInventory.setLocationRelativeTo(null);
        
        // Define columns for the JTable based on the Inventory table
        String[] columnNames = {"Inventory ID", "Product ID", "Batch Number", "Quantity", "Expiry Date", "Set Price"};
        
        // Create a list to store data rows
        ArrayList<String[]> dataList = new ArrayList<>();
        
        // Fetch the data from the Inventory table in the database, sorted by ExpiryDate (ascending) to get closest expiry first
        String query = "SELECT InventoryId, ProductId, BatchNumber, Quantity, ExpiryDate, SetPrice FROM Inventory ORDER BY ExpiryDate ASC";
        
        try (Statement stmt = DatabaseConnection.connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Add data rows directly to the list
            while (rs.next()) {
                String[] row = new String[6];
                row[0] = String.valueOf(rs.getInt("InventoryId"));     // Inventory ID
                row[1] = String.valueOf(rs.getInt("ProductId"));       // Product ID
                row[2] = rs.getString("BatchNumber");                  // Batch Number
                row[3] = String.valueOf(rs.getInt("Quantity"));        // Quantity
                row[4] = rs.getString("ExpiryDate");          // Expiry Date (converted to String)
                row[5] = String.valueOf(rs.getDouble("SetPrice"));     // Set Price
                
                dataList.add(row);  // Add each row to the list
            }
            
        } catch (SQLException e) {
            // Handle any SQL exceptions
            JOptionPane.showMessageDialog(popUpInventory, "Error fetching inventory data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    
        // Convert the List to a 2D array for JTable
        String[][] data = new String[dataList.size()][6];
        data = dataList.toArray(data);
        
        // Create a JTable with the data and column names
        JTable inventoryTable = new JTable(data, columnNames);
        
        // Make the table non-editable
        inventoryTable.setEnabled(false);
        
        // Create a JScrollPane to make the table scrollable
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        
        // Create a JPanel to hold the scrollable table
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add the panel to the pop-up frame
        popUpInventory.add(panel);
        popUpInventory.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popUpInventory.setVisible(true);
    }
    
    // Helper method for Product History pop-up
    private void showProductHistoryPopUp() {
        // Create the pop-up frame with the name 'popUpProduct'
        JFrame popUpProduct = new JFrame("Product History");
        popUpProduct.setSize(600, 400);  // Adjust the size for displaying the table
        popUpProduct.setLocationRelativeTo(null);
        
        // Define columns for the JTable based on the Product table
        String[] columnNames = {"Product ID", "Product Name", "Category", "Unit Price", "Description"};
        
        // Create a list to store data rows
        ArrayList<String[]> dataList = new ArrayList<>();
        
        // Fetch the data from the Products table in the database, sorted by DateAdded (or any other relevant field)
        String query = "SELECT ProductId, ProductName, Category, UnitPrice, Description FROM Products ORDER BY ProductName"; // Adjust ORDER BY clause as needed
        
        try (Statement stmt = DatabaseConnection.connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Add data rows directly to the list
            while (rs.next()) {
                String[] row = new String[5];
                row[0] = String.valueOf(rs.getInt("ProductId"));   // Product ID
                row[1] = rs.getString("ProductName");               // Product Name
                row[2] = rs.getString("Category");                  // Category
                row[3] = String.valueOf(rs.getDouble("UnitPrice")); // Unit Price
                row[4] = rs.getString("Description");               // Description
                
                dataList.add(row);  // Add each row to the list
            }
            
        } catch (SQLException e) {
            // Handle any SQL exceptions
            JOptionPane.showMessageDialog(popUpProduct, "Error fetching product data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    
        // Convert the List to a 2D array for JTable
        String[][] data = new String[dataList.size()][5];
        data = dataList.toArray(data);
        
        // Create a JTable with the data and column names
        JTable productTable = new JTable(data, columnNames);
        
        // Make the table non-editable
        productTable.setEnabled(false);
        
        // Create a JScrollPane to make the table scrollable
        JScrollPane scrollPane = new JScrollPane(productTable);
        
        // Create a JPanel to hold the scrollable table
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add the panel to the pop-up frame
        popUpProduct.add(panel);
        popUpProduct.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popUpProduct.setVisible(true);
    }
    
    // Helper method for Customer History pop-up
    private void showCustomerHistoryPopUp() {
        // Create the pop-up frame with the name 'popUpCustomer'
        JFrame popUpCustomer = new JFrame("Customer History");
        popUpCustomer.setSize(600, 400);  // Adjust the size for displaying the table
        popUpCustomer.setLocationRelativeTo(null);
        
        // Define columns for the JTable based on the Customer table
        String[] columnNames = {"Customer ID", "Name", "Address", "Phone Number", "Email"};
        
        // Create a list to store data rows
        ArrayList<String[]> dataList = new ArrayList<>();
        
        // Fetch the data from the Customers table in the database
        String query = "SELECT CustomerId, Name, Address, PhoneNumber, Email FROM Customers";
        
        try (Statement stmt = DatabaseConnection.connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Add data rows directly to the list
            while (rs.next()) {
                String[] row = new String[5];
                row[0] = String.valueOf(rs.getInt("CustomerId"));   // Customer ID
                row[1] = rs.getString("Name");                      // Name
                row[2] = rs.getString("Address");                   // Address
                row[3] = rs.getString("PhoneNumber");               // Phone Number
                row[4] = rs.getString("Email");                     // Email
                
                dataList.add(row);  // Add each row to the list
            }
            
        } catch (SQLException e) {
            // Handle any SQL exceptions
            JOptionPane.showMessageDialog(popUpCustomer, "Error fetching customer data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    
        // Convert the List to a 2D array for JTable
        String[][] data = new String[dataList.size()][5];
        data = dataList.toArray(data);
        
        // Create a JTable with the data and column names
        JTable customerTable = new JTable(data, columnNames);
        
        // Make the table non-editable
        customerTable.setEnabled(false);
        
        // Create a JScrollPane to make the table scrollable
        JScrollPane scrollPane = new JScrollPane(customerTable);
        
        // Create a JPanel to hold the scrollable table
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add the panel to the pop-up frame
        popUpCustomer.add(panel);
        popUpCustomer.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popUpCustomer.setVisible(true);
    }
    
    // Helper method for Supplier History pop-up
    private void showSupplierHistoryPopUp() {
        // Create the pop-up frame with the name 'popUpSupplier'
        JFrame popUpSupplier = new JFrame("Supplier History");
        popUpSupplier.setSize(600, 400);  // Adjust the size for displaying the table
        popUpSupplier.setLocationRelativeTo(null);
        
        // Define columns for the JTable based on the Supplier table
        String[] columnNames = {"Supplier ID", "Name", "Phone Number", "Contact Name", "Address", "Email"};
        
        // Create a list to store data rows
        ArrayList<String[]> dataList = new ArrayList<>();
        
        // Fetch the data from the Suppliers table in the database
        String query = "SELECT SupplierId, Name, PhoneNumber, ContactName, Address, Email FROM Suppliers";
        
        try (Statement stmt = DatabaseConnection.connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Add data rows directly to the list
            while (rs.next()) {
                String[] row = new String[6];
                row[0] = String.valueOf(rs.getInt("SupplierId"));   // Supplier ID
                row[1] = rs.getString("Name");                      // Name
                row[2] = rs.getString("PhoneNumber");               // Phone Number
                row[3] = rs.getString("ContactName");               // Contact Name
                row[4] = rs.getString("Address");                   // Address
                row[5] = rs.getString("Email");                     // Email
                
                dataList.add(row);  // Add each row to the list
            }
            
        } catch (SQLException e) {
            // Handle any SQL exceptions
            JOptionPane.showMessageDialog(popUpSupplier, "Error fetching supplier data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    
        // Convert the List to a 2D array for JTable
        String[][] data = new String[dataList.size()][6];
        data = dataList.toArray(data);
        
        // Create a JTable with the data and column names
        JTable supplierTable = new JTable(data, columnNames);
        
        // Make the table non-editable
        supplierTable.setEnabled(false);
        
        // Create a JScrollPane to make the table scrollable
        JScrollPane scrollPane = new JScrollPane(supplierTable);
        
        // Create a JPanel to hold the scrollable table
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add the panel to the pop-up frame
        popUpSupplier.add(panel);
        popUpSupplier.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popUpSupplier.setVisible(true);
    }
    
    // Helper method for Purchases History pop-up
    private void showPurchasesHistoryPopUp() {
        // Create the pop-up frame with the name 'popUpPurchases'
        JFrame popUpPurchases = new JFrame("Purchases History");
        popUpPurchases.setSize(600, 400);  // Adjust the size for displaying the table
        popUpPurchases.setLocationRelativeTo(null);
        
        // Define columns for the JTable based on the Purchases and Suppliers tables
        String[] columnNames = {"Purchase ID", "Supplier Name", "Purchase Date", "Total Amount"};

        // Create a list to store data rows
        ArrayList<String[]> dataList = new ArrayList<>();

        // Fetch the data from Purchases joined with Suppliers for readable names, sorted by date descending
        String query = "SELECT p.PurchaseId, s.Name AS SupplierName, p.PurchaseDate, p.TotalAmount " +
                      "FROM Purchases p JOIN Suppliers s ON p.SupplierId = s.SupplierId " +
                      "ORDER BY p.PurchaseDate DESC";

        try (Statement stmt = DatabaseConnection.connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String[] row = new String[4];
                row[0] = String.valueOf(rs.getInt("PurchaseId"));   // Purchase ID
                row[1] = rs.getString("SupplierName");             // Supplier Name
                row[2] = rs.getString("PurchaseDate");             // Purchase Date
                row[3] = String.valueOf(rs.getDouble("TotalAmount")); // Total Amount
                dataList.add(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(popUpPurchases, "Error fetching purchase data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (dataList.isEmpty()) {
            JOptionPane.showMessageDialog(popUpPurchases, "No purchase history found.", "Success", JOptionPane.INFORMATION_MESSAGE);
            popUpPurchases.dispose();
            return;
        }

        // Convert the List to a 2D array for JTable
        String[][] data = new String[dataList.size()][4];
        data = dataList.toArray(data);

        // Create a JTable with the data and column names
        JTable purchasesTable = new JTable(data, columnNames);
        purchasesTable.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(purchasesTable);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        popUpPurchases.add(panel);
        popUpPurchases.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popUpPurchases.setVisible(true);
    }
    
}
