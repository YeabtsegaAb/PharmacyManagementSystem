import javax.swing.*;

/**
 * Abstract base class for all tab panels in the Pharmacy Management System.
 * Implements TabPanel and provides a common structure for all tabs.
 */
public abstract class AbstractTabPanel implements TabPanel {
    /**
     * Each subclass must implement this to return its main panel.
     */
    public abstract JPanel getMainPanel();
}
