import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Java Practical Integration Demo
 * Experiments 6 (Exception Handling), 7 (Multithreading),
 * 8 (File Handling), and 9 (JDBC with PostgreSQL).
 *
 * Requires PostgreSQL JDBC driver on the classpath.
 * IMPORTANT: This is a local practical demo. Do not commit real credentials.
 */
public class JavaPracticalUpdate {

    private static final Scanner scanner = new Scanner(System.in);

    // Shows the practical menu and exits cleanly when input ends.
    public static void main(String[] args) {
        int choice;

        try {
            do {
                System.out.println("\n===== JAVA PRACTICAL UPDATE =====");
                System.out.println("1. Experiment 6 - Exception Handling");
                System.out.println("2. Experiment 7 - Multithreading");
                System.out.println("3. Experiment 8 - File Handling");
                System.out.println("4. Experiment 9 - JDBC / PostgreSQL");
                System.out.println("5. Exit");
                choice = readInt("Enter choice: ");

                switch (choice) {
                    case 1:
                        exceptionHandlingDemo();
                        break;
                    case 2:
                        multithreadingDemo();
                        break;
                    case 3:
                        fileHandlingDemo();
                        break;
                    case 4:
                        jdbcMenu();
                        break;
                    case 5:
                        System.out.println("Exiting practical demo.");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } while (choice != 5);
        } catch (NoSuchElementException e) {
            System.out.println("\nInput ended. Exiting practical demo.");
        } finally {
            scanner.close();
        }
    }

    // =========================================================
    // EXPERIMENT 6 - EXCEPTION HANDLING
    // =========================================================

    // Demonstrates checked exceptions, multiple catches, and a finally block.
    private static void exceptionHandlingDemo() {
        System.out.println("\n--- Exception Handling Demo ---");

        try {
            int quantity = readInt("Enter quantity to buy: ");

            validateQuantity(quantity);

            System.out.println("Valid quantity: " + quantity);

        } catch (InvalidQuantityException e) {
            System.out.println("Custom exception caught: " + e.getMessage());

        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric input.");

        } finally {
            System.out.println("[finally] Quantity validation completed.");
        }

        // Multiple catch demonstration: NumberFormatException and
        // ArithmeticException are handled separately.
        try {
            int value = readInt("Enter a number for 100 / number: ");
            int result = 100 / value;
            System.out.println("Result: " + result);

        } catch (NumberFormatException e) {
            System.out.println("Please enter an integer.");

        } catch (ArithmeticException e) {
            System.out.println("Division by zero is not allowed.");
        }
    }

    // Rejects zero or negative order sizes with the demo's checked exception.
    private static void validateQuantity(int quantity)
            throws InvalidQuantityException {
        if (quantity <= 0) {
            throw new InvalidQuantityException(
                    "Quantity must be greater than zero.");
        }
    }

    static class InvalidQuantityException extends Exception {
        public InvalidQuantityException(String message) {
            super(message);
        }
    }

    // =========================================================
    // EXPERIMENT 7 - MULTITHREADING
    // =========================================================

    // Starts a ticker and two synchronized deposits, then prints the final balance.
    private static void multithreadingDemo() {
        System.out.println("\n--- Multithreading Demo ---");

        MarketPriceTicker ticker = new MarketPriceTicker(3);
        Thread tickerThread = new Thread(ticker, "MarketPriceTicker");

        tickerThread.start();

        try {
            tickerThread.join();
            System.out.println("Ticker thread completed.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Main thread was interrupted.");
        }

        // Synchronization demonstration
        SharedPortfolioBalance balance = new SharedPortfolioBalance();

        Thread t1 = new Thread(() -> balance.deposit(500), "Deposit-1");
        Thread t2 = new Thread(() -> balance.deposit(700), "Deposit-2");

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Final shared balance: Rs. " + balance.getBalance());
    }

    static class MarketPriceTicker implements Runnable {
        private final int iterations;

        MarketPriceTicker(int iterations) {
            this.iterations = iterations;
        }

        @Override
        public void run() {
            for (int i = 1; i <= iterations; i++) {
                System.out.println(
                        "[" + Thread.currentThread().getName()
                                + "] Market prices updated. Tick " + i);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Ticker interrupted.");
                    return;
                }
            }
        }
    }

