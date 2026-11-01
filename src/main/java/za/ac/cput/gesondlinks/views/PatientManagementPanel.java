
package za.ac.cput.gesondlinks.views;
import za.ac.cput.gesondlinks.dao.PatientDAO;
import za.ac.cput.gesondlinks.domain.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
/**
 *
 * @author Fezeka 216283272
 */
public class PatientManagementPanel extends JPanel {

    private JTable patientsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton, addButton, editButton, deleteButton, refreshButton;
    private JPanel formPanel;
    private JTextField firstNameField, lastNameField, dobField, phoneField, emailField, addressField, emergencyContactField, emergencyPhoneField;
    private JComboBox<String> genderComboBox;
    private JButton saveButton, cancelButton;
    private boolean isEditing = false;
    private Patient currentPatient = null;
    private PatientDAO patientDAO;

    public PatientManagementPanel() {
        patientDAO = new PatientDAO();
        initComponents();
        loadPatients();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Panel - Search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(new TitledBorder("Search Patients"));

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
        centerPanel.setBorder(new TitledBorder("Patient List"));

        String[] columns = {"ID", "First Name", "Last Name", "Date of Birth", "Gender", "Phone", "Email", "Emergency Contact"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        patientsTable = new JTable(tableModel);
        patientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientsTable.getTableHeader().setReorderingAllowed(false);
        patientsTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(patientsTable);
        scrollPane.setPreferredSize(new Dimension(800, 300));

        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        addButton = new JButton("Add Patient");
        editButton = new JButton("Edit Patient");
        deleteButton = new JButton("Delete Patient");

        // Style buttons
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        editButton.setBackground(new Color(52, 152, 219));
        editButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel - Form (initially hidden)
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Patient Details"));
        formPanel.setVisible(false);
        formPanel.setBackground(new Color(248, 249, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: First Name and Last Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(15);
        formPanel.add(firstNameField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 3;
        lastNameField = new JTextField(15);
        formPanel.add(lastNameField, gbc);

        // Row 1: DOB and Gender
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        dobField = new JTextField(15);
        formPanel.add(dobField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 3;
        genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        formPanel.add(genderComboBox, gbc);

        // Row 2: Phone and Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(15);
        formPanel.add(phoneField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3;
        emailField = new JTextField(15);
        formPanel.add(emailField, gbc);

        // Row 3: Address
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        addressField = new JTextField(30);
        formPanel.add(addressField, gbc);
        gbc.gridwidth = 1;

        // Row 4: Emergency Contact
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Emergency Contact:"), gbc);
        gbc.gridx = 1;
        emergencyContactField = new JTextField(15);
        formPanel.add(emergencyContactField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Emergency Phone:"), gbc);
        gbc.gridx = 3;
        emergencyPhoneField = new JTextField(15);
        formPanel.add(emergencyPhoneField, gbc);

        // Row 5: Buttons
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel formButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        
        formButtonPanel.add(saveButton);
        formButtonPanel.add(cancelButton);
        formPanel.add(formButtonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);

        // Add Action Listeners
        addButton.addActionListener(e -> showForm(false));
        editButton.addActionListener(e -> {
            int selectedRow = patientsTable.getSelectedRow();
            if (selectedRow >= 0) {
                showForm(true);
                loadSelectedPatient(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a patient to edit.");
            }
        });
        deleteButton.addActionListener(e -> deletePatient());
        saveButton.addActionListener(e -> savePatient());
        cancelButton.addActionListener(e -> hideForm());
        refreshButton.addActionListener(e -> loadPatients());
        searchButton.addActionListener(e -> searchPatients());
        searchField.addActionListener(e -> searchPatients());
    }

    void loadPatients() {
        try {
            List<Patient> patients = patientDAO.getAllPatients();
            tableModel.setRowCount(0);
            for (Patient patient : patients) {
                tableModel.addRow(new Object[]{
                        patient.getPatientId(),
                        patient.getFirstName(),
                        patient.getLastName(),
                        patient.getDateOfBirth(),
                        patient.getGender(),
                        patient.getPhone(),
                        patient.getEmail(),
                        patient.getEmergencyContact()
                });
            }
        } catch (Exception e) {
            System.err.println("Error loading patients: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading patients: " + e.getMessage());
        }
    }

    private void showForm(boolean edit) {
        isEditing = edit;
        formPanel.setVisible(true);
        if (!edit) {
            clearForm();
            currentPatient = null;
        }
        saveButton.setText(edit ? "Update" : "Save");
        formPanel.revalidate();
        formPanel.repaint();
    }

    private void hideForm() {
        formPanel.setVisible(false);
        clearForm();
        currentPatient = null;
        isEditing = false;
    }

    private void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        dobField.setText("");
        genderComboBox.setSelectedIndex(0);
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        emergencyContactField.setText("");
        emergencyPhoneField.setText("");
    }

    private void loadSelectedPatient(int row) {
        try {
            int patientId = (int) tableModel.getValueAt(row, 0);
            currentPatient = patientDAO.getPatientById(patientId);
            if (currentPatient != null) {
                firstNameField.setText(currentPatient.getFirstName());
                lastNameField.setText(currentPatient.getLastName());
                dobField.setText(currentPatient.getDateOfBirth().toString());
                genderComboBox.setSelectedItem(currentPatient.getGender());
                phoneField.setText((String) currentPatient.getPhone());
                emailField.setText(currentPatient.getEmail());
                addressField.setText(currentPatient.getAddress());
                emergencyContactField.setText((String) currentPatient.getEmergencyContact());
                emergencyPhoneField.setText(currentPatient.getEmergencyPhone());
            }
        } catch (Exception e) {
            System.err.println("Error loading selected patient: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading patient details: " + e.getMessage());
        }
    }

    private void savePatient() {
        try {
            // Validate input
            if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "First Name and Last Name are required.");
                return;
            }

            if (dobField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Date of Birth is required.");
                return;
            }

            Patient patient = new Patient();
            patient.setFirstName(firstNameField.getText().trim());
            patient.setLastName(lastNameField.getText().trim());
            patient.setDateOfBirth(Date.valueOf(dobField.getText().trim()));
            patient.setGender((String) genderComboBox.getSelectedItem());
            patient.setPhone(phoneField.getText().trim());
            patient.setEmail(emailField.getText().trim());
            patient.setAddress(addressField.getText().trim());
            patient.setEmergencyContact(emergencyContactField.getText().trim());
            patient.setEmergencyPhone(emergencyPhoneField.getText().trim());

            if (isEditing && currentPatient != null) {
                patient.setPatientId(currentPatient.getPatientId());
                patientDAO.updatePatient(patient);
                JOptionPane.showMessageDialog(this, "Patient updated successfully!");
            } else {
                patientDAO.addPatient(patient);
                JOptionPane.showMessageDialog(this, "Patient added successfully!");
            }

            hideForm();
            loadPatients();
        } catch (Exception e) {
            System.err.println("Error saving patient: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error saving patient: " + e.getMessage());
        }
    }

    private void deletePatient() {
        int selectedRow = patientsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a patient to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this patient?\nThis action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int patientId = (int) tableModel.getValueAt(selectedRow, 0);
                patientDAO.deletePatient(patientId);
                JOptionPane.showMessageDialog(this, "Patient deleted successfully!");
                loadPatients();
            } catch (Exception e) {
                System.err.println("Error deleting patient: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Error deleting patient: " + e.getMessage());
            }
        }
    }

    private void searchPatients() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            loadPatients();
            return;
        }

        try {
            List<Patient> patients = patientDAO.searchPatients(searchText);
            tableModel.setRowCount(0);
            for (Patient patient : patients) {
                tableModel.addRow(new Object[]{
                        patient.getPatientId(),
                        patient.getFirstName(),
                        patient.getLastName(),
                        patient.getDateOfBirth(),
                        patient.getGender(),
                        patient.getPhone(),
                        patient.getEmail(),
                        patient.getEmergencyContact()
                });
            }
        } catch (Exception e) {
            System.err.println("Error searching patients: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error searching patients: " + e.getMessage());
        }
    }
}
    


