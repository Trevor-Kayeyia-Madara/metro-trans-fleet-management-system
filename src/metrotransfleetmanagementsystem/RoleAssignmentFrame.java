package metrotransfleetmanagementsystem;

import javax.swing.JComboBox;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class RoleAssignmentFrame extends javax.swing.JFrame {

    private final String DB_URL = "jdbc:mysql://localhost:3306/userconfigurationdb";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "password";

    public RoleAssignmentFrame() {
        initComponents();
        initUserComboBox();
    }

    private void initComponents() {
        userComboBox = new javax.swing.JComboBox<>();
        roleComboBox = new javax.swing.JComboBox<>();
        assignButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Role Assignment");

        // Role ComboBox
        roleComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Administrator", "Dispatcher", "Driver", "Maintenance", "Management" }));

        assignButton.setText("Assign Role");
        assignButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            assignButtonActionPerformed(evt);
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(assignButton)
                        .addGap(30, 30, 30))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(userComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(roleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(210, Short.MAX_VALUE)))
        ));
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(userComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(roleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(assignButton)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }

     private void assignButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String selectedUser = (String) userComboBox.getSelectedItem();
        String selectedRole = (String) roleComboBox.getSelectedItem();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String getUserIDQuery = "SELECT UserID FROM users WHERE UserName = ?";
            try (PreparedStatement getUserIdStmt = conn.prepareStatement(getUserIDQuery)) {
                getUserIdStmt.setString(1, selectedUser);
                int userId = -1;
                try (java.sql.ResultSet userIdResult = getUserIdStmt.executeQuery()) {
                    if (userIdResult.next()) {
                        userId = userIdResult.getInt("UserID");
                    }
                }

                if (userId != -1) {
                    String assignRoleQuery = "INSERT INTO userroles (UserID, UserRole) VALUES (?, ?)";
                    try (PreparedStatement assignRoleStmt = conn.prepareStatement(assignRoleQuery)) {
                        assignRoleStmt.setInt(1, userId);
                        assignRoleStmt.setString(2, selectedRole);
                        int rowsInserted = assignRoleStmt.executeUpdate();
                        if (rowsInserted > 0) {
                            System.out.println("Role '" + selectedRole + "' assigned to user: " + selectedUser);
                            showSuccessMessage("Role Assigned", "Role '" + selectedRole + "' assigned to user: " + selectedUser);
                        } else {
                            System.out.println("Failed to assign the role.");
                            showErrorMessage("Assignment Failed", "Failed to assign the role.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error", "An error occurred while assigning the role.");
        }
    }

    private void showSuccessMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }


    private void initUserComboBox() {
        List<String> userList = getUsersFromUserRolesTable();
        userComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(userList.toArray(String[]::new)));
    }

    private List<String> getUsersFromUserRolesTable() {
        List<String> userList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT DISTINCT UserName FROM users INNER JOIN userroles ON users.UserID = userroles.UserID";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        userList.add(rs.getString("UserName"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new RoleAssignmentFrame().setVisible(true);
        });
    }

    private javax.swing.JComboBox<String> userComboBox;
    private javax.swing.JComboBox<String> roleComboBox;
    private javax.swing.JButton assignButton;
}
