import java.sql.*;
import java.util.ArrayList;
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
          frame.setContentPane(createQueryPanel());
          frame.validate();
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

        if ("Administrator".equals(role)) {
          String adminPassword = JOptionPane.showInputDialog(frame, "Enter Admin Password:");
          if (adminPassword != null && adminPassword.equals("cancerdbadmin")) {
            new CancerStatisticApp().registerUser(username, password, role);
          } else {
            JOptionPane.showMessageDialog(frame, "Incorrect Admin Password", "Error", JOptionPane.ERROR_MESSAGE);
          }
        } else {
          new CancerStatisticApp().registerUser(username, password, role);
        }
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

  public static JPanel createQueryPanel() {
    JPanel queryPanel = new JPanel();
    JComboBox<String> stateComboBox = new JComboBox<>(new CancerStatisticApp().fetchStates()); // Populate with actual data
    JComboBox<String> cancerTypeComboBox = new JComboBox<>(new CancerStatisticApp().fetchCancerTypes());
    JComboBox<String> sexComboBox = new JComboBox<>(new CancerStatisticApp().fetchSexes());
    JComboBox<String> raceComboBox = new JComboBox<>(new CancerStatisticApp().fetchRaces());
    JButton queryButton = new JButton("Query");

    queryPanel.setLayout(new FlowLayout());
    queryPanel.add(new JLabel("State:"));
    queryPanel.add(stateComboBox);
    queryPanel.add(new JLabel("Cancer Type:"));
    queryPanel.add(cancerTypeComboBox);
    queryPanel.add(new JLabel("Sex:"));
    queryPanel.add(sexComboBox);
    queryPanel.add(new JLabel("Race:"));
    queryPanel.add(raceComboBox);
    queryPanel.add(queryButton);

    queryButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // Perform the query based on selected items
        // String state = (String) stateComboBox.getSelectedItem();
        // Add logic to perform the query and display results
      }
    });

    return queryPanel;
  }

  public String[] fetchCancerTypes() {
    String sql = "SELECT name FROM cancertype"; // SQL query to fetch cancer types
    return fetchData(sql);
  }

  public String[] fetchStates() {
    String sql = "SELECT sname FROM state";
    return fetchData(sql);
  }

  public String[] fetchSexes() {
    String sql = "SELECT DISTINCT sex FROM demographicgroup";
    return fetchData(sql);
  }

  public String[] fetchRaces() {
    String sql = "SELECT DISTINCT race FROM demographicgroup";
    return fetchData(sql);
  }

  private String[] fetchData(String sql) {
    ArrayList<String> data = new ArrayList<>();
    try (Connection conn = this.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery()) {

      while (rs.next()) {
        data.add(rs.getString(1)); // assuming the data is in the first column
      }
    } catch (SQLException e) {
      System.out.println("Error fetching data: " + e.getMessage());
    }
    return data.toArray(new String[0]);
  }
}