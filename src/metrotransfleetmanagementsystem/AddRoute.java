package metrotransfleetmanagementsystem;

import com.mysql.cj.xdevapi.Statement;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddRoute extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vehicleandrouteassignmentdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";
    
    private JTextField routeNameField;
    private JTextField routeStatusField;
    private JTextField peakChargesField;
    private JTextField offPeakChargesField;

    public AddRoute() {
        setTitle("Add Route");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 2));

        JLabel routeNameLabel = new JLabel("Route Name:");
        routeNameField = new JTextField(100);

        JLabel routeStatusLabel = new JLabel("Route Status:");
        routeStatusField = new JTextField(20);

        JLabel peakChargesLabel = new JLabel("Peak Charges:");
        peakChargesField = new JTextField(10);

        JLabel offPeakChargesLabel = new JLabel("Off Peak Charges:");
        offPeakChargesField = new JTextField(10);

        JButton addButton = new JButton("Add Route");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addRoute();
            }
        });

        panel.add(routeNameLabel);
        panel.add(routeNameField);
        panel.add(routeStatusLabel);
        panel.add(routeStatusField);
        panel.add(peakChargesLabel);
        panel.add(peakChargesField);
        panel.add(offPeakChargesLabel);
        panel.add(offPeakChargesField);
        panel.add(addButton);

        add(panel);
    }

    private void addRoute() {
        String routeName = routeNameField.getText();
        String routeStatus = routeStatusField.getText();
        int peakCharges = Integer.parseInt(peakChargesField.getText());
        int offPeakCharges = Integer.parseInt(offPeakChargesField.getText());

        // Create a database connection and insert the route information
        try {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String routeID = getNextRouteID(connection.createStatement());
                String insertQuery = "INSERT INTO routes (RouteID, RouteName, RouteStatus, PeakCharges, OffPeakCharges) VALUES (?, ?, ?, ?, ?)";
                
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, routeID);
                    preparedStatement.setString(2, routeName);
                    preparedStatement.setString(3, routeStatus);
                    preparedStatement.setInt(4, peakCharges);
                    preparedStatement.setInt(5, offPeakCharges);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Route added successfully!");
                        dispose(); // Close the AddRoute window after successful insertion
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add route. Please try again.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private String getNextRouteID(java.sql.Statement stmt) throws SQLException {
        java.sql.ResultSet resultSet = stmt.executeQuery("SELECT MAX(CAST(RouteID AS SIGNED)) AS maxRouteID FROM routes");
        if (resultSet.next()) {
            int maxRouteID = resultSet.getInt("maxRouteID");
            return String.format("%03d", maxRouteID + 1);
        }
        return "001"; // If there are no records yet, start from "001"
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddRoute addRoute = new AddRoute();
            addRoute.setVisible(true);
        });
    }
}
