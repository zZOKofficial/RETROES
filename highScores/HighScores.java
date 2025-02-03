package highScores;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HighScores extends JFrame {
    private JTextArea scoreArea;
    private static final String SCORE_FILE = "highScores/highscore.txt";
    private Font titleFont, textFont;

    public HighScores() {
        setTitle("RETROES - High Scores");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        try {
            titleFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/04B03.ttf")).deriveFont(50f);
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

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

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

    public static void saveScore(String playerName, int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE, true))) {
            writer.write(playerName + " - " + score);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new HighScores();
    }
}
