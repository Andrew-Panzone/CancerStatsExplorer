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
import javax.swing.table.DefaultTableModel;

public class CancerStatisticApp {
  private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/cancerstatisticdb"; // Update this line
  private static String USERNAME;
  private static String PASSWORD;
  // Common styling elements
  private static final Font LABEL_FONT = new Font("Futura", Font.BOLD, 14);
  private static final Color BACKGROUND_COLOR = new Color(245, 221, 203, 255); // Light lavender
  private static final Font LARGE_LABEL_FONT = new Font("Futura", Font.BOLD, 18);
  private static final Font LARGE_TEXT_FIELD_FONT = new Font("Futura", Font.PLAIN, 18);
  private static final Font LARGE_BUTTON_FONT = new Font("Futura", Font.BOLD, 18);
  private static final Font MEDIUM_BUTTON_FONT = new Font("Futura", Font.BOLD, 12);
  private static final Dimension LARGE_BUTTON_DIMENSION = new Dimension(120, 40);
  private static final Dimension LONG_BUTTON_DIMENSION = new Dimension(300, 40);
  private static final Dimension LARGE_COMBOBOX_DIMENSION = new Dimension(200, 40);


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
    JFrame frame = new JFrame("Cancer Statistic Application");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1400, 600);
    frame.getContentPane().setBackground(BACKGROUND_COLOR); // A light lavender background

    // Custom font
    Font labelFont = LABEL_FONT;

    // Layout
    frame.setLayout(new FlowLayout());

    // Username label and field
    JLabel usernameLabel = new JLabel("Username:");
    usernameLabel.setFont(labelFont);
    JTextField usernameField = new JTextField(20);
    usernameField.setFont(LARGE_TEXT_FIELD_FONT);
    usernameField.setPreferredSize(LARGE_COMBOBOX_DIMENSION);
    frame.add(usernameLabel);
    frame.add(usernameField);

    // Password label and field
    JLabel passwordLabel = new JLabel("Password:");
    passwordLabel.setFont(labelFont);
    JPasswordField passwordField = new JPasswordField(20);
    passwordField.setFont(LARGE_TEXT_FIELD_FONT);
    passwordField.setPreferredSize(LARGE_COMBOBOX_DIMENSION);
    frame.add(passwordLabel);
    frame.add(passwordField);

    // Login and register buttons
    JButton loginButton = new JButton("Login");
    loginButton.setFont(LARGE_BUTTON_FONT);
    loginButton.setPreferredSize(LARGE_BUTTON_DIMENSION);
    JButton registerButton = new JButton("Register");
    registerButton.setFont(LARGE_BUTTON_FONT);
    registerButton.setPreferredSize(LARGE_BUTTON_DIMENSION);
    frame.add(loginButton);
    frame.add(registerButton);

    // Role selection
    String[] roles = {"Viewer", "Professional", "Administrator"};
    JComboBox<String> roleComboBox = new JComboBox<>(roles);
    roleComboBox.setFont(LARGE_TEXT_FIELD_FONT);
    roleComboBox.setPreferredSize(LARGE_COMBOBOX_DIMENSION);
    JLabel roleLabel = new JLabel("Role:");
    roleLabel.setFont(labelFont);
    frame.add(roleLabel);
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
          String userRole = new CancerStatisticApp().getUserRole(username);
          frame.getContentPane().removeAll(); // Clear the existing components
          frame.getContentPane().add(createQueryPanel(frame, userRole)); // Add the query panel or other default content
          frame.revalidate(); // Revalidate the frame
          frame.repaint();    // Repaint the frame
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
        } else if ("Professional".equals(role)) {
          String professionalPin = JOptionPane.showInputDialog(frame, "Enter Professional Pin:");
          if (professionalPin != null && professionalPin.equals("12345")) {
            new CancerStatisticApp().registerUser(username, password, role);
          } else {
            JOptionPane.showMessageDialog(frame, "Incorrect Professional Pin", "Error", JOptionPane.ERROR_MESSAGE);
          }
        } else {
          new CancerStatisticApp().registerUser(username, password, role);
        }
      }
    });

    // Show the frame
    frame.setVisible(true);
  }

  public String getUserRole(String username) {
    String role = null;
    String sql = "SELECT role FROM users WHERE username = ?";

    try (Connection conn = this.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, username);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          role = rs.getString("role");
        }
      }
    } catch (SQLException e) {
      System.out.println("Error fetching user role: " + e.getMessage());
      e.printStackTrace();
    }

    return role;
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

  public static JPanel createQueryPanel(JFrame frame, String userRole) {
    JPanel queryPanel = new JPanel(new GridBagLayout());
    queryPanel.setBackground(BACKGROUND_COLOR);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(2, 2, 2, 2);

    JComboBox<String> stateComboBox = new JComboBox<>();
    stateComboBox.setFont(LARGE_TEXT_FIELD_FONT);
    stateComboBox.setPreferredSize(LARGE_COMBOBOX_DIMENSION);
    stateComboBox.addItem("All"); // Adds "All" as the first item
    // Fetches and add states from the database
    String[] states = new CancerStatisticApp().fetchStates();
    for (String state : states) {
      stateComboBox.addItem(state);
    }

    JComboBox<String> cancerTypeComboBox = new JComboBox<>(new CancerStatisticApp().fetchCancerTypes());
    cancerTypeComboBox.setFont(LARGE_TEXT_FIELD_FONT);
    cancerTypeComboBox.setPreferredSize(LARGE_COMBOBOX_DIMENSION);

    JComboBox<String> sexComboBox = new JComboBox<>(new CancerStatisticApp().fetchSexes());
    sexComboBox.setFont(LARGE_TEXT_FIELD_FONT);
    sexComboBox.setPreferredSize(LARGE_COMBOBOX_DIMENSION);

    JComboBox<String> raceComboBox = new JComboBox<>(new CancerStatisticApp().fetchRaces());
    raceComboBox.setFont(LARGE_TEXT_FIELD_FONT);
    raceComboBox.setPreferredSize(LARGE_COMBOBOX_DIMENSION);

    JRadioButton incidenceRateButton = new JRadioButton("Incidence Rate", true);
    JRadioButton deathRateButton = new JRadioButton("Death Rate");
    ButtonGroup tableSelectionGroup = new ButtonGroup();
    tableSelectionGroup.add(incidenceRateButton);
    tableSelectionGroup.add(deathRateButton);

    // Add components to the panel with GridBagConstraints
    JLabel stateLabel = new JLabel("State/Territory:");
    stateLabel.setFont(LABEL_FONT);
    gbc.gridx = 0;
    gbc.gridy = 0;
    queryPanel.add(stateLabel, gbc);
    gbc.gridx = 1;
    queryPanel.add(stateComboBox, gbc);

    JLabel cancerTypeLabel = new JLabel("Cancer Type:");
    cancerTypeLabel.setFont(LABEL_FONT);
    gbc.gridx = 0;
    gbc.gridy = 1;
    queryPanel.add(cancerTypeLabel, gbc);
    gbc.gridx = 1;
    queryPanel.add(cancerTypeComboBox, gbc);

    JLabel sexLabel = new JLabel("Sex:");
    sexLabel.setFont(LABEL_FONT);
    gbc.gridx = 0;
    gbc.gridy = 2;
    queryPanel.add(sexLabel, gbc);
    gbc.gridx = 1;
    queryPanel.add(sexComboBox, gbc);

    JLabel raceLabel = new JLabel("Race:");
    raceLabel.setFont(LABEL_FONT);
    gbc.gridx = 0;
    gbc.gridy = 3;
    queryPanel.add(raceLabel, gbc);
    gbc.gridx = 1;
    queryPanel.add(raceComboBox, gbc);

    gbc.gridx = 0;
    gbc.gridy = 4;
    queryPanel.add(incidenceRateButton, gbc);
    gbc.gridx = 1;
    queryPanel.add(deathRateButton, gbc);

    JButton queryButton = new JButton("Query");
    queryButton.setFont(LARGE_BUTTON_FONT);
    queryButton.setPreferredSize(LARGE_BUTTON_DIMENSION);
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridwidth = 2; // Span across two columns for the query button
    queryPanel.add(queryButton, gbc);

    if ("Administrator".equals(userRole)) {
      JButton openActivityLogButton = new JButton("Open Activity Log Query Panel");
      openActivityLogButton.setFont(MEDIUM_BUTTON_FONT);
      openActivityLogButton.setPreferredSize(LONG_BUTTON_DIMENSION);
      openActivityLogButton.addActionListener(e -> {
        frame.setContentPane(createActivityLogQueryPanel(frame, userRole));
        frame.revalidate();
        frame.repaint();
      });
      JButton openQueryWindowButton = new JButton("Open Professional & Report Query Panel");
      openQueryWindowButton.setFont(MEDIUM_BUTTON_FONT);
      openQueryWindowButton.setPreferredSize(LONG_BUTTON_DIMENSION);
      openQueryWindowButton.addActionListener(e -> {
        frame.setContentPane(createProfessionalReportQueryPanel(frame, userRole));
        frame.revalidate();
        frame.repaint();
      });
      queryPanel.add(openActivityLogButton);
      queryPanel.add(openQueryWindowButton);
    } else if ("Professional".equals(userRole)) {
      JButton openQueryWindowButton = new JButton("Open Professional & Report Query Panel");
      openQueryWindowButton.setFont(MEDIUM_BUTTON_FONT);
      openQueryWindowButton.setPreferredSize(LONG_BUTTON_DIMENSION);
      openQueryWindowButton.addActionListener(e -> {
        frame.setContentPane(createProfessionalReportQueryPanel(frame, userRole));
        frame.revalidate();
        frame.repaint();
      });
      queryPanel.add(openQueryWindowButton);
    }

    queryButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String selectedState = (String) stateComboBox.getSelectedItem();
        String selectedCancerType = (String) cancerTypeComboBox.getSelectedItem();
        String selectedSex = (String) sexComboBox.getSelectedItem();
        String selectedRace = (String) raceComboBox.getSelectedItem();
        boolean isIncidenceRate = incidenceRateButton.isSelected();

        String tableName = isIncidenceRate ? "incidencerate" : "deathrate";
        String[] columnNames = isIncidenceRate ?
            new String[]{"State", "Cancer Type", "Sex", "Race", "Incidence Rate", "Case Count", "Population"} :
            new String[]{"State", "Cancer Type", "Sex", "Race", "Death Rate", "Death Count", "Population"};

        // Construct SQL query
        String stateCondition = "All".equals(selectedState) ? "1" : "state = ?";
        String sql = "SELECT state, cancertype, sex, race, " +
            (isIncidenceRate ? "i_rate, case_count, population " : "d_rate, death_count, population ") +
            "FROM " + tableName + " " +
            "WHERE " + stateCondition + " AND cancertype = ? AND sex = ? AND race = ?";

        try (Connection conn = new CancerStatisticApp().connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

          // Set parameters based on state selection
          if (!"All".equals(selectedState)) {
            pstmt.setString(1, selectedState);
            pstmt.setString(2, selectedCancerType);
            pstmt.setString(3, selectedSex);
            pstmt.setString(4, selectedRace);
          } else {
            pstmt.setString(1, selectedCancerType);
            pstmt.setString(2, selectedSex);
            pstmt.setString(3, selectedRace);
          }

          try (ResultSet rs = pstmt.executeQuery()) {
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            while (rs.next()) {
              Object[] row = new Object[columnNames.length];
              for (int i = 0; i < row.length; i++) {
                row[i] = rs.getObject(i + 1); // ResultSet is 1-indexed
              }
              model.addRow(row);
            }

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            table.setFillsViewportHeight(true);

            // Display the table in a new frame or a dialog
            JDialog dialog = new JDialog();
            dialog.setTitle("Query Results");
            dialog.add(scrollPane);
            dialog.setSize(800, 400);
            dialog.setVisible(true);
          }
        } catch (SQLException ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(queryPanel, "Error executing query: " + ex.getMessage(), "Query Error", JOptionPane.ERROR_MESSAGE);
        }
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

  public static JPanel createProfessionalReportQueryPanel(JFrame frame, String userRole) {
    JPanel queryPanel = new JPanel();
    queryPanel.setLayout(new FlowLayout());
    queryPanel.setBackground(BACKGROUND_COLOR);

    JComboBox<String> cancerTypeComboBox = new JComboBox<>(new CancerStatisticApp().fetchCancerTypes());
    cancerTypeComboBox.setFont(LARGE_TEXT_FIELD_FONT);
    cancerTypeComboBox.setPreferredSize(LARGE_COMBOBOX_DIMENSION);

    JLabel cancerTypeLabel = new JLabel("Cancer Type:");
    cancerTypeLabel.setFont(LARGE_LABEL_FONT);
    queryPanel.add(cancerTypeLabel);
    queryPanel.add(cancerTypeComboBox);

    JButton queryButton = new JButton("Query");
    queryButton.setFont(LARGE_BUTTON_FONT);
    queryButton.setPreferredSize(LARGE_BUTTON_DIMENSION);
    queryPanel.add(queryButton);

    // Back button
    JButton backButton = new JButton("Back");
    backButton.setFont(LARGE_BUTTON_FONT);
    backButton.setPreferredSize(LARGE_BUTTON_DIMENSION);
    backButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        frame.setContentPane(createQueryPanel(frame, userRole)); // Switch back to the main query panel
        frame.revalidate();
        frame.repaint();
      }
    });
    queryPanel.add(backButton);

    queryButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String cancerType = (String) cancerTypeComboBox.getSelectedItem();
        String[] columnNames = {"Professional Name", "Specialization", "Cancer Type", "Patient Name"};

        // Construct SQL query
        String sql = "SELECT professional.name, professional.specialization, report.cancer_type, patient.name " +
            "FROM report " +
            "JOIN professional ON report.professional = professional.id " +
            "JOIN patient ON report.patient = patient.id " +
            "WHERE report.cancer_type = ?";

        try (Connection conn = new CancerStatisticApp().connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

          pstmt.setString(1, cancerType);

          try (ResultSet rs = pstmt.executeQuery()) {
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            while (rs.next()) {
              String profName = rs.getString(1); // the first column is professional.name
              String spec = rs.getString(2); // the second column is professional.specialization
              String cancer = rs.getString(3); // the third column is report.cancer_type
              String patientName = rs.getString(4); // the fourth column is patient.name

              model.addRow(new Object[]{profName, spec, cancer, patientName});
            }

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            table.setFillsViewportHeight(true);

            // Display the table in a new frame or a dialog
            JDialog dialog = new JDialog();
            dialog.setTitle("Query Results");
            dialog.add(scrollPane);
            dialog.setSize(800, 400);
            dialog.setVisible(true);
          }
        } catch (SQLException ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(queryPanel, "Error executing query: " + ex.getMessage(), "Query Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    return queryPanel;
  }

  public String[] fetchProfessionalNames() {
    String sql = "SELECT name FROM professional";
    return fetchData(sql);
  }

  public String[] fetchSpecializations() {
    String sql = "SELECT DISTINCT specialization FROM professional";
    return fetchData(sql);
  }

  public static JPanel createActivityLogQueryPanel(JFrame frame, String userRole) {
    JPanel queryPanel = new JPanel();
    queryPanel.setLayout(new FlowLayout());
    queryPanel.setBackground(BACKGROUND_COLOR);

    JComboBox<String> usernameComboBox = new JComboBox<>();
    usernameComboBox.addItem("All Users");
    String[] usernames = new CancerStatisticApp().fetchUsernames();
    for (String username : usernames) {
      usernameComboBox.addItem(username);
    }
    usernameComboBox.setFont(LARGE_TEXT_FIELD_FONT);
    usernameComboBox.setPreferredSize(LARGE_COMBOBOX_DIMENSION);

    JButton queryButton = new JButton("Query");
    queryButton.setFont(LARGE_BUTTON_FONT);
    queryButton.setPreferredSize(LARGE_BUTTON_DIMENSION);
    queryPanel.add(usernameComboBox);
    queryPanel.add(queryButton);

    JButton backButton = new JButton("Back");
    backButton.setFont(LARGE_BUTTON_FONT);
    backButton.setPreferredSize(LARGE_BUTTON_DIMENSION);
    backButton.addActionListener(e -> {
      frame.setContentPane(createQueryPanel(frame, userRole));
      frame.revalidate();
      frame.repaint();
    });
    queryPanel.add(backButton);

    // Add ActionListener for queryButton here to execute and display query results
    queryButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String selectedUser = (String) usernameComboBox.getSelectedItem();
        String[] columnNames = {"Log ID", "Username", "Action Type", "Action Description", "Timestamp"};

        // Construct SQL query
        String sql = selectedUser.equals("All Users")
            ? "SELECT * FROM activitylog"
            : "SELECT * FROM activitylog WHERE username = ?";

        try (Connection conn = new CancerStatisticApp().connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

          if (!selectedUser.equals("All Users")) {
            pstmt.setString(1, selectedUser);
          }

          try (ResultSet rs = pstmt.executeQuery()) {
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            while (rs.next()) {
              Object[] row = new Object[columnNames.length];
              for (int i = 0; i < row.length; i++) {
                row[i] = rs.getObject(i + 1); // ResultSet is 1-indexed
              }
              model.addRow(row);
            }

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            table.setFillsViewportHeight(true);

            // Display the table in a new frame or a dialog
            JDialog dialog = new JDialog();
            dialog.setTitle("Activity Log Results");
            dialog.add(scrollPane);
            dialog.setSize(800, 400);
            dialog.setVisible(true);
          }
        } catch (SQLException ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(queryPanel, "Error executing query: " + ex.getMessage(), "Query Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    return queryPanel;
  }

  public String[] fetchUsernames() {
    String sql = "SELECT username FROM users";
    return fetchData(sql);
  }
}