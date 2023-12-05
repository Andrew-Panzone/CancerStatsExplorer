import java.sql.*;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;


public class CancerStatisticApp {
  private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/CancerStatisticDB"; // Update this line
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
        // User interaction logic
        System.out.println("Select an option: \n1. Register \n2. Login");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume the newline

        switch (choice) {
          case 1:
            // Register user
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            System.out.print("Enter role: ");
            String role = scanner.nextLine();
            new CancerStatisticApp().registerUser(username, password, role);
            break;
          case 2:
            // User login
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            System.out.print("Enter password: ");
            password = scanner.nextLine();
            boolean isAuthenticated = new CancerStatisticApp().loginUser(username, password);
            if (isAuthenticated) {
              System.out.println("Login successful!");
            } else {
              System.out.println("Login failed.");
            }
            break;
          default:
            System.out.println("Invalid option.");
        }
      } else {
        System.out.println("Failed to establish a database connection.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
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