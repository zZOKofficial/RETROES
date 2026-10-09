package login;

import app.AppVersion;
import createAccount.CreateAccountFrame;
import homepage.Homepage;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private static final String USER_DATA_FILE = "data/users.txt";
    private static final int SALT_LENGTH = 16;
    private static final int HASH_ITERATIONS = 120000;
    private static final int HASH_KEY_LENGTH = 256;
    private static Map<String, String> userDatabase = new HashMap<>();
    private static Map<String, String> userSalts = new HashMap<>();
    private Image backgroundImage;
    private Font titleFont, textFont;

    public LoginFrame() {
        setTitle("RETROES v" + AppVersion.VERSION + " - Login");
        setSize(1280, 720);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null); // Allows custom component positioning

        try {
            backgroundImage = Toolkit.getDefaultToolkit().getImage("homepage/assets/HomeBackground.png");
        } catch (Exception e) {
            System.out.println("Error loading background image.");
            e.printStackTrace();
        }

        try {
            titleFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/joystixmonospace.otf")).deriveFont(60f);
            textFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/cinzeld.ttf")).deriveFont(24f);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(titleFont);
            ge.registerFont(textFont);
        } catch (FontFormatException | IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading fonts! Default font will be used.", "Font Error", JOptionPane.WARNING_MESSAGE);
            textFont = new Font("Arial", Font.PLAIN, 18); // Fallback font
        }

        File file = new File(USER_DATA_FILE);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating user database file.");
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    userDatabase.put(parts[0].trim(), parts[2].trim());
                    userSalts.put(parts[0].trim(), parts[1].trim());
                } else if (parts.length == 2) {
                    userDatabase.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading user database.");
        }


        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panel.setLayout(null);
        panel.setBounds(0, 0, getWidth(), getHeight());
        panel.setOpaque(false);


        JLabel titleLabel = new JLabel("RETROES", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(300, 200, 680, 80);


        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(textFont);
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(340, 470, 120, 30);


        usernameField = new JTextField();
        usernameField.setFont(textFont);
        usernameField.setForeground(Color.BLACK);
        usernameField.setBounds(340, 500, 300, 40);


        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(textFont);
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(700, 470, 120, 30);


        passwordField = new JPasswordField();
        passwordField.setFont(textFont);
        passwordField.setForeground(Color.BLACK);
        passwordField.setBounds(700, 500, 300, 40);


        JButton loginButton = new JButton("Log in");
        loginButton.setFont(textFont);
        loginButton.setBounds(520, 570, 250, 45);


        JLabel createAccountLink = new JLabel("<html><u>Create New Account</u></html>");
        createAccountLink.setFont(textFont.deriveFont(Font.BOLD, 16f));
        createAccountLink.setForeground(Color.WHITE);
        createAccountLink.setBounds(580, 620, 250, 30);
        createAccountLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Both fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (userDatabase.containsKey(username) && verifyPassword(username, password)) {
                migrateLegacyPassword(username, password);
                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose();
                new Homepage(username);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        createAccountLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new CreateAccountFrame();
            }
        });


        panel.add(titleLabel);
        panel.add(userLabel);
        panel.add(usernameField);
        panel.add(passLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(createAccountLink);


        add(panel);
        setVisible(true);
    }

    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String saltBase64) {
        try {
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, HASH_ITERATIONS, HASH_KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return Base64.getEncoder().encodeToString(factory.generateSecret(spec).getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    private boolean verifyPassword(String username, String password) {
        String stored = userDatabase.get(username);
        String salt = userSalts.get(username);
        if (salt == null) {
            return stored.equals(password);
        }
        return hashPassword(password, salt).equals(stored);
    }

    private void migrateLegacyPassword(String username, String password) {
        if (userSalts.containsKey(username)) {
            return;
        }
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].trim().equals(username)) {
                    content.append(username).append(",").append(salt).append(",").append(hash).append("\n");
                } else {
                    content.append(line).append("\n");
                }
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DATA_FILE))) {
                writer.write(content.toString());
            }
            userDatabase.put(username, hash);
            userSalts.put(username, salt);
        } catch (IOException e) {
            System.out.println("Error migrating password for " + username);
        }
    }
}
