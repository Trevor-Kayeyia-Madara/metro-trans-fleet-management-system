package metrotransfleetmanagementsystem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddDriver extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vehicleandrouteassignmentdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";
    
    private JTextField driverNameField;
    private JTextField phoneNumberField;
    private JTextField emailAddressField;
    private JTextField licenseNumberField;

    public AddDriver() {
        setTitle("Add Driver");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 2));

        JLabel driverNameLabel = new JLabel("Driver Name:");
        driverNameField = new JTextField(100);

        JLabel phoneNumberLabel = new JLabel("Phone Number:");
        phoneNumberField = new JTextField(20);

        JLabel emailAddressLabel = new JLabel("Email Address:");
        emailAddressField = new JTextField(100);

        JLabel licenseNumberLabel = new JLabel("License Number:");
        licenseNumberField = new JTextField(20);

        JButton addButton = new JButton("Add Driver");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addDriver();
            }
        });

        panel.add(driverNameLabel);
        panel.add(driverNameField);
        panel.add(phoneNumberLabel);
        panel.add(phoneNumberField);
        panel.add(emailAddressLabel);
        panel.add(emailAddressField);
        panel.add(licenseNumberLabel);
        panel.add(licenseNumberField);
        panel.add(addButton);

        add(panel);
    }

    private void addDriver() {
        String driverName = driverNameField.getText();
        String phoneNumber = phoneNumberField.getText();
        String emailAddress = emailAddressField.getText();
        String licenseNumber = licenseNumberField.getText();

        // Create a database connection and insert the driver information
        try {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String insertQuery = "INSERT INTO drivers (DriverName, PhoneNumber, EmailAddress, LicenseNumber) VALUES (?, ?, ?, ?)";
                
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, driverName);
                    preparedStatement.setString(2, phoneNumber);
                    preparedStatement.setString(3, emailAddress);
                    preparedStatement.setString(4, licenseNumber);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Driver added successfully!");
                        dispose(); // Close the AddDriver window after successful insertion
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add driver. Please try again.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddDriver addDriver = new AddDriver();
            addDriver.setVisible(true);
        });
    }
}
