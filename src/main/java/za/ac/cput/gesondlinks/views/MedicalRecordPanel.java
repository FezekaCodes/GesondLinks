
package za.ac.cput.gesondlinks.views;
import za.ac.cput.gesondlinks.dao.MedicalRecordDAO;
import za.ac.cput.gesondlinks.dao.PatientDAO;
import za.ac.cput.gesondlinks.dao.DoctorDAO;
import za.ac.cput.gesondlinks.domain.MedicalRecord;
import za.ac.cput.gesondlinks.domain.Patient;
import za.ac.cput.gesondlinks.domain.Doctor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
/**
 *
 * @author Fezek
 */
public class MedicalRecordPanel extends JPanel {
  

    private JTable medicalRecordsTable;
    private JScrollPane scrollPane;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton, addButton, editButton, deleteButton, refreshButton;
    private JComboBox<Patient> patientComboBox;
    private JComboBox<Doctor> doctorComboBox;
    private JTextArea diagnosisArea, prescriptionArea, notesArea;
    private JTextField recordDateField;
    private JButton saveButton, cancelButton;
    private JPanel formPanel;
    private boolean isEditing = false;
    private MedicalRecord currentRecord = null;

    private MedicalRecordDAO medicalRecordDAO;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;

    public MedicalRecordPanel() {
        medicalRecordDAO = new MedicalRecordDAO();
        patientDAO = new PatientDAO();
        doctorDAO = new DoctorDAO();
        setLayout(new BorderLayout());
        
        medicalRecordsTable = new JTable(new DefaultTableModel());
        initComponents();
        loadMedicalRecords();
        populateComboBoxes();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Panel - Search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(new TitledBorder("Search Medical Records"));

        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        refreshButton = new JButton("Refresh");

        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Table
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(new TitledBorder("Medical Records List"));

        // Create table model with columns
        String[] columns = {"Record ID", "Patient", "Doctor", "Diagnosis", "Record Date", "Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        medicalRecordsTable = new JTable(tableModel);
        medicalRecordsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medicalRecordsTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(medicalRecordsTable);
        scrollPane.setPreferredSize(new Dimension(600, 250));

        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("Add Record");
        editButton = new JButton("Edit Record");
        deleteButton = new JButton("Delete Record");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel - Form
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Medical Record Details"));
        formPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Patient
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        patientComboBox = new JComboBox<>();
        patientComboBox.setPreferredSize(new Dimension(200, 25));
        formPanel.add(patientComboBox, gbc);

        // Doctor
        gbc.gridx = 2;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Doctor:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 0;
        doctorComboBox = new JComboBox<>();
        doctorComboBox.setPreferredSize(new Dimension(200, 25));
        formPanel.add(doctorComboBox, gbc);

        // Record Date
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Record Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        recordDateField = new JTextField(15);
        formPanel.add(recordDateField, gbc);

        // Diagnosis
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Diagnosis:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        diagnosisArea = new JTextArea(3, 30);
        JScrollPane diagnosisScroll = new JScrollPane(diagnosisArea);
        formPanel.add(diagnosisScroll, gbc);

        // Prescription
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Prescription:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        prescriptionArea = new JTextArea(3, 30);
        JScrollPane prescriptionScroll = new JScrollPane(prescriptionArea);
        formPanel.add(prescriptionScroll, gbc);

        // Notes
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        notesArea = new JTextArea(3, 30);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        formPanel.add(notesScroll, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel formButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        formButtonPanel.add(saveButton);
        formButtonPanel.add(cancelButton);
        formPanel.add(formButtonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);

        // Add Action Listeners
        addButton.addActionListener(e -> showForm(false));
        editButton.addActionListener(e -> {
            int selectedRow = medicalRecordsTable.getSelectedRow();
            if (selectedRow >= 0) {
                showForm(true);
                loadSelectedRecord(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a record to edit.");
            }
        });
        deleteButton.addActionListener(e -> deleteRecord());
        saveButton.addActionListener(e -> saveRecord());
        cancelButton.addActionListener(e -> hideForm());
        refreshButton.addActionListener(e -> loadMedicalRecords());
        searchButton.addActionListener(e -> searchRecords());
        searchField.addActionListener(e -> searchRecords());

        // Add selection listener to table
        medicalRecordsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = medicalRecordsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    // You can add code here to show details in a separate panel
                }
            }
        });
    }

    
    public void setBorder(Border border) {
        
     
        
        // OR if you want to set border on a specific child component:
        this.medicalRecordsTable.setBorder(border);
         this.formPanel.setBorder(border);
    }

    void loadMedicalRecords() {
        try {
            List<MedicalRecord> records = medicalRecordDAO.getAllMedicalRecords();
            tableModel.setRowCount(0);
            for (MedicalRecord record : records) {
                Patient patient = patientDAO.getPatientById(record.getPatientId());
                Doctor doctor = doctorDAO.getDoctorById(record.getDoctorId());
                tableModel.addRow(new Object[]{
                        record.getRecordId(),
                        patient != null ? patient.getFirstName() + " " + patient.getLastName() : "Unknown",
                        doctor != null ? doctor.getFirstName() + " " + doctor.getLastName() : "Unknown",
                        record.getDiagnosis(),
                        record.getRecordDate(),
                        record.getNotes()
                });
            }
        } catch (Exception e) {
            System.err.println("Error loading medical records: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading medical records: " + e.getMessage());
        }
    }

    private void populateComboBoxes() {
        try {
            // Clear existing items
            patientComboBox.removeAllItems();
            doctorComboBox.removeAllItems();

            List<Patient> patients = patientDAO.getAllPatients();
            for (Patient patient : patients) {
                patientComboBox.addItem(patient);
            }

            List<Doctor> doctors = doctorDAO.getAllDoctors();
            for (Doctor doctor : doctors) {
                doctorComboBox.addItem(doctor);
            }
        } catch (Exception e) {
            System.err.println("Error populating comboboxes: " + e.getMessage());
        }
    }

    private void showForm(boolean edit) {
        isEditing = edit;
        formPanel.setVisible(true);
        if (!edit) {
            clearForm();
            currentRecord = null;
        }
        // Set current date as default
        if (recordDateField.getText().isEmpty()) {
            java.util.Date today = new java.util.Date();
            recordDateField.setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(today));
        }
        // Update button text
        saveButton.setText(edit ? "Update" : "Save");
        formPanel.revalidate();
        formPanel.repaint();
    }

    private void hideForm() {
        formPanel.setVisible(false);
        clearForm();
        currentRecord = null;
        isEditing = false;
    }

    private void clearForm() {
        patientComboBox.setSelectedIndex(0);
        doctorComboBox.setSelectedIndex(0);
        recordDateField.setText("");
        diagnosisArea.setText("");
        prescriptionArea.setText("");
        notesArea.setText("");
    }

    private void loadSelectedRecord(int row) {
        try {
            int recordId = (int) tableModel.getValueAt(row, 0);
            currentRecord = medicalRecordDAO.getMedicalRecordById(recordId);
            if (currentRecord != null) {
                // Set the patient
                for (int i = 0; i < patientComboBox.getItemCount(); i++) {
                    Patient p = patientComboBox.getItemAt(i);
                    if (p.getPatientId() == currentRecord.getPatientId()) {
                        patientComboBox.setSelectedIndex(i);
                        break;
                    }
                }
                // Set the doctor
                for (int i = 0; i < doctorComboBox.getItemCount(); i++) {
                    Doctor d = doctorComboBox.getItemAt(i);
                    if (d.getDoctorId() == currentRecord.getDoctorId()) {
                        doctorComboBox.setSelectedIndex(i);
                        break;
                    }
                }
                recordDateField.setText(currentRecord.getRecordDate().toString());
                diagnosisArea.setText(currentRecord.getDiagnosis());
                prescriptionArea.setText(currentRecord.getPrescription());
                notesArea.setText(currentRecord.getNotes());
            }
        } catch (Exception e) {
            System.err.println("Error loading selected record: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading selected record: " + e.getMessage());
        }
    }

    private void saveRecord() {
        try {
            Patient selectedPatient = (Patient) patientComboBox.getSelectedItem();
            Doctor selectedDoctor = (Doctor) doctorComboBox.getSelectedItem();
            String recordDate = recordDateField.getText();
            String diagnosis = diagnosisArea.getText();
            String prescription = prescriptionArea.getText();
            String notes = notesArea.getText();

            // Validation
            if (selectedPatient == null || selectedDoctor == null) {
                JOptionPane.showMessageDialog(this, "Please select both patient and doctor.");
                return;
            }
            if (recordDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a record date.");
                return;
            }

            MedicalRecord record = new MedicalRecord();
            record.setPatientId(selectedPatient.getPatientId());
            record.setDoctorId(selectedDoctor.getDoctorId());
            record.setRecordDate(Date.valueOf(recordDate));
            record.setDiagnosis(diagnosis);
            record.setPrescription(prescription);
            record.setNotes(notes);

            if (isEditing && currentRecord != null) {
                record.setRecordId(currentRecord.getRecordId());
                medicalRecordDAO.updateMedicalRecord(record);
                JOptionPane.showMessageDialog(this, "Medical record updated successfully!");
            } else {
                medicalRecordDAO.addMedicalRecord(record);
                JOptionPane.showMessageDialog(this, "Medical record added successfully!");
            }

            hideForm();
            loadMedicalRecords();
            populateComboBoxes();
        } catch (Exception e) {
            System.err.println("Error saving medical record: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error saving medical record: " + e.getMessage());
        }
    }

    private void deleteRecord() {
        int selectedRow = medicalRecordsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a record to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this medical record?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int recordId = (int) tableModel.getValueAt(selectedRow, 0);
                medicalRecordDAO.deleteMedicalRecord(recordId);
                JOptionPane.showMessageDialog(this, "Medical record deleted successfully!");
                loadMedicalRecords();
            } catch (Exception e) {
                System.err.println("Error deleting medical record: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Error deleting medical record: " + e.getMessage());
            }
        }
    }

    private void searchRecords() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            loadMedicalRecords();
            return;
        }

        try {
            List<MedicalRecord> records = medicalRecordDAO.searchMedicalRecords(searchText);
            tableModel.setRowCount(0);
            for (MedicalRecord record : records) {
                Patient patient = patientDAO.getPatientById(record.getPatientId());
                Doctor doctor = doctorDAO.getDoctorById(record.getDoctorId());
                tableModel.addRow(new Object[]{
                        record.getRecordId(),
                        patient != null ? patient.getFirstName() + " " + patient.getLastName() : "Unknown",
                        doctor != null ? doctor.getFirstName() + " " + doctor.getLastName() : "Unknown",
                        record.getDiagnosis(),
                        record.getRecordDate(),
                        record.getNotes()
                });
            }
        } catch (Exception e) {
            System.err.println("Error searching medical records: " + e.getMessage());
        }
    }
}