    static class SharedPortfolioBalance {
        private double balance = 10000.0;

        public synchronized void deposit(double amount) {
            balance += amount;
            System.out.println(Thread.currentThread().getName()
                    + " deposited Rs. " + amount);
        }

        public synchronized double getBalance() {
            return balance;
        }
    }

    // =========================================================
    // EXPERIMENT 8 - FILE HANDLING
    // =========================================================

    // Demonstrates text, byte, and random-access file operations.
    private static void fileHandlingDemo() {
        System.out.println("\n--- File Handling Demo ---");

        Path reportPath = Path.of("portfolio-report.txt");
        String report = "Stock Market & Portfolio Management System\n"
                + "Investor: Demo User\n"
                + "Cash Balance: Rs. 10000.00\n"
                + "This report was written using Java file handling.\n";

        // Character stream writing and reading (BufferedWriter/Reader)
        try (BufferedWriter writer =
                     Files.newBufferedWriter(reportPath, StandardCharsets.UTF_8)) {
            writer.write(report);
            System.out.println("Report written to: " + reportPath.toAbsolutePath());

        } catch (IOException e) {
            System.out.println("File write error: " + e.getMessage());
        }

        try (BufferedReader reader =
                     Files.newBufferedReader(reportPath, StandardCharsets.UTF_8)) {
            String line;
            System.out.println("\n--- Report Contents ---");

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            System.out.println("File read error: " + e.getMessage());
        }

        // Byte stream demo
        byteStreamDemo();

        // RandomAccessFile demo
        randomAccessDemo();
    }

    // Writes a few bytes to a file and reads them back.
    private static void byteStreamDemo() {
        String fileName = "market-data.bin";

        try (FileOutputStream output = new FileOutputStream(fileName);
             BufferedOutputStream bufferedOutput =
                     new BufferedOutputStream(output)) {

            bufferedOutput.write(new byte[]{10, 20, 30, 40});
            System.out.println("Byte stream file written.");

        } catch (IOException e) {
            System.out.println("Byte stream write error: " + e.getMessage());
        }

        try (FileInputStream input = new FileInputStream(fileName);
             BufferedInputStream bufferedInput =
                     new BufferedInputStream(input)) {

            int value;
            System.out.print("Byte stream contents: ");

            while ((value = bufferedInput.read()) != -1) {
                System.out.print(value + " ");
            }
            System.out.println();

        } catch (IOException e) {
            System.out.println("Byte stream read error: " + e.getMessage());
        }
    }

    // Writes a fixed-size record, seeks to the start, and reads it back.
    private static void randomAccessDemo() {
        try (RandomAccessFile file =
                     new RandomAccessFile("market-record.dat", "rw")) {

            file.setLength(0); // Clear previous demo contents.
            file.writeInt(101);
            file.writeDouble(245.75);

            file.seek(0);

            int stockId = file.readInt();
            double price = file.readDouble();

            System.out.println("RandomAccessFile stock ID: " + stockId);
            System.out.println("RandomAccessFile price: " + price);
            System.out.println("File pointer: " + file.getFilePointer());
            System.out.println("File length: " + file.length());

        } catch (IOException e) {
            System.out.println("Random access error: " + e.getMessage());
        }
    }

    // =========================================================
    // EXPERIMENT 9 - JDBC WITH POSTGRESQL
    // =========================================================

    // Opens PostgreSQL using the configured URL and environment credentials.
    private static Connection getConnection() throws SQLException {
        String url = System.getenv().getOrDefault(
                "PG_URL", "jdbc:postgresql://localhost:5432/stock_market");
        String user = System.getenv().getOrDefault("PG_USER", "postgres");
        String password = System.getenv("PG_PASSWORD");
        if (password == null || password.isBlank()) {
            throw new SQLException("Set the PG_PASSWORD environment variable before starting the practical demo.");
        }
        return DriverManager.getConnection(url, user, password);
    }

