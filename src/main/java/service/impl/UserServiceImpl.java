package service.impl;

import service.UserService;
import util.DBConnection;

import java.sql.*;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;
import javax.swing.*;

public class UserServiceImpl implements UserService {

    @Override
    public int login(Scanner scanner) {
        try (Connection conn = DBConnection.getConnection()) {
            String user = JOptionPane.showInputDialog(null, "Enter username:");
            if (user == null || user.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username cannot be empty!");
                return -1;
            }

            String password = readPassword("Enter password:");

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT user_id, full_name, password FROM users WHERE username=?");
            ps.setString(1, user);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password");

                if (BCrypt.checkpw(password, storedHash)) {
                    JOptionPane.showMessageDialog(null, "Welcome " + rs.getString("full_name") + "!");
                    return rs.getInt("user_id");
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid password!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "User not found!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void register(Scanner scanner) {
        try (Connection conn = DBConnection.getConnection()) {

            String username = promptUsernameUnique(conn);
            if (username == null) return;


            String password = "";
            boolean passwordValid = false;
            String name = JOptionPane.showInputDialog(null, "Enter full name:");
            String email = JOptionPane.showInputDialog(null, "Enter email:");

            while (!passwordValid) {
                // The prompt informs the user of the requirements
                password = readPassword("Enter password (Min 8 chars, incl. special character) or CANCEL to exit:");

                if (password.isEmpty() && !didUserCancel()) {
                    // User clicked OK without entering anything (empty string)
                    JOptionPane.showMessageDialog(null, "Password cannot be empty!");
                    continue; // Reprompt
                } else if (password.isEmpty() && didUserCancel()) {
                    // User clicked CANCEL, or closed the dialog
                    JOptionPane.showMessageDialog(null, "Registration cancelled.");
                    return; // Exit the register method
                }

                if (isPasswordValid(password)) {
                    passwordValid = true; // Exit the loop
                } else {
                    JOptionPane.showMessageDialog(null, "Password is too weak. It must be at least 8 characters long and include at least one special character. Try again.");
                    // Loop continues
                }
            }
            // ✅ Hash before saving
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users(username, password, full_name, email) VALUES (?,?,?,?)");
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ps.setString(3, name);
            ps.setString(4, email);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Registration successful!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ✅ Utility to securely read password with masking (***)
     */
    private String readPassword(String prompt) {
        JPasswordField pf = new JPasswordField();
        int okCxl = JOptionPane.showConfirmDialog(
                null, pf, prompt, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (okCxl == JOptionPane.OK_OPTION) {
            return new String(pf.getPassword());
        } else {
            return "";
        }
    }

    /**
     * keep asking until a non-empty username that doesn't already exist (for register)
     */
    private String promptUsernameUnique(Connection conn) throws SQLException {
        while (true) {
            String u = JOptionPane.showInputDialog(null, "Enter username:", "Register", JOptionPane.QUESTION_MESSAGE);
            if (u == null) return null; // user cancelled
            u = u.trim();
            if (u.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                continue; // show input box again
            }
            if (usernameExists(conn, u)) {
                JOptionPane.showMessageDialog(null, "Username already exists. Try another.", "Taken",
                        JOptionPane.WARNING_MESSAGE);
                continue; // show input box again
            }
            return u;
        }
    }


    private String promptPasswordLoop(String prompt, String title) {
        while (true) {
            JPasswordField pf = new JPasswordField();
            int ok = JOptionPane.showConfirmDialog(null, pf, prompt,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (ok != JOptionPane.OK_OPTION) return null; // user cancelled
            String pw = new String(pf.getPassword());
            if (pw.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                continue; // ask again
            }
            return pw;
        }
    }

    private boolean isPasswordValid(String password) {
        // Condition 1: Must be at least 8 characters long
        if (password.length() < 8) {
            return false;
        }

        // Condition 2: Must include at least one special character.
        // The regex `.*[!@#$%^&*()-+=_].*` checks if the string contains any of the listed special characters.
        String specialCharRegex = ".*[!@#$%^&*()-+=_].*";
        if (!password.matches(specialCharRegex)) {
            return false;
        }

        return true;
    }
    private boolean usernameExists(Connection conn, String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE LOWER(username) = LOWER(?) LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
    private boolean userCancelledDialog = false;
    private boolean didUserCancel() {
        return userCancelledDialog;
    }
}

