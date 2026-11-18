/*
 * Concept Name                             | Line No.
 * -----------------------------------------+----------
 * Classes and Objects                      | Line 56
 * Console I/O (Scanner)                    | Line 59
 * Control Flow (Loops & Switch)            | Line 78
 * Polymorphism                             | Line 138
 * Exception Handling (Single try-catch)    | Line 169
 * Input Validation                         | Line 181
 * Exception Handling (Multiple try-catch)  | Line 236
 * Exception Handling (finally Block)       | Line 252
 * File Handling (Report Export)            | Line 322
 * Interface                                | Line 334
 * Abstraction (Abstract Class)             | Line 343
 * Inheritance & super                      | Line 381
 * Method Overriding (@Override)            | Line 394
 * Collections (List & ArrayList)           | Line 432
 * Collections (Vector)                     | Line 435
 * Compile-Time Polymorphism (Overload)     | Line 466
 * JDBC (Portfolio Persistence)             | Line 520
 * Threads (Multithreading)                 | Line 738
 * Static Variables                         | Line 768
 * Encapsulation & Data Hiding              | Line 771
 * Collections (Map & LinkedHashMap)        | Line 914
 * Collections (Set & HashSet)               | Line 919
 * Exception Handling (throws Keyword)      | Line 979
 * Exception Handling (throw Keyword)       | Line 982
 * Mutable Strings (StringBuffer)           | Line 1171
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

// Classes and Objects
public class Main {

    // Console I/O (Scanner)
    private static Scanner scanner;
    private static StockMarket market;
    private static Portfolio portfolio;

    // Connects to storage, loads the portfolio, and dispatches menu actions.
    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        try {
            JdbcPortfolioStore store = new JdbcPortfolioStore();
            market = new StockMarket();
            portfolio = store.loadPortfolio(market);

            System.out.println("Stock Market & Portfolio Management System");
            System.out.println("Investor: " + portfolio.getInvestorName() + " | Cash: Rs. " + portfolio.getCashBalance());

            demonstrateRuntimePolymorphism();

            int choice = -1;

            // Control Flow (Loops & Switch)
            do {
                displayMenu();
                choice = readChoice();

                switch (choice) {
                    case 1:
                        market.displayAllStocks();
                        break;
                    case 2:
                        handleSearch();
                        break;
                    case 3:
                        handleBuy();
                        break;
                    case 4:
                        handleSell();
                        break;
                    case 5:
                        portfolio.displayPortfolio();
                        break;
                    case 6:
                        portfolio.displayTransactions();
                        break;
                    case 7:
                        System.out.println("Total Portfolio Value: Rs. " + portfolio.calculateTotalPortfolioValue());
                        break;
                    case 8:
                        portfolio.displayProfitLoss();
                        break;
                    case 9:
                        handleWatchlist();
                        break;
                    case 10:
                        market.displayAuditLog();
                        break;
                    case 11:
                        System.out.print(portfolio.generateStringBufferReport());
                        break;
                    case 12:
                        handleMarketSimulation();
                        break;
                    case 13:
                        System.out.println("Exiting application. Goodbye!");
                        break;
                    case 14:
                        handleExportReport();
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 14.");
                        break;
                }
            } while (choice != 13);
        } catch (SQLException e) {
            System.err.println("Database error; the application stopped to avoid continuing with unsaved data: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    // Polymorphism
    private static void demonstrateRuntimePolymorphism() {
        System.out.println("\nRuntime Polymorphism Demonstration:");
        Security s1 = new Stock("TCS", "Tata Consultancy Services", 3200.0, "IT");
        Security s2 = new ETF("NIFTYBEES", "Nifty 50 ETF", 250.0, 0.05);
        s1.displayDetails();
        s2.displayDetails();

        // Interface reference demonstration
        Tradable t = s1;
        System.out.println("Tradable Interface Reference -> Symbol: " + t.getSymbol() + " | Price: Rs. " + t.getCurrentPrice());
    }

    private static void displayMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1. View Available Stocks");
        System.out.println("2. Search Stock");
        System.out.println("3. Buy Stock");
        System.out.println("4. Sell Stock");
        System.out.println("5. View Portfolio");
        System.out.println("6. View Transactions");
        System.out.println("7. Calculate Portfolio Value");
        System.out.println("8. Calculate Profit/Loss");
        System.out.println("9. Manage Watchlist");
        System.out.println("10. View Audit Log");
        System.out.println("11. Generate Report (StringBuffer)");
        System.out.println("12. Live Market Ticker Simulation (Threads)");
        System.out.println("13. Exit");
        System.out.println("14. Export Portfolio Report to File");
    }

    // Returns the selected menu number, or an invalid value when parsing fails.
    // Exception Handling (Single try-catch)
    private static int readChoice() {
        System.out.print("Enter Choice: ");
        if (!scanner.hasNextLine()) return 13;
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Reads the search mode and displays a matching security when found.
    private static void handleSearch() {
        System.out.println("1. Search by Symbol");
        System.out.println("2. Search by Symbol and Company Name");
        System.out.print("Select Option: ");
        if (!scanner.hasNextLine()) return;
        String opt = scanner.nextLine().trim();

        if (opt.equals("1")) {
            System.out.print("Enter Symbol: ");
            if (!scanner.hasNextLine()) return;
            String symbol = scanner.nextLine().trim();
            Security sec = market.findStock(symbol);
            if (sec != null) {
                sec.displayDetails();
            } else {
                System.out.println("Security not found in market.");
            }
        } else if (opt.equals("2")) {
            System.out.print("Enter Symbol: ");
            if (!scanner.hasNextLine()) return;
            String symbol = scanner.nextLine().trim();
            System.out.print("Enter Company Keyword: ");
            if (!scanner.hasNextLine()) return;
            String name = scanner.nextLine().trim();

            Security sec = market.findStock(symbol, name);
            if (sec != null) {
                sec.displayDetails();
            } else {
                System.out.println("No matching security found.");
            }
        } else {
            System.out.println("Invalid search option.");
        }
    }

    // Validates the selected security and quantity before recording a purchase.
    private static void handleBuy() throws SQLException {
        System.out.print("Enter Security Symbol to Buy: ");
        if (!scanner.hasNextLine()) return;
        String symbol = scanner.nextLine().trim();
        Security sec = market.findStock(symbol);
        if (sec == null) {
            System.out.println("Security '" + symbol + "' not found.");
            return;
        }

        // Exception Handling (Multiple try-catch)
        try {
            System.out.print("Enter Quantity to Buy: ");
            if (!scanner.hasNextLine()) return;
            String qtyInput = scanner.nextLine().trim();
            int quantity = Integer.parseInt(qtyInput);

            portfolio.buySecurity(sec, quantity);
            market.logAudit("BUY: " + quantity + " units of " + sec.getSymbol());
        } catch (NumberFormatException e) {
            System.out.println("Input Error: Quantity must be a valid integer number. (" + e.getMessage() + ")");
        } catch (InsufficientFundsException e) {
            System.out.println("Transaction Failed: " + e.getMessage());
        } catch (InvalidOrderException e) {
            System.out.println("Order Rejected: " + e.getMessage());
        } finally {
            // Exception Handling (finally Block)
            System.out.println("[Audit] Buy transaction processing cycle completed.");
        }
    }

    // Validates the selected security and quantity before recording a sale.
    private static void handleSell() throws SQLException {
        System.out.print("Enter Security Symbol to Sell: ");
        if (!scanner.hasNextLine()) return;
        String symbol = scanner.nextLine().trim();

        Security sec = market.findStock(symbol);
        if (sec == null) {
            System.out.println("Security '" + symbol + "' not found.");
            return;
        }

        try {
            System.out.print("Enter Quantity to Sell: ");
            if (!scanner.hasNextLine()) return;
            String qtyInput = scanner.nextLine().trim();
            int quantity = Integer.parseInt(qtyInput);

            portfolio.sellSecurity(sec, quantity);
            market.logAudit("SELL: " + quantity + " units of " + sec.getSymbol());
        } catch (NumberFormatException e) {
            System.out.println("Input Error: Quantity must be a valid integer number. (" + e.getMessage() + ")");
        } catch (InvalidOrderException e) {
            System.out.println("Order Rejected: " + e.getMessage());
        } finally {
            System.out.println("[Audit] Sell transaction processing cycle completed.");
        }
    }

    // Routes the user's watchlist choice to view, add, or remove operations.
    private static void handleWatchlist() throws SQLException {
        System.out.println("\nWatchlist Options:");
        System.out.println("1. View Watchlist");
        System.out.println("2. Add Symbol");
        System.out.println("3. Remove Symbol");
        System.out.print("Select Option: ");
        if (!scanner.hasNextLine()) return;
        String opt = scanner.nextLine().trim();

        if (opt.equals("1")) {
            portfolio.displayWatchlist(market);
        } else if (opt.equals("2")) {
            System.out.print("Enter Symbol to Add: ");
            if (!scanner.hasNextLine()) return;
            String sym = scanner.nextLine().trim();
            portfolio.addToWatchlist(sym);
        } else if (opt.equals("3")) {
            System.out.print("Enter Symbol to Remove: ");
            if (!scanner.hasNextLine()) return;
            String sym = scanner.nextLine().trim();
            portfolio.removeFromWatchlist(sym);
        } else {
            System.out.println("Invalid option.");
        }
    }

    // Runs the ticker and waits so menu actions cannot overlap its updates.
    private static void handleMarketSimulation() {
        System.out.println("\nLaunching live market ticker thread (3 ticks at 1-second intervals)...");
        MarketPriceTicker ticker = new MarketPriceTicker(market, 3);
        ticker.start();
        try {
            ticker.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted while waiting: " + e.getMessage());
        }
    }

    // Creates the report folder if needed, then writes the current summary.
    private static void handleExportReport() {
        Path reportPath = Path.of("reports", "portfolio-report.txt");
        try {
            Files.createDirectories(reportPath.getParent());
            Files.writeString(reportPath, portfolio.generateStringBufferReport(), StandardCharsets.UTF_8);
            System.out.println("Portfolio report exported to " + reportPath.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Could not export portfolio report: " + e.getMessage());
        }
    }
}

// Interface
interface Tradable {
    String getSymbol();
    String getCompanyName();
    double getCurrentPrice();
    void displayDetails();
    String getSecurityType();
}

// Abstraction (Abstract Class)
abstract class Security implements Tradable {
    protected String symbol;
    protected String companyName;
    protected double currentPrice;

    public Security(String symbol, String companyName, double currentPrice) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.currentPrice = currentPrice;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getCompanyName() {
        return companyName;
    }

    @Override
    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    @Override
    public abstract void displayDetails();

    @Override
    public abstract String getSecurityType();
}

// Inheritance & super
class Stock extends Security {
    private String sector;

    public Stock(String symbol, String companyName, double currentPrice, String sector) {
        super(symbol, companyName, currentPrice);
        this.sector = sector;
    }

    public String getSector() {
        return sector;
    }

    // Method Overriding (@Override)
    @Override
    public String getSecurityType() {
        return "Stock";
    }

    @Override
    public void displayDetails() {
        System.out.println("[Stock] Symbol: " + symbol + " | Name: " + companyName + " | Price: Rs. " + currentPrice + " | Sector: " + sector);
    }
}

// Subclass: ETF (Exchange Traded Fund)
class ETF extends Security {
    private double expenseRatio;

    public ETF(String symbol, String companyName, double currentPrice, double expenseRatio) {
        super(symbol, companyName, currentPrice);
        this.expenseRatio = expenseRatio;
    }

    public double getExpenseRatio() {
        return expenseRatio;
    }

    @Override
    public String getSecurityType() {
        return "ETF";
    }

    @Override
    public void displayDetails() {
        System.out.println("[ETF] Symbol: " + symbol + " | Name: " + companyName + " | Price: Rs. " + currentPrice + " | Expense Ratio: " + expenseRatio + "%");
    }
}

// StockMarket class
class StockMarket {
    // Collections (List & ArrayList)
    private List<Security> securities = new ArrayList<>();

    // Collections (Vector)
    private Vector<String> auditLog = new Vector<>();

    public StockMarket() {
        securities.add(new Stock("TCS", "Tata Consultancy Services", 3200.0, "IT"));
        securities.add(new Stock("INFY", "Infosys Ltd", 1550.0, "IT"));
        securities.add(new Stock("RELIANCE", "Reliance Industries Ltd", 2450.0, "Energy"));
        securities.add(new Stock("HDFCBANK", "HDFC Bank Ltd", 1600.0, "Banking"));
        securities.add(new Stock("ICICIBANK", "ICICI Bank Ltd", 950.0, "Banking"));
        securities.add(new Stock("TATAMOTORS", "Tata Motors Ltd", 650.0, "Automobile"));
        securities.add(new Stock("ITC", "ITC Limited", 440.0, "FMCG"));
        securities.add(new Stock("LT", "Larsen & Toubro Ltd", 2800.0, "Infrastructure"));
        securities.add(new Stock("BHARTIARTL", "Bharti Airtel Ltd", 850.0, "Telecom"));
        securities.add(new ETF("NIFTYBEES", "Nifty 50 ETF", 250.0, 0.05));
        securities.add(new ETF("GOLDBEES", "Nippon India Gold ETF", 55.0, 0.10));
        securities.add(new ETF("BANKBEES", "Nippon India Bank ETF", 430.0, 0.15));

        logAudit("Market opened with " + securities.size() + " securities.");
    }

    public void displayAllStocks() {
        System.out.println("\n--- Available Securities ---");
        for (Security s : securities) {
            s.displayDetails();
        }
    }

    public List<Security> getSecurities() {
        return Collections.unmodifiableList(securities);
    }

    // Compile-Time Polymorphism (Overload)
    // Finds a security by its case-insensitive ticker symbol.
    public Security findStock(String symbol) {
        if (symbol == null) return null;
        for (Security s : securities) {
            if (s.getSymbol().equalsIgnoreCase(symbol.trim())) {
                return s;
            }
        }
        return null;
    }

    // Matches both fields when supplied; otherwise searches whichever is present.
    public Security findStock(String symbol, String keyword) {
        for (Security s : securities) {
            boolean matchSymbol = (symbol != null && !symbol.isEmpty()) && s.getSymbol().equalsIgnoreCase(symbol.trim());
            boolean matchName = (keyword != null && !keyword.isEmpty()) && s.getCompanyName().toLowerCase().contains(keyword.trim().toLowerCase());

            if (symbol != null && !symbol.isEmpty() && keyword != null && !keyword.isEmpty()) {
                if (matchSymbol && matchName) return s;
            } else if (matchSymbol || matchName) {
                return s;
            }
        }
        return null;
    }

    // Updates every price together so readers do not see a partly updated list.
    public synchronized void updateRandomMarketPrices() {
        Random rand = new Random();
        for (Security s : securities) {
            double changePercent = (rand.nextDouble() * 4.0 - 2.0) / 100.0;
            double oldPrice = s.getCurrentPrice();
            double newPrice = Math.round((oldPrice * (1.0 + changePercent)) * 100.0) / 100.0;
            s.setCurrentPrice(newPrice);
        }
        logAudit("Market prices updated by live ticker thread.");
    }

    // Java Date & Time API
    public void logAudit(String event) {
        String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
        auditLog.add("[" + timestamp + "] " + event);
    }

    public void displayAuditLog() {
        System.out.println("\n--- Market Audit Log (Vector) ---");
        if (auditLog.isEmpty()) {
            System.out.println("No market events logged.");
        } else {
            for (String log : auditLog) {
                System.out.println(log);
            }
        }
    }
}

class JdbcPortfolioStore {
    private static final int PORTFOLIO_ID = 1;
    private final String url;
    private final String username;
    private final String password;

    public JdbcPortfolioStore() throws SQLException {
        url = System.getenv().getOrDefault("PG_URL", "jdbc:postgresql://localhost:5432/stock_portfolio");
        username = System.getenv().getOrDefault("PG_USER", "postgres");
        password = requiredEnvironmentVariable("PG_PASSWORD");
        initializeSchema();
    }

    private static String requiredEnvironmentVariable(String name) throws SQLException {
        String value = System.getenv(name);
        if (value == null || value.trim().isEmpty()) {
            throw new SQLException("Set the " + name + " environment variable before starting the application.");
        }
        return value;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // Creates the tables required by the portfolio app when they are missing.
    private void initializeSchema() throws SQLException {
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS portfolios (" +
                    "id INT PRIMARY KEY, investor_name VARCHAR(255) NOT NULL, initial_capital DOUBLE PRECISION NOT NULL, " +
                    "cash_balance DOUBLE PRECISION NOT NULL, realized_profit_loss DOUBLE PRECISION NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS portfolio_holdings (" +
                    "portfolio_id INT NOT NULL, symbol VARCHAR(32) NOT NULL, quantity INT NOT NULL, " +
                    "invested_cost DOUBLE PRECISION NOT NULL, PRIMARY KEY (portfolio_id, symbol))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS portfolio_transactions (" +
                    "id VARCHAR(32) PRIMARY KEY, portfolio_id INT NOT NULL, transaction_time VARCHAR(32) NOT NULL, " +
                    "transaction_type VARCHAR(16) NOT NULL, symbol VARCHAR(32) NOT NULL, quantity INT NOT NULL, " +
                    "price_per_unit DOUBLE PRECISION NOT NULL, total_amount DOUBLE PRECISION NOT NULL, " +
                    "post_balance DOUBLE PRECISION NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS portfolio_watchlist (" +
                    "portfolio_id INT NOT NULL, symbol VARCHAR(32) NOT NULL, " +
                    "PRIMARY KEY (portfolio_id, symbol))");
        }
    }

    // Loads the saved portfolio, or creates the default portfolio on first run.
    public Portfolio loadPortfolio(StockMarket market) throws SQLException {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT investor_name, initial_capital, cash_balance, realized_profit_loss " +
                             "FROM portfolios WHERE id = ?")) {
            statement.setInt(1, PORTFOLIO_ID);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    Portfolio portfolio = new Portfolio(100000.0, "Default Investor", this);
                    savePortfolio(portfolio);
                    return portfolio;
                }

                Portfolio portfolio = new Portfolio(
                        result.getString("investor_name"),
                        result.getDouble("initial_capital"),
                        result.getDouble("cash_balance"),
                        result.getDouble("realized_profit_loss"),
                        this);
                loadHoldings(connection, market, portfolio);
                loadTransactions(connection, portfolio);
                loadWatchlist(connection, portfolio);
                return portfolio;
            }
        }
    }

    private void loadHoldings(Connection connection, StockMarket market, Portfolio portfolio) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT symbol, quantity, invested_cost FROM portfolio_holdings WHERE portfolio_id = ?")) {
            statement.setInt(1, PORTFOLIO_ID);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    String symbol = result.getString("symbol");
                    Security security = market.findStock(symbol);
                    if (security == null) {
                        throw new SQLException("Stored holding '" + symbol + "' is not in the market catalog.");
                    }
                    portfolio.restoreHolding(new Holding(
                            security, result.getInt("quantity"), result.getDouble("invested_cost")));
                }
            }
        }
    }

    private void loadTransactions(Connection connection, Portfolio portfolio) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id, transaction_time, transaction_type, symbol, quantity, price_per_unit, total_amount, post_balance " +
                        "FROM portfolio_transactions WHERE portfolio_id = ? " +
                        "ORDER BY transaction_time, LENGTH(id), id")) {
            statement.setInt(1, PORTFOLIO_ID);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    try {
                        portfolio.restoreTransaction(new Transaction(
                                result.getString("id"),
                                result.getString("transaction_time"),
                                result.getString("transaction_type"),
                                result.getString("symbol"),
                                result.getInt("quantity"),
                                result.getDouble("price_per_unit"),
                                result.getDouble("total_amount"),
                                result.getDouble("post_balance")));
                    } catch (IllegalArgumentException e) {
                        throw new SQLException("Stored transaction has an invalid ID.", e);
                    }
                }
            }
        }
    }

    private void loadWatchlist(Connection connection, Portfolio portfolio) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT symbol FROM portfolio_watchlist WHERE portfolio_id = ?")) {
            statement.setInt(1, PORTFOLIO_ID);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    portfolio.restoreWatchlistSymbol(result.getString("symbol"));
                }
            }
        }
    }

    // shortcut: rewrites the one portfolio snapshot per change; use row-level writes if histories grow large.
    // Replaces the portfolio snapshot in one transaction to avoid partial saves.
    public void savePortfolio(Portfolio portfolio) throws SQLException {
        try (Connection connection = connect()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO portfolios (id, investor_name, initial_capital, cash_balance, realized_profit_loss) " +
                                "VALUES (?, ?, ?, ?, ?) ON CONFLICT (id) DO UPDATE SET " +
                                "investor_name = EXCLUDED.investor_name, initial_capital = EXCLUDED.initial_capital, " +
                                "cash_balance = EXCLUDED.cash_balance, realized_profit_loss = EXCLUDED.realized_profit_loss")) {
                    statement.setInt(1, PORTFOLIO_ID);
                    statement.setString(2, portfolio.getInvestorName());
                    statement.setDouble(3, portfolio.getInitialCapital());
                    statement.setDouble(4, portfolio.getCashBalance());
                    statement.setDouble(5, portfolio.getTotalRealizedProfitLoss());
                    statement.executeUpdate();
                }
                deleteRows(connection, "portfolio_holdings");
                deleteRows(connection, "portfolio_transactions");
                deleteRows(connection, "portfolio_watchlist");
                saveHoldings(connection, portfolio);
                saveTransactions(connection, portfolio);
                saveWatchlist(connection, portfolio);
                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        }
    }

    private void deleteRows(Connection connection, String table) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM " + table + " WHERE portfolio_id = ?")) {
            statement.setInt(1, PORTFOLIO_ID);
            statement.executeUpdate();
        }
    }

    private void saveHoldings(Connection connection, Portfolio portfolio) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO portfolio_holdings (portfolio_id, symbol, quantity, invested_cost) VALUES (?, ?, ?, ?)")) {
            for (Holding holding : portfolio.getHoldings()) {
                statement.setInt(1, PORTFOLIO_ID);
                statement.setString(2, holding.getSecurity().getSymbol());
                statement.setInt(3, holding.getQuantity());
                statement.setDouble(4, holding.getTotalInvestedCost());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void saveTransactions(Connection connection, Portfolio portfolio) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO portfolio_transactions " +
                        "(id, portfolio_id, transaction_time, transaction_type, symbol, quantity, price_per_unit, total_amount, post_balance) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            for (Transaction transaction : portfolio.getTransactions()) {
                statement.setString(1, transaction.getId());
                statement.setInt(2, PORTFOLIO_ID);
                statement.setString(3, transaction.getTimestamp());
                statement.setString(4, transaction.getType());
                statement.setString(5, transaction.getSymbol());
                statement.setInt(6, transaction.getQuantity());
                statement.setDouble(7, transaction.getPricePerUnit());
                statement.setDouble(8, transaction.getTotalAmount());
                statement.setDouble(9, transaction.getPostBalance());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void saveWatchlist(Connection connection, Portfolio portfolio) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO portfolio_watchlist (portfolio_id, symbol) VALUES (?, ?)")) {
            for (String symbol : portfolio.getWatchlist()) {
                statement.setInt(1, PORTFOLIO_ID);
                statement.setString(2, symbol);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }
}

// Threads (Multithreading)
class MarketPriceTicker extends Thread {
    private StockMarket market;
    private int iterations;

    public MarketPriceTicker(StockMarket market, int iterations) {
        super("MarketPriceTickerThread");
        this.market = market;
        this.iterations = iterations;
    }

    // Applies a fixed number of price updates, pausing between ticks.
    @Override
    public void run() {
        System.out.println("\n[" + getName() + "] Background market ticker started.");
        for (int i = 1; i <= iterations; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("[" + getName() + "] Ticker interrupted: " + e.getMessage());
                return;
            }
            market.updateRandomMarketPrices();
            System.out.println("[" + getName() + "] Update " + i + "/" + iterations + " broadcasted live market prices.");
        }
        System.out.println("[" + getName() + "] Background market ticker finished successfully.\n");
    }
}

// Transaction Model
class Transaction {
    // Static Variables
    private static int counter = 1001;

    // Encapsulation & Data Hiding
    private String id;
    private String timestamp;
    private String type;
    private String symbol;
    private int quantity;
    private double pricePerUnit;
    private double totalAmount;
    private double postBalance;

    public Transaction(String type, String symbol, int quantity, double pricePerUnit, double totalAmount, double postBalance) {
        this("TXN-" + (counter++),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()),
                type, symbol, quantity, pricePerUnit, totalAmount, postBalance);
    }

    public Transaction(String id, String timestamp, String type, String symbol, int quantity,
                       double pricePerUnit, double totalAmount, double postBalance) {
        this.id = id;
        this.timestamp = timestamp;
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.totalAmount = totalAmount;
        this.postBalance = postBalance;
        if (id.startsWith("TXN-")) {
            try {
                counter = Math.max(counter, Integer.parseInt(id.substring(4)) + 1);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid transaction ID: " + id, e);
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getPostBalance() {
        return postBalance;
    }

    @Override
    public String toString() {
        return id + " | " + timestamp + " | " + type + " | " + symbol + " | Qty: " + quantity + " | Price: Rs. " + pricePerUnit + " | Total: Rs. " + totalAmount + " | Balance: Rs. " + postBalance;
    }
}

// Holding Model
class Holding {
    private Security security;
    private int quantity;
    private double totalInvestedCost;

    public Holding(Security security, int quantity, double totalInvestedCost) {
        this.security = security;
        this.quantity = quantity;
        this.totalInvestedCost = totalInvestedCost;
    }

    public Security getSecurity() {
        return security;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalInvestedCost() {
        return totalInvestedCost;
    }

    public double getAverageBuyPrice() {
        return quantity > 0 ? (totalInvestedCost / quantity) : 0.0;
    }

    public double getCurrentValue() {
        return quantity * security.getCurrentPrice();
    }

    public double getUnrealizedProfitLoss() {
        return getCurrentValue() - totalInvestedCost;
    }

    public void addQuantity(int addQty, double addedCost) {
        this.quantity += addQty;
        this.totalInvestedCost += addedCost;
    }

    // Returns the average-cost basis removed from the holding for this sale.
    public double reduceQuantity(int sellQty) {
        double avgCost = getAverageBuyPrice();
        double costOfSoldShares = avgCost * sellQty;
        this.quantity -= sellQty;
        this.totalInvestedCost = Math.max(0.0, this.totalInvestedCost - costOfSoldShares);
        return costOfSoldShares;
    }
}

// Custom Checked Exceptions for Exception Handling Demonstration
class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

class InvalidOrderException extends Exception {
    public InvalidOrderException(String message) {
        super(message);
    }
}

// Portfolio Model
class Portfolio {
    private String investorName;
    private double initialCapital;
    private double cashBalance;
    private double totalRealizedProfitLoss = 0.0;

    // Collections (Map & LinkedHashMap)
    private Map<String, Holding> holdings = new LinkedHashMap<>();

    private List<Transaction> transactions = new ArrayList<>();

    // Collections (Set & HashSet)
    private Set<String> watchlist = new HashSet<>();
    private JdbcPortfolioStore store;

    public Portfolio(double initialBalance, String investorName, JdbcPortfolioStore store) {
        this.investorName = investorName;
        this.initialCapital = initialBalance;
        this.cashBalance = initialBalance;
        this.store = store;
    }

    public Portfolio(String investorName, double initialCapital, double cashBalance,
                     double totalRealizedProfitLoss, JdbcPortfolioStore store) {
        this.investorName = investorName;
        this.initialCapital = initialCapital;
        this.cashBalance = cashBalance;
        this.totalRealizedProfitLoss = totalRealizedProfitLoss;
        this.store = store;
    }

    public String getInvestorName() {
        return investorName;
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public double getInitialCapital() {
        return initialCapital;
    }

    public double getTotalRealizedProfitLoss() {
        return totalRealizedProfitLoss;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public Set<String> getWatchlist() {
        return watchlist;
    }

    public Iterable<Holding> getHoldings() {
        return holdings.values();
    }

    public void restoreHolding(Holding holding) {
        holdings.put(holding.getSecurity().getSymbol().toUpperCase(), holding);
    }

    public void restoreTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void restoreWatchlistSymbol(String symbol) {
        watchlist.add(symbol);
    }

    // Exception Handling (throws Keyword)
    // Updates cash, holdings, and history; restores memory if the database save fails.
    public void buySecurity(Security sec, int quantity) throws InsufficientFundsException, InvalidOrderException, SQLException {
        if (sec == null) {
            // Exception Handling (throw Keyword)
            throw new InvalidOrderException("Security cannot be null.");
        }
        if (quantity <= 0) {
            throw new InvalidOrderException("Quantity must be greater than zero. Provided: " + quantity);
        }

        double totalCost = sec.getCurrentPrice() * quantity;
        if (totalCost > cashBalance) {
            throw new InsufficientFundsException("Insufficient funds. Required: Rs. " + totalCost + ", Available: Rs. " + cashBalance);
        }

        String key = sec.getSymbol().toUpperCase();
        Holding holding = holdings.get(key);
        Holding previousHolding = holding == null ? null :
                new Holding(holding.getSecurity(), holding.getQuantity(), holding.getTotalInvestedCost());
        double previousCashBalance = cashBalance;
        cashBalance -= totalCost;
        if (holding != null) {
            holding.addQuantity(quantity, totalCost);
        } else {
            holdings.put(key, new Holding(sec, quantity, totalCost));
        }

        Transaction transaction = new Transaction("BUY", sec.getSymbol(), quantity, sec.getCurrentPrice(), totalCost, cashBalance);
        transactions.add(transaction);
        try {
            store.savePortfolio(this);
        } catch (SQLException e) {
            cashBalance = previousCashBalance;
            if (previousHolding == null) {
                holdings.remove(key);
            } else {
                holdings.put(key, previousHolding);
            }
            transactions.remove(transaction);
            throw e;
        }
        System.out.println("Successfully bought " + quantity + " units of " + sec.getSymbol() + ". Remaining Cash: Rs. " + cashBalance);
    }

    // Sells owned units using average cost, and rolls back memory if saving fails.
    public void sellSecurity(Security sec, int quantity) throws InvalidOrderException, SQLException {
        if (sec == null) {
            throw new InvalidOrderException("Security cannot be null.");
        }
        if (quantity <= 0) {
            throw new InvalidOrderException("Quantity must be greater than zero. Provided: " + quantity);
        }

        String key = sec.getSymbol().toUpperCase();
        Holding holding = holdings.get(key);
        if (holding == null) {
            throw new InvalidOrderException("You do not own any shares of " + sec.getSymbol() + " to sell.");
        }
        if (holding.getQuantity() < quantity) {
            throw new InvalidOrderException("Insufficient shares. You own " + holding.getQuantity() + " but attempted to sell " + quantity + ".");
        }

        Holding previousHolding = new Holding(holding.getSecurity(), holding.getQuantity(), holding.getTotalInvestedCost());
        double previousCashBalance = cashBalance;
        double previousRealizedProfitLoss = totalRealizedProfitLoss;
        double totalProceeds = sec.getCurrentPrice() * quantity;
        double costBasis = holding.reduceQuantity(quantity);
        double realizedGain = totalProceeds - costBasis;
        totalRealizedProfitLoss += realizedGain;
        cashBalance += totalProceeds;

        if (holding.getQuantity() == 0) {
            holdings.remove(key);
        }

        Transaction transaction = new Transaction("SELL", sec.getSymbol(), quantity, sec.getCurrentPrice(), totalProceeds, cashBalance);
        transactions.add(transaction);
        try {
            store.savePortfolio(this);
        } catch (SQLException e) {
            cashBalance = previousCashBalance;
            totalRealizedProfitLoss = previousRealizedProfitLoss;
            holdings.put(key, previousHolding);
            transactions.remove(transaction);
            throw e;
        }
        System.out.println("Successfully sold " + quantity + " units of " + sec.getSymbol() + ". Realized P/L: Rs. " + realizedGain + ". Updated Cash: Rs. " + cashBalance);
    }

    // Adds available cash to the current market value of all holdings.
    public double calculateTotalPortfolioValue() {
        double totalHoldingsValue = 0.0;
        for (Holding h : holdings.values()) {
            totalHoldingsValue += h.getCurrentValue();
        }
        return cashBalance + totalHoldingsValue;
    }

    public void displayPortfolio() {
        System.out.println("\n--- Portfolio: " + investorName + " ---");
        System.out.println("Cash Balance: Rs. " + cashBalance);
        System.out.println("Total Portfolio Value: Rs. " + calculateTotalPortfolioValue());

        if (holdings.isEmpty()) {
            System.out.println("No holdings currently held in portfolio.");
        } else {
            System.out.println("\nCurrent Holdings:");
            for (Holding h : holdings.values()) {
                Security s = h.getSecurity();
                System.out.println(s.getSymbol() + " (" + s.getCompanyName() + ") | Qty: " + h.getQuantity() +
                        " | Avg Buy: Rs. " + h.getAverageBuyPrice() +
                        " | Market Price: Rs. " + s.getCurrentPrice() +
                        " | Value: Rs. " + h.getCurrentValue() +
                        " | Gain/Loss: Rs. " + h.getUnrealizedProfitLoss());
            }
        }
    }

    public void displayTransactions() {
        System.out.println("\n--- Transaction History ---");
        if (transactions.isEmpty()) {
            System.out.println("No transactions executed yet.");
        } else {
            for (Transaction t : transactions) {
                System.out.println(t);
            }
        }
    }

    // Shows realized gains plus the current unrealized gain or loss.
    public void displayProfitLoss() {
        double totalHoldingsValue = 0.0;
        double totalInvested = 0.0;
        for (Holding h : holdings.values()) {
            totalHoldingsValue += h.getCurrentValue();
            totalInvested += h.getTotalInvestedCost();
        }

        double totalVal = cashBalance + totalHoldingsValue;
        double netPL = totalVal - initialCapital;

        System.out.println("\n--- Profit & Loss Summary ---");
        System.out.println("Initial Capital: Rs. " + initialCapital);
        System.out.println("Cash Balance: Rs. " + cashBalance);
        System.out.println("Holdings Value: Rs. " + totalHoldingsValue);
        System.out.println("Total Portfolio Value: Rs. " + totalVal);
        System.out.println("Realized Profit/Loss: Rs. " + totalRealizedProfitLoss);
        System.out.println("Unrealized Profit/Loss: Rs. " + (totalHoldingsValue - totalInvested));
        System.out.println("Overall Net Profit/Loss: Rs. " + netPL);
    }

    public void displayWatchlist(StockMarket market) {
        System.out.println("\n--- My Watchlist (HashSet) ---");
        if (watchlist.isEmpty()) {
            System.out.println("Watchlist is currently empty.");
        } else {
            for (String sym : watchlist) {
                Security sec = market.findStock(sym);
                if (sec != null) {
                    System.out.println(sec.getSymbol() + " - " + sec.getCompanyName() + " (" + sec.getSecurityType() + ") : Rs. " + sec.getCurrentPrice());
                } else {
                    System.out.println(sym + " (Not listed in market)");
                }
            }
        }
    }

    // Normalizes the symbol and persists the updated watchlist.
    public void addToWatchlist(String symbol) throws SQLException {
        if (symbol == null || symbol.trim().isEmpty()) {
            System.out.println("Symbol cannot be empty.");
            return;
        }
        String sym = symbol.trim().toUpperCase();
        if (watchlist.add(sym)) {
            store.savePortfolio(this);
            System.out.println("Added '" + sym + "' to watchlist.");
        } else {
            System.out.println("'" + sym + "' is already in watchlist.");
        }
    }

    // Normalizes the symbol and persists the updated watchlist.
    public void removeFromWatchlist(String symbol) throws SQLException {
        if (symbol == null || symbol.trim().isEmpty()) {
            System.out.println("Symbol cannot be empty.");
            return;
        }
        String sym = symbol.trim().toUpperCase();
        if (watchlist.remove(sym)) {
            store.savePortfolio(this);
            System.out.println("Removed '" + sym + "' from watchlist.");
        } else {
            System.out.println("'" + sym + "' is not present in watchlist.");
        }
    }

    // Mutable Strings (StringBuffer)
    // Builds a compact summary used by the console and report export.
    public String generateStringBufferReport() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n--- Summary Report (StringBuffer) ---\n");
        sb.append("Investor: ").append(investorName).append("\n");
        sb.append("Cash: Rs. ").append(cashBalance).append("\n");
        sb.append("Total Portfolio Value: Rs. ").append(calculateTotalPortfolioValue()).append("\n");
        sb.append("Total Holdings: ").append(holdings.size()).append("\n");
        sb.append("Total Transactions: ").append(transactions.size()).append("\n");
        return sb.toString();
    }
}
