// DateCalculator.java
// This utility class provides methods related to date calculations for pharmacy products.
// It is designed to be independent and does not interact with your database or UI.

import java.time.LocalDate; // Used for working with dates
import java.time.format.DateTimeFormatter; // Used for formatting dates into strings
import java.time.format.DateTimeParseException; // Used for handling errors when parsing dates

public class DateCalculator {

    /**
     * Calculates the expiry date of a product given its manufacturing date and shelf life in months.
     *
     * @param manufacturingDateString The manufacturing date in "YYYY-MM-DD" format.
     * @param shelfLifeMonths The shelf life of the product in months.
     * @return A string representing the expiry date in "YYYY-MM-DD" format,
     * or an error message if the input date is invalid.
     */
    public static String calculateExpiryDate(String manufacturingDateString, int shelfLifeMonths) {
        // Define the date format expected for input and output
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            // Parse the manufacturing date string into a LocalDate object
            LocalDate manufacturingDate = LocalDate.parse(manufacturingDateString, formatter);

            // Add the shelf life in months to the manufacturing date to get the expiry date
            LocalDate expiryDate = manufacturingDate.plusMonths(shelfLifeMonths);

            // Format the expiry date back into a string and return it
            return expiryDate.format(formatter);

        } catch (DateTimeParseException e) {
            // Handle cases where the input date string is not in the expected format
            return "Error: Invalid date format for manufacturing date. Please use YYYY-MM-DD.";
        } catch (Exception e) {
            // Catch any other unexpected errors
            return "An unexpected error occurred: " + e.getMessage();
        }
    }

    /**
     * Main method for testing the DateCalculator utility.
     * This method demonstrates how to use the calculateExpiryDate method.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Example 1: Valid calculation
        String manufacturingDate1 = "2024-01-15";
        int shelfLife1 = 24; // 24 months = 2 years
        String expiryDate1 = calculateExpiryDate(manufacturingDate1, shelfLife1);
        System.out.println("Manufacturing Date: " + manufacturingDate1 + ", Shelf Life: " + shelfLife1 + " months -> Expiry Date: " + expiryDate1);
        // Expected Output: Expiry Date: 2026-01-15

        // Example 2: Another valid calculation
        String manufacturingDate2 = "2023-06-01";
        int shelfLife2 = 18; // 18 months
        String expiryDate2 = calculateExpiryDate(manufacturingDate2, shelfLife2);
        System.out.println("Manufacturing Date: " + manufacturingDate2 + ", Shelf Life: " + shelfLife2 + " months -> Expiry Date: " + expiryDate2);
        // Expected Output: Expiry Date: 2024-12-01

        // Example 3: Invalid date format
        String manufacturingDate3 = "15-03-2024"; // Incorrect format
        int shelfLife3 = 12;
        String expiryDate3 = calculateExpiryDate(manufacturingDate3, shelfLife3);
        System.out.println("Manufacturing Date: " + manufacturingDate3 + ", Shelf Life: " + shelfLife3 + " months -> Expiry Date: " + expiryDate3);
        // Expected Output: Error: Invalid date format for manufacturing date. Please use YYYY-MM-DD.

        // Example 4: Edge case - 0 months shelf life
        String manufacturingDate4 = "2025-07-20";
        int shelfLife4 = 0;
        String expiryDate4 = calculateExpiryDate(manufacturingDate4, shelfLife4);
        System.out.println("Manufacturing Date: " + manufacturingDate4 + ", Shelf Life: " + shelfLife4 + " months -> Expiry Date: " + expiryDate4);
        // Expected Output: Expiry Date: 2025-07-20
    }
}
