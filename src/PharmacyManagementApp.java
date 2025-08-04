import javax.swing.*;
import java.awt.*;

public class PharmacyManagementApp {
    private JFrame frame;

    public PharmacyManagementApp() {
        displayGUI();
    }

    public void displayGUI() {
        frame = new JFrame("Pharmacy Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null); // Center the frame on the screen

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 255, 255)); // White background for the panel
        frame.add(mainPanel);

        // Create and style the tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 16)); // Match font style
        tabbedPane.setBackground(new Color(230, 240, 255)); // Light blue background for the tabbed pane

        // Add tabs for different functionalities
        tabbedPane.addTab("Add Customers", new CustomerTab().getMainPanel());
        tabbedPane.addTab("Add Products", new ProductTab().getMainPanel());
        tabbedPane.addTab("Add Suppliers", new SupplierTab().getMainPanel());
        tabbedPane.addTab("Add to Inventory", new InventoryTab().getMainPanel());
        tabbedPane.addTab("Make Sales", new SalesTab().getMainPanel());
        tabbedPane.addTab("History", new HistoryTab().getMainPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Add a header label for a consistent look
        JLabel headerLabel = new JLabel("Pharmacy Management System", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(new Color(0, 102, 204)); // Same color as welcome text from login
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        frame.setVisible(true);
    }
}
