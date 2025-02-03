package createAccount;

import login.LoginFrame;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class CreateAccountFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton, showPasswordButton;
    private static final String FILE_NAME = "data/users.txt";
    private boolean isPasswordVisible = false;
    private Image backgroundImage;
    private Font titleFont, textFont;

    public CreateAccountFrame() {
        setTitle("RETROES - Create Account");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null); // Custom layout


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
            JOptionPane.showMessageDialog(this, "Error loading fonts!", "Font Error", JOptionPane.WARNING_MESSAGE);
            textFont = new Font("Arial", Font.PLAIN, 18); // Fallback font
        }


        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setLayout(null);
        backgroundPanel.setBounds(0, 0, getWidth(), getHeight());


        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(280, 180, 700, 80); // Positioning the title label


        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(textFont);
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(240, 470, 120, 30);

        usernameField = new JTextField();
        usernameField.setFont(textFont);
        usernameField.setForeground(Color.BLACK);
        usernameField.setBounds(240, 500, 300, 40);


        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(textFont);
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(700, 470, 120, 30);

        passwordField = new JPasswordField();
        passwordField.setFont(textFont);
        passwordField.setForeground(Color.BLACK);
        passwordField.setBounds(700, 500, 300, 40);


        showPasswordButton = new JButton("Show");
        showPasswordButton.setFont(textFont);
        showPasswordButton.setBounds(1010, 500, 130, 40);
        showPasswordButton.addActionListener(e -> togglePasswordVisibility());


        registerButton = new JButton("Register");
        registerButton.setFont(textFont);
        registerButton.setBounds(520, 570, 250, 45);
        registerButton.addActionListener(e -> registerUser());


        JLabel createAccountLink = new JLabel("<html><u>Already have an account?</u></html>");
        createAccountLink.setFont(textFont.deriveFont(Font.BOLD, 16f));
        createAccountLink.setForeground(Color.WHITE);
        createAccountLink.setBounds(570, 620, 250, 30);
        createAccountLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        createAccountLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new LoginFrame();
            }
        });

        backgroundPanel.add(titleLabel);
        backgroundPanel.add(userLabel);
        backgroundPanel.add(usernameField);
        backgroundPanel.add(passLabel);
        backgroundPanel.add(passwordField);
        backgroundPanel.add(showPasswordButton);
        backgroundPanel.add(registerButton);
        backgroundPanel.add(createAccountLink);


        setContentPane(backgroundPanel);
        setVisible(true);
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordField.setEchoChar('*');
            showPasswordButton.setText("Show");
        } else {
            passwordField.setEchoChar((char) 0);
            showPasswordButton.setText("Hide");
        }
        isPasswordVisible = !isPasswordVisible;
    }

    private void registerUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Both fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    if (parts[0].equals(username)) {
                        JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading user database.", "Error", JOptionPane.ERROR_MESSAGE);
        }


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(username + "," + password);
            writer.newLine();
            JOptionPane.showMessageDialog(this, "Account Created Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginFrame();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving user data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