    // Routes JDBC menu choices to the record operations below.
    private static void jdbcMenu() {
        try {
            createTable();

            int choice;
            do {
                System.out.println("\n--- PostgreSQL JDBC Menu ---");
                System.out.println("1. Insert practical record");
                System.out.println("2. View all records");
                System.out.println("3. Update record");
                System.out.println("4. Delete record");
                System.out.println("5. Test database connection");
                System.out.println("6. Return to main menu");

                choice = readInt("Enter choice: ");

                switch (choice) {
                    case 1:
                        insertRecord();
                        break;
                    case 2:
                        viewRecords();
                        break;
                    case 3:
                        updateRecord();
                        break;
                    case 4:
                        deleteRecord();
                        break;
                    case 5:
                        testConnection();
                        break;
                    case 6:
                        break;
                    default:
                        System.out.println("Invalid JDBC menu choice.");
                }
            } while (choice != 6);

        } catch (SQLException e) {
            System.out.println("JDBC setup error: " + e.getMessage());
            System.out.println("Check PostgreSQL service, database, and JDBC driver.");
        }
    }

    private static void testConnection() {
        try (Connection connection = getConnection()) {
            System.out.println("Connected to PostgreSQL successfully.");
            System.out.println("Database: " + connection.getCatalog());

        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    // Creates the demo table once; the database constraint also checks scores.
    private static void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS java_practical_records ("
                + "record_id SERIAL PRIMARY KEY, "
                + "student_name VARCHAR(100) NOT NULL, "
                + "experiment_name VARCHAR(150) NOT NULL, "
                + "score INTEGER NOT NULL CHECK (score >= 0 AND score <= 100)"
                + ")";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(sql);
            System.out.println("Table java_practical_records is ready.");
        }
    }

    // Validates user fields and inserts one row with a prepared statement.
    private static void insertRecord() {
        String sql = "INSERT INTO java_practical_records "
                + "(student_name, experiment_name, score) VALUES (?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            System.out.print("Enter student name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter experiment name: ");
            String experiment = scanner.nextLine().trim();

            int score = readInt("Enter score (0-100): ");

            if (name.isEmpty() || experiment.isEmpty()
                    || score < 0 || score > 100) {
                System.out.println("Invalid input. Name/experiment cannot be empty "
                        + "and score must be between 0 and 100.");
                return;
            }

            statement.setString(1, name);
            statement.setString(2, experiment);
            statement.setInt(3, score);

            int rows = statement.executeUpdate();
            System.out.println("Inserted rows: " + rows);

        } catch (SQLException e) {
            System.out.println("Insert failed: " + e.getMessage());
        }
    }

    // Prints all saved practical records in ID order.
    private static void viewRecords() {
        String sql = "SELECT record_id, student_name, experiment_name, score "
                + "FROM java_practical_records ORDER BY record_id";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("\nID | Student | Experiment | Score");
            System.out.println("--------------------------------");

            while (resultSet.next()) {
                System.out.println(
                        resultSet.getInt("record_id") + " | "
                                + resultSet.getString("student_name") + " | "
                                + resultSet.getString("experiment_name") + " | "
                                + resultSet.getInt("score"));
            }

        } catch (SQLException e) {
            System.out.println("Select failed: " + e.getMessage());
        }
    }

    // Changes the score for the requested record ID.
    private static void updateRecord() {
        String sql = "UPDATE java_practical_records SET score = ? WHERE record_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            int id = readInt("Enter record ID to update: ");
            int score = readInt("Enter new score (0-100): ");

            if (score < 0 || score > 100) {
                System.out.println("Score must be between 0 and 100.");
                return;
            }

            statement.setInt(1, score);
            statement.setInt(2, id);

            int rows = statement.executeUpdate();
            System.out.println(rows == 0
                    ? "No matching record found."
                    : "Record updated.");

        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    // Deletes the record with the requested ID.
    private static void deleteRecord() {
        String sql = "DELETE FROM java_practical_records WHERE record_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            int id = readInt("Enter record ID to delete: ");
            statement.setInt(1, id);

            int rows = statement.executeUpdate();
            System.out.println(rows == 0
                    ? "No matching record found."
                    : "Record deleted.");

        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }

    // Re-prompts on non-numeric input; main handles end-of-input.
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid integer.");
            }
        }
    }
}
