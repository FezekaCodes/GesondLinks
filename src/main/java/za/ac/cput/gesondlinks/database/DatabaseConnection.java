
package za.ac.cput.gesondlinks.database;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Fezek
 */
public class DatabaseConnection {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static Connection connection = null;

    private DatabaseConnection() {
        // Private constructor for Singleton pattern
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName(DatabaseConfig.DRIVER);
                connection = DriverManager.getConnection(
                    DatabaseConfig.URL, 
                    DatabaseConfig.USERNAME, 
                    DatabaseConfig.PASSWORD
                );
                LOGGER.info("Database connection established successfully.");
            } catch (ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found.", e);
                throw new SQLException("Driver not found", e);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database.", e);
                throw e;
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Database connection closed.");
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing connection", e);
            }
        }
    }

    // Execute Query with PreparedStatement
    public static PreparedStatement prepareStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    public static PreparedStatement prepareStatement(String sql, int returnGeneratedKeys) throws SQLException {
        return getConnection().prepareStatement(sql, returnGeneratedKeys);
    }

    // Close resources
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignore */ }
        try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignore */ }
        try { if (conn != null && !conn.isClosed()) conn.close(); } catch (SQLException e) { /* Ignore */ }
    }

    public static void close(Statement stmt) {
        try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignore */ }
    }
}

