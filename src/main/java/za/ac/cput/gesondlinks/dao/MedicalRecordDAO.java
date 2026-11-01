
package za.ac.cput.gesondlinks.dao;
import za.ac.cput.gesondlinks.database.DatabaseConnection;
import za.ac.cput.gesondlinks.domain.MedicalRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Fezek
 */
public class MedicalRecordDAO {
 
    private static final Logger LOGGER = Logger.getLogger(MedicalRecordDAO.class.getName());

    // ========== CREATE ==========
    public boolean insertRecord(MedicalRecord record) {
        String sql = "INSERT INTO medical_records (patient_id, doctor_id, diagnosis, treatment, notes, record_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, record.getPatientId());
            stmt.setInt(2, record.getDoctorId());
            stmt.setString(3, record.getDiagnosis());
            stmt.setString(4, record.getTreatment());
            stmt.setString(5, record.getNotes());
            stmt.setDate(6, record.getRecordDate());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        record.setRecordId(generatedKeys.getInt(1));
                    }
                }
                LOGGER.info("Medical record inserted successfully for patient ID: " + record.getPatientId());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting medical record", e);
        }
        return false;
    }

    // ========== READ ==========
    public MedicalRecord getRecordById(int recordId) {
        String sql = "SELECT r.*, " +
                     "p.first_name as patient_first, p.last_name as patient_last, " +
                     "d.first_name as doctor_first, d.last_name as doctor_last " +
                     "FROM medical_records r " +
                     "JOIN patients p ON r.patient_id = p.patient_id " +
                     "JOIN doctors d ON r.doctor_id = d.doctor_id " +
                     "WHERE r.record_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MedicalRecord record = mapResultSetToRecord(rs);
                    record.setPatientName(rs.getString("patient_first") + " " + rs.getString("patient_last"));
                    record.setDoctorName(rs.getString("doctor_first") + " " + rs.getString("doctor_last"));
                    return record;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting record by ID: " + recordId, e);
        }
        return null;
    }

    public List<MedicalRecord> getAllRecords() {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT r.*, " +
                     "p.first_name as patient_first, p.last_name as patient_last, " +
                     "d.first_name as doctor_first, d.last_name as doctor_last " +
                     "FROM medical_records r " +
                     "JOIN patients p ON r.patient_id = p.patient_id " +
                     "JOIN doctors d ON r.doctor_id = d.doctor_id " +
                     "ORDER BY r.record_date DESC, r.record_id DESC";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                MedicalRecord record = mapResultSetToRecord(rs);
                record.setPatientName(rs.getString("patient_first") + " " + rs.getString("patient_last"));
                record.setDoctorName(rs.getString("doctor_first") + " " + rs.getString("doctor_last"));
                records.add(record);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all records", e);
        }
        return records;
    }

    public List<MedicalRecord> getRecordsByPatient(int patientId) {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT r.*, " +
                     "p.first_name as patient_first, p.last_name as patient_last, " +
                     "d.first_name as doctor_first, d.last_name as doctor_last " +
                     "FROM medical_records r " +
                     "JOIN patients p ON r.patient_id = p.patient_id " +
                     "JOIN doctors d ON r.doctor_id = d.doctor_id " +
                     "WHERE r.patient_id = ? " +
                     "ORDER BY r.record_date DESC, r.record_id DESC";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MedicalRecord record = mapResultSetToRecord(rs);
                    record.setPatientName(rs.getString("patient_first") + " " + rs.getString("patient_last"));
                    record.setDoctorName(rs.getString("doctor_first") + " " + rs.getString("doctor_last"));
                    records.add(record);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting records by patient: " + patientId, e);
        }
        return records;
    }

    public List<MedicalRecord> getRecordsByDoctor(int doctorId) {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT r.*, " +
                     "p.first_name as patient_first, p.last_name as patient_last, " +
                     "d.first_name as doctor_first, d.last_name as doctor_last " +
                     "FROM medical_records r " +
                     "JOIN patients p ON r.patient_id = p.patient_id " +
                     "JOIN doctors d ON r.doctor_id = d.doctor_id " +
                     "WHERE r.doctor_id = ? " +
                     "ORDER BY r.record_date DESC, r.record_id DESC";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MedicalRecord record = mapResultSetToRecord(rs);
                    record.setPatientName(rs.getString("patient_first") + " " + rs.getString("patient_last"));
                    record.setDoctorName(rs.getString("doctor_first") + " " + rs.getString("doctor_last"));
                    records.add(record);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting records by doctor: " + doctorId, e);
        }
        return records;
    }

    public List<MedicalRecord> getRecordsByDate(Date date) {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT r.*, " +
                     "p.first_name as patient_first, p.last_name as patient_last, " +
                     "d.first_name as doctor_first, d.last_name as doctor_last " +
                     "FROM medical_records r " +
                     "JOIN patients p ON r.patient_id = p.patient_id " +
                     "JOIN doctors d ON r.doctor_id = d.doctor_id " +
                     "WHERE DATE(r.record_date) = ? " +
                     "ORDER BY r.record_date DESC, r.record_id DESC";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setDate(1, date);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MedicalRecord record = mapResultSetToRecord(rs);
                    record.setPatientName(rs.getString("patient_first") + " " + rs.getString("patient_last"));
                    record.setDoctorName(rs.getString("doctor_first") + " " + rs.getString("doctor_last"));
                    records.add(record);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting records by date: " + date, e);
        }
        return records;
    }

    public List<MedicalRecord> getRecordsByDateRange(Date startDate, Date endDate) {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT r.*, " +
                     "p.first_name as patient_first, p.last_name as patient_last, " +
                     "d.first_name as doctor_first, d.last_name as doctor_last " +
                     "FROM medical_records r " +
                     "JOIN patients p ON r.patient_id = p.patient_id " +
                     "JOIN doctors d ON r.doctor_id = d.doctor_id " +
                     "WHERE r.record_date BETWEEN ? AND ? " +
                     "ORDER BY r.record_date DESC, r.record_id DESC";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MedicalRecord record = mapResultSetToRecord(rs);
                    record.setPatientName(rs.getString("patient_first") + " " + rs.getString("patient_last"));
                    record.setDoctorName(rs.getString("doctor_first") + " " + rs.getString("doctor_last"));
                    records.add(record);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting records by date range", e);
        }
        return records;
    }

    public List<MedicalRecord> getRecordsByDiagnosis(String diagnosis) {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT r.*, " +
                     "p.first_name as patient_first, p.last_name as patient_last, " +
                     "d.first_name as doctor_first, d.last_name as doctor_last " +
                     "FROM medical_records r " +
                     "JOIN patients p ON r.patient_id = p.patient_id " +
                     "JOIN doctors d ON r.doctor_id = d.doctor_id " +
                     "WHERE r.diagnosis LIKE ? " +
                     "ORDER BY r.record_date DESC, r.record_id DESC";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setString(1, "%" + diagnosis + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MedicalRecord record = mapResultSetToRecord(rs);
                    record.setPatientName(rs.getString("patient_first") + " " + rs.getString("patient_last"));
                    record.setDoctorName(rs.getString("doctor_first") + " " + rs.getString("doctor_last"));
                    records.add(record);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching records by diagnosis", e);
        }
        return records;
    }

    // ========== UPDATE ==========
    public boolean updateRecord(MedicalRecord record) {
        String sql = "UPDATE medical_records SET " +
                     "patient_id = ?, " +
                     "doctor_id = ?, " +
                     "diagnosis = ?, " +
                     "treatment = ?, " +
                     "notes = ?, " +
                     "record_date = ? " +
                     "WHERE record_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setInt(1, record.getPatientId());
            stmt.setInt(2, record.getDoctorId());
            stmt.setString(3, record.getDiagnosis());
            stmt.setString(4, record.getTreatment());
            stmt.setString(5, record.getNotes());
            stmt.setDate(6, record.getRecordDate());
            stmt.setInt(7, record.getRecordId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Medical record updated successfully: " + record.getRecordId());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating medical record: " + record.getRecordId(), e);
        }
        return false;
    }

    // ========== DELETE ==========
    public boolean deleteRecord(int recordId) {
        String sql = "DELETE FROM medical_records WHERE record_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Medical record deleted successfully: " + recordId);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting medical record: " + recordId, e);
        }
        return false;
    }

    public boolean deleteRecordsByPatient(int patientId) {
        String sql = "DELETE FROM medical_records WHERE patient_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            int affectedRows = stmt.executeUpdate();
            LOGGER.info("Deleted " + affectedRows + " medical records for patient: " + patientId);
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting records by patient: " + patientId, e);
        }
        return false;
    }

    // ========== STATISTICS ==========
    public int getTotalRecordCount() {
        String sql = "SELECT COUNT(*) FROM medical_records";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total record count", e);
        }
        return 0;
    }

    public int getRecordCountByPatient(int patientId) {
        String sql = "SELECT COUNT(*) FROM medical_records WHERE patient_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting record count by patient", e);
        }
        return 0;
    }

    public int getRecordCountByDateRange(Date startDate, Date endDate) {
        String sql = "SELECT COUNT(*) FROM medical_records WHERE record_date BETWEEN ? AND ?";
        try (PreparedStatement stmt = DatabaseConnection.prepareStatement(sql)) {
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting record count by date range", e);
        }
        return 0;
    }

    // ========== HELPER METHODS ==========
    private MedicalRecord mapResultSetToRecord(ResultSet rs) throws SQLException {
        return new MedicalRecord(
            rs.getInt("record_id"),
            rs.getInt("patient_id"),
            rs.getInt("doctor_id"),
            rs.getString("diagnosis"),
            rs.getString("treatment"),
            rs.getString("notes"),
            rs.getDate("record_date")
        );
    }

    public List<MedicalRecord> getAllMedicalRecords() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public MedicalRecord getMedicalRecordById(int recordId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void updateMedicalRecord(MedicalRecord record) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void addMedicalRecord(MedicalRecord record) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void deleteMedicalRecord(int recordId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public List<MedicalRecord> searchMedicalRecords(String searchText) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}  

