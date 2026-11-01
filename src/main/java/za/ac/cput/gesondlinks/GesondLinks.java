
package za.ac.cput.gesondlinks;
import za.ac.cput.gesondlinks.database.DatabaseConnection;
import za.ac.cput.gesondlinks.views.LoginFrame;

import javax.swing.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Fezeka 216283272
 */
public class GesondLinks {
    
    private static final Logger LOGGER = Logger.getLogger(GesondLinks.class.getName());
  

    public static void main(String[] args) {
    
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not set system look and feel", e);
        }

        // Test database connection
        try {
            DatabaseConnection.getConnection();
            LOGGER.info("Database connection test successful");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection failed", e);
            int choice = JOptionPane.showConfirmDialog(null,
                "Could not connect to database.\nCheck if MySQL is running.\n\nContinue anyway?",
                "Database Error",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                System.exit(1);
            }
        }

        // Launch application
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}  
    

