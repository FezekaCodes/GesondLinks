
package za.ac.cput.gesondlinks.dao;
import za.ac.cput.gesondlinks.database.DatabaseConnection;
import za.ac.cput.gesondlinks.domain.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Fezek
 */
public class DoctorDAO {
  
    private static final Logger LOGGER = Logger.getLogger(DoctorDAO.class.getName());

    public boolean insertDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctor (first_name, last_name, specialization, phone_number, email, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, doctor.getFirstName());
            stmt.setString(2, doctor.getLastName());
            stmt.setString(3, doctor.getSpecialization());
            stmt.setString(4, doctor.getPhoneNumber());
            stmt.setString(5, doctor.getEmail());
            stmt.setBoolean(6, doctor.isActive());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        doctor.setDoctorId(generatedKeys.getInt(1));
                    }
                }
                LOGGER.info("Doctor inserted successfully: " + doctor.getFullName());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting doctor", e);
        }
        return false;
    }

    public Doctor getDoctorById(int doctorId) {
        String sql = "SELECT * FROM doctor WHERE doctor_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDoctor(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting doctor by ID", e);
        }
        return null;
    }

    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctor ORDER BY last_name, first_name";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                doctors.add(mapResultSetToDoctor(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all doctors", e);
        }
        return doctors;
    }

    public List<Doctor> getActiveDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctor WHERE is_active = true ORDER BY last_name, first_name";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                doctors.add(mapResultSetToDoctor(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting active doctors", e);
        }
        return doctors;
    }

    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctor SET first_name = ?, last_name = ?, specialization = ?, " +
                     "phone_number = ?, email = ?, is_active = ? WHERE doctor_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setString(1, doctor.getFirstName());
            stmt.setString(2, doctor.getLastName());
            stmt.setString(3, doctor.getSpecialization());
            stmt.setString(4, doctor.getPhoneNumber());
            stmt.setString(5, doctor.getEmail());
            stmt.setBoolean(6, doctor.isActive());
            stmt.setInt(7, doctor.getDoctorId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Doctor updated successfully: " + doctor.getFullName());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating doctor", e);
        }
        return false;
    }

    public boolean deleteDoctor(int doctorId) {
        if (hasAppointments(doctorId)) {
            LOGGER.warning("Cannot delete doctor with existing appointments: " + doctorId);
            return false;
        }
        String sql = "DELETE FROM doctor WHERE doctor_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Doctor deleted successfully: " + doctorId);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting doctor", e);
        }
        return false;
    }

    public boolean toggleDoctorStatus(int doctorId, boolean isActive) {
        String sql = "UPDATE doctor SET is_active = ? WHERE doctor_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setBoolean(1, isActive);
            stmt.setInt(2, doctorId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error toggling doctor status", e);
        }
        return false;
    }

    private boolean hasAppointments(int doctorId) {
        String sql = "SELECT COUNT(*) FROM appointment WHERE doctor_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking appointments", e);
        }
        return false;
    }

    private Doctor mapResultSetToDoctor(ResultSet rs) throws SQLException {
        return new Doctor(
            rs.getInt("doctor_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("specialization"),
            rs.getString("phone_number"),
            rs.getString("email"),
            rs.getBoolean("is_active")
        );
    }
}  

