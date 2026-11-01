
package za.ac.cput.gesondlinks.views;
import za.ac.cput.gesondlinks.controllers.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 *
 * @author Fezeka
 */
public class DashboardFrame extends JFrame {
  
    private JTabbedPane tabbedPane;
    private PatientManagementPanel patientPanel;
    private DoctorManagementPanel doctorPanel;
    private AppointmentPanel appointmentPanel;
    private MedicalRecordPanel medicalRecordPanel;

    public DashboardFrame() {
        initComponents();
        setTitle("GesondLinks Healthcare Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(236, 240, 241));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("🏥 GesondLinks Healthcare System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Initialize panels
        patientPanel = new PatientManagementPanel();
        doctorPanel = new DoctorManagementPanel();
        appointmentPanel = new AppointmentPanel();
        medicalRecordPanel = new MedicalRecordPanel();

        // Add tabs
        tabbedPane.addTab("👤 Patients", patientPanel);
        tabbedPane.addTab("👨‍⚕️ Doctors", doctorPanel);
        tabbedPane.addTab("📅 Appointments", appointmentPanel);
        tabbedPane.addTab("📋 Medical Records", medicalRecordPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(236, 240, 241));
        statusPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        JLabel statusLabel = new JLabel("Status: Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusPanel.add(statusLabel);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        // Add logout action
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame();
            }
        });

        add(mainPanel);
    }

    // Method to refresh all panels
    public void refreshAll() {
        patientPanel.loadPatients();
        doctorPanel.loadDoctors();
        appointmentPanel.loadAppointments();
        medicalRecordPanel.loadMedicalRecords();
    }
}  
    