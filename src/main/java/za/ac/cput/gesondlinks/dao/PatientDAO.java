
package za.ac.cput.gesondlinks.dao;
import za.ac.cput.gesondlinks.database.DatabaseConnection;
import za.ac.cput.gesondlinks.domain.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Fezek
 */
public class PatientDAO {
  
   
  
    private Connection connection;

    public PatientDAO() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(PatientDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addPatient(Patient patient) throws SQLException {
        String query = "INSERT INTO patient (first_name, last_name, date_of_birth, gender, email, address) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setDate(3, patient.getDateOfBirth());
            stmt.setString(4, patient.getGender());
            stmt.setString(5, patient.getEmail());
            stmt.setString(6, patient.getAddress());
           

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                patient.setPatientId(rs.getInt(1));
            }
        }
    }

    public Patient getPatientById(int patientId) throws SQLException {
        String query = "SELECT * FROM patient WHERE patient_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractPatientFromResultSet(rs);
            }
        }
        return null;
    }

    public List<Patient> getAllPatients() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String query = "SELECT * FROM patient ORDER BY last_name, first_name";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
            }
        }
        return patients;
    }

    public void updatePatient(Patient patient) throws SQLException {
        String query = "UPDATE patient SET first_name = ?, last_name = ?, date_of_birth = ?, gender = ?, email = ?, address = ? WHERE patient_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setString(3, patient.getGender());
            stmt.setString(4, patient.getEmail());
            stmt.setString(5, patient.getAddress());
            stmt.setInt(6, patient.getPatientId());

            stmt.executeUpdate();
        }
    }

    public void deletePatient(int patientId) throws SQLException {
        String query = "DELETE FROM patient WHERE patient_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, patientId);
            stmt.executeUpdate();
        }
    }

    public List<Patient> searchPatients(String searchText) throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String query = "SELECT * FROM patient WHERE first_name LIKE ? OR last_name LIKE ? OR email LIKE ? OR phone LIKE ? ORDER BY last_name, first_name";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + searchText + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
            }
        }
        return patients;
    }

    private Patient extractPatientFromResultSet(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setPatientId(rs.getInt("patient_id"));
        patient.setFirstName(rs.getString("first_name"));
        patient.setLastName(rs.getString("last_name"));
        patient.setDateOfBirth(rs.getDate("date_of_birth"));
        patient.setGender(rs.getString("gender"));
        patient.setEmail(rs.getString("email"));
        patient.setAddress(rs.getString("address"));
       
        return patient; 
      
  
}
}
   



