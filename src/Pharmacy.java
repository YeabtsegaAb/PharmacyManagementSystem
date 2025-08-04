public class Pharmacy {
    public static void main(String[] args) {
        // Connect to the database
        DatabaseConnection.connectDB();
        
        // Launch the login panel
        Login.createLoginPanel();
    }
}
