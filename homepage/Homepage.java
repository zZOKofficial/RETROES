package homepage;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import credits.Credits;
import games.flapocalypse.Flapocalypse;
import games.whatTheSnake.WhatTheSnake;
import games.streakTacToe.StreakTacToe;
import highScores.HighScores;
import login.LoginFrame;

public class Homepage extends JFrame {
    private String userName;
    private Image backgroundImage;
    private Font titleFont, textFont;

    public Homepage(String userName) {
        this.userName = userName;
        setTitle("RETROES - Home");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);

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
            textFont = new Font("Arial", Font.PLAIN, 18);
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

        // Title
        JLabel titleLabel = new JLabel("RETROES", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(300, 200, 680, 80);

        JLabel welcomeLabel = new JLabel("Hello, " + userName, SwingConstants.CENTER);
        welcomeLabel.setFont(textFont);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(300, 300, 680, 40);


        JButton flapocalypseButton = gameButton("Flapocalypse", 240, 460);
        JButton streakTacToeButton = gameButton("StreakTacToe", 540, 460);
        JButton whatTheSnakeButton = gameButton("WhatTheSnake", 840, 460);
        JButton highScoresButton = gameButton("High Scores", 240, 530);
        JButton creditsButton = gameButton("Credits", 540, 530);
        JButton logoutButton = gameButton("Logout", 840, 530);


        flapocalypseButton.addActionListener(e -> {
            dispose();
            new Flapocalypse();
        });
        streakTacToeButton.addActionListener(e -> {
            dispose();
            new StreakTacToe();
        });
        whatTheSnakeButton.addActionListener(e -> {
            dispose();
            new WhatTheSnake();
        });
        highScoresButton.addActionListener(e -> {
            dispose();
            new HighScores();
        });
        creditsButton.addActionListener(e -> {
            dispose();
            new Credits();
        });
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        backgroundPanel.add(titleLabel);
        backgroundPanel.add(welcomeLabel);
        backgroundPanel.add(flapocalypseButton);
        backgroundPanel.add(whatTheSnakeButton);
        backgroundPanel.add(streakTacToeButton);
        backgroundPanel.add(highScoresButton);
        backgroundPanel.add(creditsButton);
        backgroundPanel.add(logoutButton);

        setContentPane(backgroundPanel);
        setVisible(true);
    }

    private JButton gameButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 200, 50);
        button.setFont(textFont);
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(255, 204, 51)); // Default color
        button.setFocusPainted(false);


        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 153, 0)); // Darker orange on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 204, 51)); // Back to original color
            }
        });

        return button;
    }

}
