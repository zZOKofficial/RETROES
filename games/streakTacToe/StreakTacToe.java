package games.streakTacToe;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;

public class StreakTacToe implements ActionListener {
    private Random random = new Random();
    public JFrame frame = new JFrame();
    private JPanel title_panel = new JPanel();
    private JPanel button_panel = new JPanel();
    private JPanel control_panel = new JPanel();
    private JLabel textfield = new JLabel();
    private JLabel scoreLabel = new JLabel("X: 0 | O: 0");
    private JButton restartButton = new JButton("Restart");
    private Font pixelFont;
    private boolean player1_turn;
    private JButton[] button;
    private int gridSize = 3;
    private int xScore = 0, oScore = 0;
    private int roundCount = 0;

    public StreakTacToe() {
        initializeGame();
        firstTurn();
    }

    public void initializeGame() {
        loadPixelFont();

        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(50, 50, 50));
        frame.setLayout(new BorderLayout());

        textfield.setBackground(new Color(25, 25, 25));
        textfield.setForeground(new Color(25, 255, 0));
        textfield.setFont(pixelFont.deriveFont(30f));
        textfield.setHorizontalAlignment(JLabel.CENTER);
        textfield.setText("Streak-Tac-Toe");
        textfield.setOpaque(true);

        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBackground(Color.BLACK);
        scoreLabel.setOpaque(true);
        scoreLabel.setFont(pixelFont.deriveFont(20f));

        title_panel.setLayout(new BorderLayout());
        title_panel.add(scoreLabel, BorderLayout.WEST);
        title_panel.add(textfield, BorderLayout.CENTER);

        button_panel.setLayout(new GridLayout(gridSize, gridSize));
        setupButtons();

        restartButton.setFont(pixelFont.deriveFont(20f));
        restartButton.setFocusable(false);
        restartButton.addActionListener(e -> resetGame());

        control_panel.setLayout(new FlowLayout());
        control_panel.add(restartButton);

        frame.add(title_panel, BorderLayout.NORTH);
        frame.add(button_panel, BorderLayout.CENTER);
        frame.add(control_panel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void setupButtons() {
        button_panel.removeAll();
        button_panel.setLayout(new GridLayout(gridSize, gridSize));
        button = new JButton[gridSize * gridSize];
        for (int i = 0; i < button.length; i++) {
            button[i] = new JButton();
            button_panel.add(button[i]);
            button[i].setFont(pixelFont.deriveFont(40f));
            button[i].setFocusable(false);
            button[i].addActionListener(this);
        }
        button_panel.revalidate();
        button_panel.repaint();
    }

    private void loadPixelFont() {
        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/04b03.ttf")).deriveFont(36f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pixelFont);
        } catch (Exception e) {
            pixelFont = new Font("SansSerif", Font.BOLD, 36);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < button.length; i++) {
            if (e.getSource() == button[i] && button[i].getText().equals("")) {
                button[i].setText(player1_turn ? "X" : "O");
                button[i].setForeground(player1_turn ? Color.RED : Color.BLUE);
                player1_turn = !player1_turn;
                textfield.setText(player1_turn ? "X turn" : "O turn");
                check();
                break;
            }
        }
    }

    public void firstTurn() {
        player1_turn = random.nextInt(2) == 0;
        textfield.setText(player1_turn ? "X turn" : "O turn");
    }

    public void check() {
        if (checkWin("X")) {
            xScore++;
            roundCount++;
            nextLevel();
        } else if (checkWin("O")) {
            oScore++;
            roundCount++;
            nextLevel();
        } else if (Arrays.stream(button).allMatch(b -> !b.getText().equals(""))) {
            textfield.setText("It's a Tie!");
            roundCount++;
            nextLevel();
        }
    }

    private boolean checkWin(String player) {
        int size = gridSize;
        int[][] winConditions = new int[size * 2 + 2][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                winConditions[i][j] = i * size + j;
                winConditions[size + i][j] = j * size + i;
            }
        }
        for (int i = 0; i < size; i++) {
            winConditions[size * 2][i] = i * (size + 1);
            winConditions[size * 2 + 1][i] = (i + 1) * (size - 1);
        }

        for (int[] condition : winConditions) {
            if (Arrays.stream(condition).allMatch(idx -> button[idx].getText().equals(player))) {
                return true;
            }
        }
        return false;
    }

    private void nextLevel() {
        updateScoreboard();
        if (roundCount == 3) {
            declareWinner();
        } else {
            if (roundCount == 1) {
                gridSize = 4;
            } else if (roundCount == 2) {
                gridSize = 5;
            }
            setupButtons();
            firstTurn();
        }
    }

    private void updateScoreboard() {
        scoreLabel.setText("X: " + xScore + " | O: " + oScore);
    }

    private void declareWinner() {
        String winner = xScore > oScore ? "X Wins the Series!" : "O Wins the Series!";
        JOptionPane.showMessageDialog(frame, winner, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        resetGame();
    }

    public void resetGame() {
        xScore = 0;
        oScore = 0;
        roundCount = 0;
        gridSize = 3;
        setupButtons();
        updateScoreboard();
        firstTurn();
    }

    public static void main(String[] args) {
        StreakTacToe game = new StreakTacToe();
        game.frame.setSize(1280, 720);
        game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.frame.setLocationRelativeTo(null);
        game.frame.setVisible(true);
    }
}
