package highScores;

import homepage.Homepage;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HighScores extends JFrame {
    private JTextArea scoreArea;
    private static final String SCORE_FILE = "highScores/highscore.txt";
    private Font titleFont, textFont;
    private final String userName;

    public HighScores(String userName) {
        this.userName = userName;
        setTitle("RETROES - High Scores");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        try {
            titleFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/04b03.ttf")).deriveFont(50f);
            textFont = titleFont.deriveFont(24f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(titleFont);
        } catch (FontFormatException | IOException e) {
            System.out.println("Error loading font.");
        }

        JLabel titleLabel = new JLabel("HIGH SCORES", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

        scoreArea = new JTextArea();
        scoreArea.setEditable(false);
        scoreArea.setFont(textFont);
        scoreArea.setForeground(Color.WHITE);
        scoreArea.setBackground(Color.BLACK);
        scoreArea.setMargin(new Insets(10, 20, 10, 20));

        JScrollPane scrollPane = new JScrollPane(scoreArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        scrollPane.setBackground(Color.BLACK);

        loadScores();

        getContentPane().setBackground(Color.BLACK);

        JButton backButton = new JButton("Homepage");
        backButton.setFont(textFont != null ? textFont.deriveFont(20f) : new Font("Arial", Font.PLAIN, 20));
        backButton.setFocusable(false);
        backButton.addActionListener(e -> {
            dispose();
            new Homepage(userName);
        });

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.setBackground(Color.BLACK);
        southPanel.add(backButton);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadScores() {
        List<String> scores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                scores.add(line);
            }
        } catch (IOException e) {
            scores.add("No high scores recorded yet.");
        }

        scoreArea.setText(String.join("\n", scores));
    }
}
