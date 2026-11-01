
package za.ac.cput.gesondlinks.views;
import za.ac.cput.gesondlinks.dao.AppointmentDAO;
import za.ac.cput.gesondlinks.dao.DoctorDAO;
import za.ac.cput.gesondlinks.dao.PatientDAO;
import za.ac.cput.gesondlinks.domain.Appointment;
import za.ac.cput.gesondlinks.domain.Doctor;
import za.ac.cput.gesondlinks.domain.Patient;
import za.ac.cput.gesondlinks.controllers.ValidationUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Fezek
 */
public class AppointmentPanel extends JPanel {
    



    private static final Logger LOGGER = Logger.getLogger(AppointmentPanel.class.getName());
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;

    // UI Components
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JComboBox<Patient> patientCombo;
    private JComboBox<Doctor> doctorCombo;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;
    private JComboBox<String> statusCombo;
    private JTextArea notesArea;

    private JButton addButton, editButton, deleteButton, clearButton;

    public AppointmentPanel() {
        appointmentDAO = new AppointmentDAO();
        patientDAO = new PatientDAO();
        doctorDAO = new DoctorDAO();
        initComponents();
        loadAppointments();
        try {
            populateComboBoxes();
        } catch (SQLException ex) {
            Logger.getLogger(AppointmentPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Split Panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(550);

        // Table Panel
        JPanel tablePanel = createTablePanel();
        splitPane.setLeftComponent(tablePanel);

        // Form Panel
        JPanel formPanel = createFormPanel();
        splitPane.setRightComponent(formPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Patient", "Doctor", "Date", "Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = new JTable(tableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.getTableHeader().setReorderingAllowed(false);
        appointmentTable.setRowHeight(25);

        appointmentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = appointmentTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadAppointmentToForm(selectedRow);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Appointments"));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadAppointments());
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Appointment Details"));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Row 0: Patient
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Patient:*"), gbc);
        gbc.gridx = 1;
        patientCombo = new JComboBox<>();
        patientCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Patient) {
                    Patient p = (Patient) value;
                    setText(p.getFullName() + " (ID: " + p.getPatientId() + ")");
                } else {
                    setText(value != null ? value.toString() : "");
                }
                return this;
            }
        });
        formPanel.add(patientCombo, gbc);

        // Row 1: Doctor
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Doctor:*"), gbc);
        gbc.gridx = 1;
        doctorCombo = new JComboBox<>();
        doctorCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Doctor) {
                    Doctor d = (Doctor) value;
                    setText(d.getFullName() + " - " + d.getSpecialization());
                } else {
                    setText(value != null ? value.toString() : "");
                }
                return this;
            }
        });
        formPanel.add(doctorCombo, gbc);

        // Row 2: Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Date:*"), gbc);
        gbc.gridx = 1;
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        formPanel.add(dateSpinner, gbc);

        // Row 3: Time
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Time:*"), gbc);
        gbc.gridx = 1;
        timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        formPanel.add(timeSpinner, gbc);

        // Row 4: Status
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"Pending", "Confirmed", "Cancelled"});
        formPanel.add(statusCombo, gbc);

        // Row 5: Notes
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        notesArea = new JTextArea(3, 15);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        formPanel.add(notesScroll, gbc);

        // Form Buttons
        JPanel formButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        addButton = new JButton("Book Appointment");
        addButton.setBackground(new Color(0, 153, 76));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addAppointment());

        editButton = new JButton("Update");
        editButton.setBackground(new Color(0, 102, 204));
        editButton.setForeground(Color.WHITE);
        editButton.setEnabled(false);
        editButton.addActionListener(e -> updateAppointment());

        deleteButton = new JButton("Cancel");
        deleteButton.setBackground(new Color(204, 0, 0));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteAppointment());

        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearForm());

        formButtonPanel.add(addButton);
        formButtonPanel.add(editButton);
        formButtonPanel.add(deleteButton);
        formButtonPanel.add(clearButton);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(formButtonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void populateComboBoxes() throws SQLException {
        // Populate patients
        patientCombo.removeAllItems();
        List<Patient> patients = patientDAO.getAllPatients();
        for (Patient patient : patients) {
            patientCombo.addItem(patient);
        }

        // Populate active doctors
        doctorCombo.removeAllItems();
        List<Doctor> doctors = doctorDAO.getActiveDoctors();
        for (Doctor doctor : doctors) {
            doctorCombo.addItem(doctor);
        }
    }

    void loadAppointments() {
        List<Appointment> appointments = appointmentDAO.getAllAppointments();
        tableModel.setRowCount(0);
        for (Appointment appointment : appointments) {
            tableModel.addRow(new Object[]{
                appointment.getAppointmentId(),
                appointment.getPatientName() != null ? appointment.getPatientName() : "N/A",
                appointment.getDoctorName() != null ? appointment.getDoctorName() : "N/A",
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getStatus()
            });
        }
        clearForm();
    }

    private void loadAppointmentToForm(int rowIndex) {
        int appointmentId = (int) tableModel.getValueAt(rowIndex, 0);
        List<Appointment> appointments = appointmentDAO.getAllAppointments();
        Appointment appointment = appointments.stream()
            .filter(a -> a.getAppointmentId() == appointmentId)
            .findFirst()
            .orElse(null);

        if (appointment != null) {
            // Select patient
            for (int i = 0; i < patientCombo.getItemCount(); i++) {
                Patient p = patientCombo.getItemAt(i);
                if (p != null && p.getPatientId() == appointment.getPatientId()) {
                    patientCombo.setSelectedIndex(i);
                    break;
                }
            }

            // Select doctor
            for (int i = 0; i < doctorCombo.getItemCount(); i++) {
                Doctor d = doctorCombo.getItemAt(i);
                if (d != null && d.getDoctorId() == appointment.getDoctorId()) {
                    doctorCombo.setSelectedIndex(i);
                    break;
                }
            }

            dateSpinner.setValue(appointment.getAppointmentDate());
            timeSpinner.setValue(Time.valueOf(appointment.getAppointmentTime().toLocalTime()));
            statusCombo.setSelectedItem(appointment.getStatus());
            notesArea.setText(appointment.getNotes());

            addButton.setEnabled(false);
            editButton.setEnabled(true);
            deleteButton.setEnabled(true);
        }
    }

    private void addAppointment() {
        String validationError = validateForm();
        if (validationError != null) {
            JOptionPane.showMessageDialog(this, validationError, "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Patient selectedPatient = (Patient) patientCombo.getSelectedItem();
        Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();

        Appointment appointment = new Appointment();
        appointment.setPatientId(selectedPatient.getPatientId());
        appointment.setDoctorId(selectedDoctor.getDoctorId());
        appointment.setAppointmentDate(new Date(((java.util.Date) dateSpinner.getValue()).getTime()));
        appointment.setAppointmentTime(new Time(((java.util.Date) timeSpinner.getValue()).getTime()));
        appointment.setStatus((String) statusCombo.getSelectedItem());
        appointment.setNotes(notesArea.getText().trim());

        if (appointmentDAO.insertAppointment(appointment)) {
            JOptionPane.showMessageDialog(this, "Appointment booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAppointments();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to book appointment. Doctor or patient may already have an appointment at this time.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);

        String validationError = validateForm();
        if (validationError != null) {
            JOptionPane.showMessageDialog(this, validationError, "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Patient selectedPatient = (Patient) patientCombo.getSelectedItem();
        Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(appointmentId);
        appointment.setPatientId(selectedPatient.getPatientId());
        appointment.setDoctorId(selectedDoctor.getDoctorId());
        appointment.setAppointmentDate(new Date(((java.util.Date) dateSpinner.getValue()).getTime()));
        appointment.setAppointmentTime(new Time(((java.util.Date) timeSpinner.getValue()).getTime()));
        appointment.setStatus((String) statusCombo.getSelectedItem());
        appointment.setNotes(notesArea.getText().trim());

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to update this appointment?", 
            "Confirm Update", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (appointmentDAO.updateAppointment(appointment)) {
                JOptionPane.showMessageDialog(this, "Appointment updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAppointments();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update appointment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to cancel.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel this appointment?", 
            "Confirm Cancellation", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (appointmentDAO.deleteAppointment(appointmentId)) {
                JOptionPane.showMessageDialog(this, "Appointment cancelled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAppointments();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel appointment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String validateForm() {
        Patient selectedPatient = (Patient) patientCombo.getSelectedItem();
        Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();
        Date date = new Date(((java.util.Date) dateSpinner.getValue()).getTime());
        Time time = new Time(((java.util.Date) timeSpinner.getValue()).getTime());

        int patientId = selectedPatient != null ? selectedPatient.getPatientId() : 0;
        int doctorId = selectedDoctor != null ? selectedDoctor.getDoctorId() : 0;

        return ValidationUtils.validateAppointment(patientId, doctorId, date, time);
    }

    private void clearForm() {
        if (patientCombo.getItemCount() > 0) patientCombo.setSelectedIndex(0);
        if (doctorCombo.getItemCount() > 0) doctorCombo.setSelectedIndex(0);
        dateSpinner.setValue(Date.valueOf(LocalDate.now()));
        timeSpinner.setValue(Time.valueOf(LocalTime.now()));
        statusCombo.setSelectedIndex(0);
        notesArea.setText("");

        addButton.setEnabled(true);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        appointmentTable.clearSelection();
    }
}

