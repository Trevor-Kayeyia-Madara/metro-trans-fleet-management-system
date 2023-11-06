package metrotransfleetmanagementsystem;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;

public class AssignVehiclesToRoute extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vehicleandrouteassignmentdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    private JComboBox<String> vehicleComboBox;
    private JComboBox<String> routeComboBox;
    private JButton assignButton;

    public AssignVehiclesToRoute() {
        setTitle("Assign Vehicles to Routes");
        setLayout(new GridLayout(4, 2));

        JLabel vehicleLabel = new JLabel("Select Vehicle:");
        vehicleComboBox = new JComboBox<>(getVehiclesList());

        JLabel routeLabel = new JLabel("Select Route:");
        routeComboBox = new JComboBox<>(getRoutesList());

        assignButton = new JButton("Assign Vehicle to Route");
        assignButton.addActionListener(e -> assignVehicleToRoute());

        add(vehicleLabel);
        add(vehicleComboBox);
        add(routeLabel);
        add(routeComboBox);
        add(new JLabel()); // Empty label for spacing
        add(assignButton);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
    }

    private void assignVehicleToRoute() {
        String selectedVehicle = (String) vehicleComboBox.getSelectedItem();
        String selectedRoute = (String) routeComboBox.getSelectedItem();

        // Implement the logic to insert the assignment into the database
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Get the IDs of the selected vehicle and route from the database
            int vehicleID = getVehicleID(selectedVehicle, conn);
            int routeID = getRouteID(selectedRoute, conn);

            if (vehicleID > 0 && routeID > 0) {
                // Insert the assignment into the database
                String insertQuery = "INSERT INTO DriverAssignments (VehicleID, VehicleAssignedRoute) VALUES (?, ?)";
                try (PreparedStatement preparedStatement = conn.prepareStatement(insertQuery)) {
                    preparedStatement.setInt(1, vehicleID);
                    preparedStatement.setInt(2, routeID);
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Assignment successful!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to assign the vehicle to the route.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selected vehicle or route not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error assigning the vehicle to the route: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getVehicleID(String selectedVehicle, Connection conn) {
    int vehicleID = -1;
    try {
        // Create a PreparedStatement to retrieve the vehicle ID
        String query = "SELECT VehicleID FROM vehicles WHERE PlateNumber = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, selectedVehicle);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            vehicleID = resultSet.getInt("VehicleID");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return vehicleID;
}


    private int getRouteID(String selectedRoute, Connection conn) {
    int routeID = -1;
    try {
        // Create a PreparedStatement to retrieve the route ID
        String query = "SELECT RouteID FROM routes WHERE RouteName = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, selectedRoute);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            routeID = resultSet.getInt("RouteID");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return routeID;
}
    private String[] getVehiclesList() {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    ArrayList<String> vehicleList = new ArrayList<>();

    try {
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        String query = "SELECT PlateNumber FROM vehicles";
        preparedStatement = conn.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String plateNumber = resultSet.getString("PlateNumber");
            vehicleList.add(plateNumber);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    return vehicleList.toArray(new String[0]);
}

private String[] getRoutesList() {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    ArrayList<String> routeList = new ArrayList<>();

    try {
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        String query = "SELECT RouteName FROM routes";
        preparedStatement = conn.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String routeName = resultSet.getString("RouteName");
            routeList.add(routeName);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    return routeList.toArray(new String[0]);
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AssignVehiclesToRoute assignmentForm = new AssignVehiclesToRoute();
            assignmentForm.setVisible(true);
        });
    }
}
