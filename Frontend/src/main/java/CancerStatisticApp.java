import java.sql.*;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CancerStatisticApp {
  private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/cancerstatisticdb"; // Update this line
  private static String USERNAME;
  private static String PASSWORD;

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    // Prompt for MySQL credentials
    System.out.print("Enter MySQL Username: ");
    USERNAME = scanner.nextLine();
    System.out.print("Enter MySQL Password: ");
    PASSWORD = scanner.nextLine();

    // Establish a connection to test
    try (Connection conn = new CancerStatisticApp().connect()) {
      if (conn != null) {
        SwingUtilities.invokeLater(() -> {
          createAndShowGUI();
        });
      } else {
        System.out.println("Failed to establish a database connection.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void createAndShowGUI() {
    // Create the main frame
    JFrame frame = new JFrame("Cancer Statistic Application");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(300, 200);

    // Create UI elements
    JTextField usernameField = new JTextField(20);
    JPasswordField passwordField = new JPasswordField(20);
    JButton loginButton = new JButton("Login");
    JButton registerButton = new JButton("Register");

    // Layout
    frame.setLayout(new FlowLayout());
    frame.add(new JLabel("Username:"));
    frame.add(usernameField);
    frame.add(new JLabel("Password:"));
    frame.add(passwordField);
    frame.add(loginButton);
    frame.add(registerButton);

    // Role selection
    String[] roles = {"Viewer", "Analyst", "Administrator"};
    JComboBox<String> roleComboBox = new JComboBox<>(roles);
    frame.add(new JLabel("Role:"));
    frame.add(roleComboBox);

    // Add action listeners
    loginButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        boolean isAuthenticated = new CancerStatisticApp().loginUser(username, password);
        if (isAuthenticated) {
          JOptionPane.showMessageDialog(frame, "Login successful!");
        } else {
          JOptionPane.showMessageDialog(frame, "Login failed.");
        }
      }
    });

    registerButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();
        new CancerStatisticApp().registerUser(username, password, role);
      }
    });

    // Show the frame
    frame.setVisible(true);
  }

  private Connection connect() {
    Connection conn = null;
    try {
      conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
      System.out.println("Connected to the database successfully.");
    } catch (SQLException e) {
      System.out.println("Error connecting to the database: " + e.getMessage());
      e.printStackTrace();
    }
    return conn;
  }

  public void registerUser(String username, String password, String role) {
    String hashedPassword = hashPassword(password);

    String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";

    try (Connection conn = this.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, username);
      pstmt.setString(2, hashedPassword);
      pstmt.setString(3, role);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    JOptionPane.showMessageDialog(null, "User registered successfully");
  }

  public boolean loginUser(String username, String password) {
    String sql = "SELECT password_hash FROM users WHERE username = ?";

    try (Connection conn = this.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        String storedHash = rs.getString("password_hash");
        return hashPassword(password).equals(storedHash);
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return false;
  }

  public static String hashPassword(String passwordToHash) {
    String generatedPassword = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] bytes = md.digest(passwordToHash.getBytes());
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < bytes.length; i++) {
        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
      }
      generatedPassword = sb.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return generatedPassword;
  }
}