
package za.ac.cput.gesondlinks.views;
import za.ac.cput.gesondlinks.dao.DoctorDAO;
import za.ac.cput.gesondlinks.domain.Doctor;
import za.ac.cput.gesondlinks.controllers.ValidationUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Fezeka Ntakumba 216283272
 */
public class DoctorManagementPanel extends JPanel {
  




    private static final Logger LOGGER = Logger.getLogger(DoctorManagementPanel.class.getName());
    private DoctorDAO doctorDAO;

    // UI Components
    private JTable doctorTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton, addButton, editButton, deleteButton, clearButton, toggleButton;

    // Form Components
    private JTextField firstNameField, lastNameField, specializationField, phoneField, emailField;
    private JCheckBox activeCheckBox;

    public DoctorManagementPanel() {
        doctorDAO = new DoctorDAO();
        initComponents();
        loadDoctors();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Search Panel
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        // Split Panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerLocation(550);

        // Table Panel
        JPanel tablePanel = createTablePanel();
        splitPane.setLeftComponent(tablePanel);

        // Form Panel
        JPanel formPanel = createFormPanel();
        splitPane.setRightComponent(formPanel);

        add(splitPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Search Doctors"));

        searchField = new JTextField(25);
        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchDoctors());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadDoctors());

        JButton activeOnlyButton = new JButton("Active Only");
        activeOnlyButton.addActionListener(e -> loadActiveDoctors());

        panel.add(new JLabel("Search:"));
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(activeOnlyButton);
        panel.add(refreshButton);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Name", "Specialization", "Phone", "Email", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        doctorTable = new JTable(tableModel);
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        doctorTable.getTableHeader().setReorderingAllowed(false);
        doctorTable.setRowHeight(25);

        doctorTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = doctorTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadDoctorToForm(selectedRow);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(doctorTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Doctor List"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Doctor Details"));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Row 0: First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:*"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(15);
        formPanel.add(firstNameField, gbc);

        // Row 1: Last Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Last Name:*"), gbc);
        gbc.gridx = 1;
        lastNameField = new JTextField(15);
        formPanel.add(lastNameField, gbc);

        // Row 2: Specialization
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Specialization:*"), gbc);
        gbc.gridx = 1;
        specializationField = new JTextField(15);
        formPanel.add(specializationField, gbc);

        // Row 3: Phone
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Phone:*"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(15);
        formPanel.add(phoneField, gbc);

        // Row 4: Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Email:*"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(15);
        formPanel.add(emailField, gbc);

        // Row 5: Active
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Active:"), gbc);
        gbc.gridx = 1;
        activeCheckBox = new JCheckBox();
        activeCheckBox.setSelected(true);
        formPanel.add(activeCheckBox, gbc);

        // Form Buttons
        JPanel formButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        addButton = new JButton("Add");
        addButton.setBackground(new Color(0, 153, 76));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addDoctor());

        editButton = new JButton("Update");
        editButton.setBackground(new Color(0, 102, 204));
        editButton.setForeground(Color.WHITE);
        editButton.setEnabled(false);
        editButton.addActionListener(e -> updateDoctor());

        deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(204, 0, 0));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteDoctor());

        toggleButton = new JButton("Toggle Status");
        toggleButton.setEnabled(false);
        toggleButton.addActionListener(e -> toggleDoctorStatus());

        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearForm());

        formButtonPanel.add(addButton);
        formButtonPanel.add(editButton);
        formButtonPanel.add(toggleButton);
        formButtonPanel.add(deleteButton);
        formButtonPanel.add(clearButton);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(formButtonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panel.setBorder(BorderFactory.createEtchedBorder());

        JButton exportButton = new JButton("Export to CSV");
        exportButton.addActionListener(e -> exportToCSV());

        panel.add(exportButton);

        return panel;
    }

    void loadDoctors() {
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        updateTable(doctors);
    }

    private void loadActiveDoctors() {
        List<Doctor> doctors = doctorDAO.getActiveDoctors();
        updateTable(doctors);
    }

    private void searchDoctors() {
        String searchTerm = searchField.getText().trim();
        // For simplicity, just filter the current list
        // In a real app, you'd have a search method in DAO
        loadDoctors();
        // Filter logic could be added here
    }

    private void updateTable(List<Doctor> doctors) {
        tableModel.setRowCount(0);
        for (Doctor doctor : doctors) {
            tableModel.addRow(new Object[]{
                doctor.getDoctorId(),
                doctor.getFullName(),
                doctor.getSpecialization(),
                doctor.getPhoneNumber(),
                doctor.getEmail(),
                doctor.isActive() ? "Active" : "Inactive"
            });
        }
        clearForm();
    }

    private void loadDoctorToForm(int rowIndex) {
        int doctorId = (int) tableModel.getValueAt(rowIndex, 0);
        Doctor doctor = doctorDAO.getDoctorById(doctorId);
        if (doctor != null) {
            firstNameField.setText(doctor.getFirstName());
            lastNameField.setText(doctor.getLastName());
            specializationField.setText(doctor.getSpecialization());
            phoneField.setText(doctor.getPhoneNumber());
            emailField.setText(doctor.getEmail());
            activeCheckBox.setSelected(doctor.isActive());

            addButton.setEnabled(false);
            editButton.setEnabled(true);
            deleteButton.setEnabled(true);
            toggleButton.setEnabled(true);
        }
    }

    private void addDoctor() {
        String validationError = validateForm();
        if (validationError != null) {
            JOptionPane.showMessageDialog(this, validationError, "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Doctor doctor = new Doctor();
        doctor.setFirstName(firstNameField.getText().trim());
        doctor.setLastName(lastNameField.getText().trim());
        doctor.setSpecialization(specializationField.getText().trim());
        doctor.setPhoneNumber(phoneField.getText().trim());
        doctor.setEmail(emailField.getText().trim());
        doctor.setActive(activeCheckBox.isSelected());

        if (doctorDAO.insertDoctor(doctor)) {
            JOptionPane.showMessageDialog(this, "Doctor added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadDoctors();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add doctor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);

        String validationError = validateForm();
        if (validationError != null) {
            JOptionPane.showMessageDialog(this, validationError, "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Doctor doctor = new Doctor();
        doctor.setDoctorId(doctorId);
        doctor.setFirstName(firstNameField.getText().trim());
        doctor.setLastName(lastNameField.getText().trim());
        doctor.setSpecialization(specializationField.getText().trim());
        doctor.setPhoneNumber(phoneField.getText().trim());
        doctor.setEmail(emailField.getText().trim());
        doctor.setActive(activeCheckBox.isSelected());

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to update this doctor?", 
            "Confirm Update", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (doctorDAO.updateDoctor(doctor)) {
                JOptionPane.showMessageDialog(this, "Doctor updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDoctors();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update doctor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);
        String doctorName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete doctor: " + doctorName + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (doctorDAO.deleteDoctor(doctorId)) {
                JOptionPane.showMessageDialog(this, "Doctor deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDoctors();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Cannot delete doctor with existing appointments.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleDoctorStatus() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow < 0) return;

        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);
        boolean newStatus = !activeCheckBox.isSelected();

        if (doctorDAO.toggleDoctorStatus(doctorId, newStatus)) {
            JOptionPane.showMessageDialog(this, 
                "Doctor status updated to: " + (newStatus ? "Active" : "Inactive"), 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            loadDoctors();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String validateForm() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String specialization = specializationField.getText().trim();

        return ValidationUtils.validateDoctor(firstName, lastName, email, phone, specialization);
    }

    private void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        specializationField.setText("");
        phoneField.setText("");
        emailField.setText("");
        activeCheckBox.setSelected(true);

        addButton.setEnabled(true);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        toggleButton.setEnabled(false);
        doctorTable.clearSelection();
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("doctors.csv"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getPath();
            try (java.io.PrintWriter writer = new java.io.PrintWriter(filePath)) {
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    writer.print(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1) writer.print(",");
                }
                writer.println();

                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        Object value = tableModel.getValueAt(row, col);
                        writer.print(value != null ? value.toString() : "");
                        if (col < tableModel.getColumnCount() - 1) writer.print(",");
                    }
                    writer.println();
                }

                JOptionPane.showMessageDialog(this, "Data exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Export error", e);
                JOptionPane.showMessageDialog(this, "Error exporting data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}  

