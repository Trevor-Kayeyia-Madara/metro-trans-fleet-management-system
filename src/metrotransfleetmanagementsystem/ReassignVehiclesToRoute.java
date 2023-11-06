package metrotransfleetmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class ReassignVehiclesToRoute extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vehicleandrouteassignmentdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    private JComboBox<String> vehicleComboBox;
    private JComboBox<String> routeComboBox;
    private JButton reassignButton;

    public ReassignVehiclesToRoute() {
        setTitle("Reassign Vehicles to Route");
        setLayout(new GridLayout(4, 2));

        JLabel vehicleLabel = new JLabel("Select Vehicle:");
        vehicleComboBox = new JComboBox<>(getVehiclesList());

        JLabel routeLabel = new JLabel("Select Route:");
        routeComboBox = new JComboBox<>(getRoutesList());

        reassignButton = new JButton("Reassign");
        reassignButton.addActionListener(e -> reassignVehicle());

        add(vehicleLabel);
        add(vehicleComboBox);
        add(routeLabel);
        add(routeComboBox);
        add(reassignButton);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
    }

    private void reassignVehicle() {
        String selectedVehicle = (String) vehicleComboBox.getSelectedItem();
        String selectedRoute = (String) routeComboBox.getSelectedItem();

        int vehicleID = getVehicleID(selectedVehicle);
        int routeID = getRouteID(selectedRoute);

        if (vehicleID != -1 && routeID != -1) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String updateQuery = "UPDATE vehicles SET VehicleAssignedRoute = ? WHERE VehicleID = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(updateQuery);
                preparedStatement.setInt(1, routeID);
                preparedStatement.setInt(2, vehicleID);

                int rowsUpdated = preparedStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Vehicle reassigned successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to reassign vehicle. Please try again.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int getVehicleID(String selectedVehicle) {
        // Implement logic to get the vehicle ID based on the selected vehicle
        int vehicleID = -1;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
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

    private int getRouteID(String selectedRoute) {
        // Implement logic to get the route ID based on the selected route
        int routeID = -1;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
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
            ReassignVehiclesToRoute reassign = new ReassignVehiclesToRoute();
            reassign.setVisible(true);
        });
    }
}