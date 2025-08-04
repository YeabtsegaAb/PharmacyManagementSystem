import javax.swing.*;

/**
 * Interface for all tab panels in the Pharmacy Management System.
 * Enforces a method to return the main JPanel for the tab.
 */
public interface TabPanel {
    /**
     * Returns the main panel for this tab.
     * @return JPanel representing the tab's main content
     */
    JPanel getMainPanel();
}
