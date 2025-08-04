import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DatabaseConnection {
    public static final String DB_URL = "jdbc:sqlite:C:/PHARMACY/PHARMACY.db";
    public static Connection connection;

    public static void connectDB() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to database!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error connecting to database: " + e.getMessage());
            System.exit(1);
        }

    }
}
