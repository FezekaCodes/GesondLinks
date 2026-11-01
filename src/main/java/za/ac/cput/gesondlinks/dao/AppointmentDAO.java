
package za.ac.cput.gesondlinks.dao;
import za.ac.cput.gesondlinks.database.DatabaseConnection;
import za.ac.cput.gesondlinks.domain.Appointment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Fezek
 */
public class AppointmentDAO {

    private static final Logger LOGGER = Logger.getLogger(AppointmentDAO.class.getName());

    public boolean insertAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointment (patient_id, doctor_id, appointment_date, appointment_time, status, notes) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDoctorId());
            stmt.setDate(3, appointment.getAppointmentDate());
            stmt.setTime(4, appointment.getAppointmentTime());
            stmt.setString(5, appointment.getStatus());
            stmt.setString(6, appointment.getNotes());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        appointment.setAppointmentId(generatedKeys.getInt(1));
                    }
                }
                LOGGER.info("Appointment inserted successfully");
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting appointment", e);
        }
        return false;
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.first_name as patient_first, p.last_name as patient_last, " +
                     "d.first_name as doctor_first, d.last_name as doctor_last " +
                     "FROM appointment a " +
                     "JOIN patient p ON a.patient_id = p.patient_id " +
                     "JOIN doctor d ON a.doctor_id = d.doctor_id " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Appointment appointment = mapResultSetToAppointment(rs);
                appointment.setPatientName(rs.getString("patient_first") + " " + rs.getString("patient_last"));
                appointment.setDoctorName(rs.getString("doctor_first") + " " + rs.getString("doctor_last"));
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all appointments", e);
        }
        return appointments;
    }

    public List<Appointment> getAppointmentsByPatient(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.first_name as patient_first, p.last_name as patient_last, " +
                     "d.first_name as doctor_first, d.last_name as doctor_last " +
                     "FROM appointment a " +
                     "JOIN patient p ON a.patient_id = p.patient_id " +
                     "JOIN doctor d ON a.doctor_id = d.doctor_id " +
                     "WHERE a.patient_id = ? ORDER BY a.appointment_date DESC";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Appointment appointment = mapResultSetToAppointment(rs);
                    appointment.setPatientName(rs.getString("patient_first") + " " + rs.getString("patient_last"));
                    appointment.setDoctorName(rs.getString("doctor_first") + " " + rs.getString("doctor_last"));
                    appointments.add(appointment);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting appointments by patient", e);
        }
        return appointments;
    }

    public boolean updateAppointment(Appointment appointment) {
        String sql = "UPDATE appointment SET patient_id = ?, doctor_id = ?, appointment_date = ?, " +
                     "appointment_time = ?, status = ?, notes = ? WHERE appointment_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDoctorId());
            stmt.setDate(3, appointment.getAppointmentDate());
            stmt.setTime(4, appointment.getAppointmentTime());
            stmt.setString(5, appointment.getStatus());
            stmt.setString(6, appointment.getNotes());
            stmt.setInt(7, appointment.getAppointmentId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Appointment updated successfully");
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating appointment", e);
        }
        return false;
    }

    public boolean deleteAppointment(int appointmentId) {
        String sql = "DELETE FROM appointment WHERE appointment_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Appointment deleted successfully: " + appointmentId);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting appointment", e);
        }
        return false;
    }

    public boolean updateAppointmentStatus(int appointmentId, String status) {
        String sql = "UPDATE appointment SET status = ? WHERE appointment_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, appointmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating appointment status", e);
        }
        return false;
    }

    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        return new Appointment(
            rs.getInt("appointment_id"),
            rs.getInt("patient_id"),
            rs.getInt("doctor_id"),
            rs.getDate("appointment_date"),
            rs.getTime("appointment_time"),
            rs.getString("status"),
            rs.getString("notes")
        );
    }
}  

