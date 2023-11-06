package metrotransfleetmanagementsystem;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import net.sourceforge.jdatepicker.JDateComponentFactory;
import net.sourceforge.jdatepicker.JDatePicker;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;

public class AddVehicle extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vehicleandrouteassignmentdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";
    
    private JTextField plateNumberField;
    private JTextField vehicleTypeField;
    private JTextField vehicleStatusField;
    private JComboBox<String> driverComboBox;
    private JTextField insuranceNumberField;
    private JDatePicker insuranceExpiryDatePicker;
    private JButton addVehicle;

    public AddVehicle() {
        setTitle("Add Vehicle");
        setLayout(new GridLayout(7, 2));
        
        JLabel plateNumberLabel = new JLabel("Plate Number:");
        plateNumberField = new JTextField(100);
        
        JLabel vehicleTypeLabel = new JLabel("Vehicle Type:");
        vehicleTypeField = new JTextField(50);
        
        JLabel vehicleStatusLabel = new JLabel("Vehicle Status:");
        vehicleStatusField = new JTextField(20);
        
        driverComboBox = new JComboBox<>(getDriversList());
        driverComboBox.setSelectedIndex(0); // Set the default selected driver

        JLabel insuranceNumberLabel = new JLabel("Insurance Number:");
        insuranceNumberField = new JTextField(100);

        JLabel insuranceExpiryDateLabel = new JLabel("Insurance Expiry Date:");
        UtilDateModel dateModel = new UtilDateModel();
        JDatePanelImpl datePanel = new JDatePanelImpl(dateModel);
        insuranceExpiryDatePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        addVehicle = new JButton("Add Vehicle");
        addVehicle.addActionListener(e -> addVehicle());

        driverComboBox.addActionListener((ActionEvent e) -> {
            // No need to set the driverAssignedField here since we directly use the selected driver
        });

        add(plateNumberLabel);
        add(plateNumberField);
        add(vehicleTypeLabel);
        add(vehicleTypeField);
        add(vehicleStatusLabel);
        add(vehicleStatusField);
        add(driverComboBox);
        add(insuranceNumberLabel);
        add(insuranceNumberField);
        add(insuranceExpiryDateLabel);
        add((JComponent) insuranceExpiryDatePicker);
        add(addVehicle);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
    }

    private void addVehicle() {
        String plateNumber = plateNumberField.getText();
        String vehicleType = vehicleTypeField.getText();
        String vehicleStatus = vehicleStatusField.getText();
        String driverAssigned = (String) driverComboBox.getSelectedItem(); // Get the selected driver from the ComboBox
        String insuranceNumber = insuranceNumberField.getText();
        java.util.Date selectedDate = (java.util.Date) insuranceExpiryDatePicker.getModel().getValue();

        // Convert the selected date to java.sql.Date
        java.sql.Date insuranceExpiryDate = new java.sql.Date(selectedDate.getTime());

        try {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                Statement stmt = conn.createStatement();

                // Generate Fleet Number (MT100 increment)
                String nextFleetNumber = getNextFleetNumber(stmt);
                String fleetNumber = "MT" + nextFleetNumber;

                // Insert the new vehicle into the database
                String insertQuery = "INSERT INTO vehicles (PlateNumber, VehicleType, VehicleStatus, FleetNumber, DriverAssigned, InsuranceNumber, InsuranceExpiryDate) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
                preparedStatement.setString(1, plateNumber);
                preparedStatement.setString(2, vehicleType);
                preparedStatement.setString(3, vehicleStatus);
                preparedStatement.setString(4, fleetNumber);
                preparedStatement.setString(5, driverAssigned);
                preparedStatement.setString(6, insuranceNumber);
                preparedStatement.setDate(7, insuranceExpiryDate);

                preparedStatement.executeUpdate();

                JOptionPane.showMessageDialog(this, "Vehicle added successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding vehicle: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getNextFleetNumber(Statement stmt) throws SQLException {
        ResultSet resultSet = stmt.executeQuery("SELECT MAX(CAST(SUBSTRING(FleetNumber, 6) AS SIGNED)) AS maxFleetNumber FROM vehicles");
        if (resultSet.next()) {
            int maxFleetNumber = resultSet.getInt("maxFleetNumber");
            return "MT" + String.format("%03d", maxFleetNumber + 1);
        }
        return "MT001"; // If there are no records yet, start from MT001
    }

    private String[] getDriversList() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet resultSet = null;
        ArrayList<String> driversList = new ArrayList<>();

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            stmt = conn.createStatement();
            String query = "SELECT DriverName FROM Drivers";
            resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                String driverName = resultSet.getString("DriverName");
                driversList.add(driverName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching driver list: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return driversList.toArray(new String[0]);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddVehicle addVehicle = new AddVehicle();
            addVehicle.setVisible(true);
        });
    }

    public class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final String datePattern = "yyyy-MM-dd";
        private final SimpleDateFormat dateFormatter;

        public DateLabelFormatter() {
            dateFormatter = new SimpleDateFormat(datePattern);
        }

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }
}
