package games.flapocalypse;

import homepage.Homepage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class Flapocalypse extends JFrame implements ActionListener, KeyListener {
    private final int boardWidth = 1280;
    private final int boardHeight = 720;

    private Image backgroundImg, birdImg, topPipeImg, bottomPipeImg;
    private int birdX = boardWidth / 8, birdY = boardHeight / 2;
    private int birdWidth = 34, birdHeight = 24;
    private Font customFont;

    class Bird {
        int x = birdX, y = birdY, width = birdWidth, height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    class Pipe {
        int x = boardWidth, y, width = 64, height = 512;
        Image img;
        boolean passed = false;

        Pipe(Image img, int y) {
            this.img = img;
            this.y = y;
        }
    }

    private Bird bird;
    private int velocityX = -4, velocityY = 0, gravity = 1;
    private final ArrayList<Pipe> pipes = new ArrayList<>();
    private Timer gameLoop, placePipeTimer;
    private boolean gameOver = false;
    private double score = 0;
    private double highScore = 0;
    private String gameState = "START";
    private GamePanel gamePanel;

    public Flapocalypse() {
        setTitle("Flapocalypse");
        setSize(boardWidth, boardHeight);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        gamePanel = new GamePanel();
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();
        gamePanel.addKeyListener(this);
        add(gamePanel);

        backgroundImg = new ImageIcon(getClass().getResource("/games/flapocalypse/assets/flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("/games/flapocalypse/assets/flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("/games/flapocalypse/assets/toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("/games/flapocalypse/assets/bottompipe.png")).getImage();

        bird = new Bird(birdImg);

        placePipeTimer = new Timer(1500, e -> placePipes());
        gameLoop = new Timer(1000 / 60, this);

        try {
            File fontFile = new File("fonts/cinzeld.ttf");
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(24f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            customFont = new Font("Arial", Font.BOLD, 28);
        }


        JButton backButton = new JButton("Homepage");
        backButton.setBounds(boardWidth - 140, 10, 130, 40);
        backButton.addActionListener(e -> goToHomepage());
        gamePanel.setLayout(null);


        backButton.setBackground(new Color(0, 70, 70));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(customFont.deriveFont(20f));
        backButton.setBorder(BorderFactory.createEmptyBorder());

        gamePanel.add(backButton);

        readHighScore();

        setVisible(true);
    }

    private void placePipes() {
        int randomPipeY = (int) (Math.random() * (boardHeight / 3)) + 100;
        pipes.add(new Pipe(topPipeImg, randomPipeY - 512));
        pipes.add(new Pipe(bottomPipeImg, randomPipeY + 150));
    }

    private class GamePanel extends JPanel {
        public GamePanel() {
            setPreferredSize(new Dimension(boardWidth, boardHeight));
            setFocusable(true);
            requestFocus();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
            g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

            for (Pipe pipe : pipes) {
                g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
            }

            g.setColor(Color.WHITE);
            g.setFont(customFont);
            g.drawString("Score: " + (int) score, 20, 40);
            g.drawString("High Score: " + (int) highScore, 20, 80);

            if (gameState.equals("START")) {
                showStartMessage(g);
            } else if (gameState.equals("GAME_OVER")) {
                showGameOverMessage(g);
            } else if (gameState.equals("PAUSED")) {
                g.setFont(customFont.deriveFont(48f));
                g.setColor(Color.RED);
                g.drawString("PAUSED", boardWidth / 2 - 100, boardHeight / 2);
            }
        }

        private void showStartMessage(Graphics g) {
            g.setFont(customFont.deriveFont(40f));  // Start screen text
            g.setColor(Color.blue);
            g.drawString("Press SPACE to Start", boardWidth / 2 - 180, boardHeight / 2);
            g.drawString("P to Pause and Resume", boardWidth / 2 - 180, boardHeight / 2 + 50);
        }

        private void showGameOverMessage(Graphics g) {
            g.setFont(customFont.deriveFont(72f));  // Game Over text
            g.setColor(Color.RED);
            g.drawString("GAME OVER", boardWidth / 2 - 170, boardHeight / 2 - 100);

            g.setFont(customFont.deriveFont(24f));  // Restart instruction
            g.setColor(Color.WHITE);
            g.drawString("Press SPACE to Restart", boardWidth / 2 - 140, boardHeight / 2 - 70);
        }
    }

    private void move() {
        if (gameState.equals("PLAYING")) {
            velocityY += gravity;
            bird.y += velocityY;
            bird.y = Math.max(bird.y, 0);

            for (Pipe pipe : pipes) {
                pipe.x += velocityX;
                if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                    score += 0.5;
                    pipe.passed = true;
                }
                if (collision(bird, pipe)) {
                    gameOver();
                }
            }
            if (bird.y > boardHeight) {
                gameOver();
            }
        }
    }

    private void gameOver() {
        if (score > highScore) {
            highScore = score;
            updateHighScore();
        }

        gameState = "GAME_OVER";
        gameLoop.stop();
        placePipeTimer.stop();
    }

    private boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width && a.x + a.width > b.x &&
                a.y < b.y + b.height && a.y + a.height > b.y;
    }

    private void readHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader("highScores/highscore.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Flapocalypse:")) {
                    highScore = Double.parseDouble(line.split(":")[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader("highScores/highscore.txt"))) {
            StringBuilder updatedContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Flapocalypse:")) {
                    updatedContent.append("Flapocalypse: ").append((int) highScore).append("\n");
                } else {
                    updatedContent.append(line).append("\n");
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("highScores/highscore.txt"))) {
                writer.write(updatedContent.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        gamePanel.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameState.equals("START")) {
                gameState = "PLAYING";
                placePipeTimer.start();
                gameLoop.start();
            } else if (gameState.equals("PLAYING")) {
                velocityY = -10;
            } else if (gameState.equals("GAME_OVER")) {
                restartGame();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_P) {
            if (gameState.equals("PLAYING")) {
                gameState = "PAUSED";
                gameLoop.stop();
                placePipeTimer.stop();
            } else if (gameState.equals("PAUSED")) {
                gameState = "PLAYING";
                gameLoop.start();
                placePipeTimer.start();
            }
        }
    }

    private void restartGame() {
        bird.y = birdY;
        velocityY = 0;
        pipes.clear();
        score = 0;
        gameState = "START";
        gamePanel.repaint();
    }

    private void goToHomepage() {
        dispose();
        new Homepage("Player");
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new Flapocalypse();
    }
}
