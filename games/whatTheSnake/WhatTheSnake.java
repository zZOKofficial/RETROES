package games.whatTheSnake;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.LinkedList;
import java.util.Random;

public class WhatTheSnake extends JPanel implements KeyListener, Runnable, ActionListener {
    private static final int BOARD_WIDTH = 1280;
    private static final int BOARD_HEIGHT = 720;
    private static final int UNIT_SIZE = 40;
    private static final int DELAY = 200;
    private static final int FOOD_SIZE = UNIT_SIZE / 2;
    private static final String GAME_NAME = "WhatTheSnake"; // Game name to identify the high score

    private final LinkedList<Point> snake;
    private Point food;
    private Point bonusFood;
    private char direction = 'R';
    private boolean isRunning = false;
    private boolean isGameOver = false;
    private boolean isGameStarted = false;
    private int score = 0;
    private int highScore = 0;
    private int bonusFoodTimer = 0;
    private int bonusFoodCooldown = 0;
    private Font pixeloidFont;
    private Image backgroundImage;

    private Image snakeHeadUp;
    private Image snakeHeadDown;
    private Image snakeHeadLeft;
    private Image snakeHeadRight;
    private Image snakeBodyImage;

    private JButton startButton;

    public WhatTheSnake() {
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        Border border = BorderFactory.createLineBorder(Color.BLACK, 10);
        setBorder(border);

        snake = new LinkedList<>();
        snake.add(new Point(100, 100));
        createFood();
        bonusFood = null;

        try {
            pixeloidFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/cinzeld.ttf")).deriveFont(20f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pixeloidFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            pixeloidFont = new Font("Arial", Font.BOLD, 20);
        }

        // Load background image
        try {
            backgroundImage = new ImageIcon("games/whatTheSnake/assets/grass.png").getImage();
        } catch (Exception e) {
            e.printStackTrace();
            backgroundImage = null;
        }

        // Load snake images
        try {
            snakeHeadUp = new ImageIcon("games/whatTheSnake/assets/upmouth.png").getImage().getScaledInstance(UNIT_SIZE, UNIT_SIZE, Image.SCALE_SMOOTH);
            snakeHeadDown = new ImageIcon("games/whatTheSnake/assets/downmouth.png").getImage().getScaledInstance(UNIT_SIZE, UNIT_SIZE, Image.SCALE_SMOOTH);
            snakeHeadLeft = new ImageIcon("games/whatTheSnake/assets/leftmouth.png").getImage().getScaledInstance(UNIT_SIZE, UNIT_SIZE, Image.SCALE_SMOOTH);
            snakeHeadRight = new ImageIcon("games/whatTheSnake/assets/rightmouth.png").getImage().getScaledInstance(UNIT_SIZE, UNIT_SIZE, Image.SCALE_SMOOTH);
            snakeBodyImage = new ImageIcon("games/whatTheSnake/assets/snakeimage.png").getImage().getScaledInstance(UNIT_SIZE, UNIT_SIZE, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            e.printStackTrace();
            snakeHeadUp = null;
            snakeHeadDown = null;
            snakeHeadLeft = null;
            snakeHeadRight = null;
            snakeBodyImage = null;
        }

        loadHighScore();

        startButton = new JButton("Start Game");
        startButton.setFont(pixeloidFont.deriveFont(20f));
        startButton.setBackground(new Color(50, 150, 50)); // Darker green background
        startButton.setForeground(Color.WHITE); // White text
        startButton.setFocusable(false);
        startButton.addActionListener(this);
        startButton.setBounds((BOARD_WIDTH - 150) / 2, (BOARD_HEIGHT - 50) / 2, 150, 50);
        add(startButton);
    }

    private void createFood() {
        Random random = new Random();
        int x = random.nextInt(BOARD_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        int y = random.nextInt(BOARD_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        food = new Point(x, y);

        while (snake.contains(food)) {
            x = random.nextInt(BOARD_WIDTH / UNIT_SIZE) * UNIT_SIZE;
            y = random.nextInt(BOARD_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
            food = new Point(x, y);
        }
    }

    private void createBonusFood() {
        Random random = new Random();
        int x = random.nextInt(BOARD_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        int y = random.nextInt(BOARD_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        bonusFood = new Point(x, y);

        while (snake.contains(bonusFood)) {
            x = random.nextInt(BOARD_WIDTH / UNIT_SIZE) * UNIT_SIZE;
            y = random.nextInt(BOARD_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
            bonusFood = new Point(x, y);
        }

        bonusFoodTimer = 50;
    }

    private void move() {
        Point head = snake.getFirst();
        Point newHead = (Point) head.clone();

        if (direction == 'U') newHead.y -= UNIT_SIZE;
        else if (direction == 'D') newHead.y += UNIT_SIZE;
        else if (direction == 'L') newHead.x -= UNIT_SIZE;
        else if (direction == 'R') newHead.x += UNIT_SIZE;

        boolean isOutOfBounds = newHead.x < 0 || newHead.x >= BOARD_WIDTH || newHead.y < 0 || newHead.y >= BOARD_HEIGHT;
        boolean isBodyCollision = false;

        for (int i = 1; i < snake.size(); i++) {
            if (newHead.equals(snake.get(i))) {
                isBodyCollision = true;
                break;
            }
        }

        if (isOutOfBounds || isBodyCollision) {
            isRunning = false;
            isGameOver = true;
            updateHighScore();
            return;
        }

        snake.addFirst(newHead);

        if (newHead.equals(food)) {
            score++;
            createFood();
        } else if (bonusFood != null && newHead.x >= bonusFood.x && newHead.x < bonusFood.x + UNIT_SIZE * 2 &&
                newHead.y >= bonusFood.y && newHead.y < bonusFood.y + UNIT_SIZE * 2) {
            score += 4;
            bonusFood = null;
            for (int i = 0; i < 4; i++) {
                snake.addLast(new Point(-1, -1));
            }
        } else {
            snake.removeLast();
        }

        if (bonusFood == null && bonusFoodCooldown <= 0 && new Random().nextInt(200) < 2) {
            createBonusFood();
            bonusFoodCooldown = 100;
        }

        if (bonusFood != null) {
            bonusFoodTimer--;
            if (bonusFoodTimer <= 0) {
                bonusFood = null;
            }
        }

        if (bonusFoodCooldown > 0) {
            bonusFoodCooldown--;
        }
    }

    private void updateHighScore() {
        if (score > highScore) {
            highScore = score;
            saveHighScore();
        }
    }

    private void saveHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader("highScores/highscore.txt"))) {
            StringBuilder content = new StringBuilder();
            String line;
            boolean updated = false;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(GAME_NAME)) {
                    content.append(GAME_NAME).append(": ").append(highScore).append("\n");
                    updated = true;
                } else {
                    content.append(line).append("\n");
                }
            }
            if (!updated) {
                content.append(GAME_NAME).append(": ").append(highScore).append("\n");
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("highScores/highscore.txt"))) {
                writer.write(content.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadHighScore() {
        File file = new File("highScores/highscore.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
                highScore = 0;
                saveHighScore();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(GAME_NAME)) {
                        String[] parts = line.split(": ");
                        if (parts.length == 2) {
                            highScore = Integer.parseInt(parts[1]);
                        }
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (isGameOver && e.getKeyCode() == KeyEvent.VK_ENTER) {
            resetGame();
        }

        if (e.getKeyCode() == KeyEvent.VK_UP && direction != 'D') {
            direction = 'U';
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && direction != 'U') {
            direction = 'D';
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && direction != 'R') {
            direction = 'L';
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && direction != 'L') {
            direction = 'R';
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    private void resetGame() {
        snake.clear();
        int initialX = (BOARD_WIDTH / 2 / UNIT_SIZE) * UNIT_SIZE;
        int initialY = (BOARD_HEIGHT / 2 / UNIT_SIZE) * UNIT_SIZE;
        snake.add(new Point(initialX, initialY));
        direction = 'R';
        score = 0;
        isGameOver = false;
        isRunning = true;
        createFood();
        bonusFood = null;
        new Thread(this).start();
        requestFocusInWindow();
    }

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            move();
            repaint();
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, this);
        }

        if (isGameOver) {
            String gameOverMessage = "Game Over! Press Enter to Restart";
            String scoreMessage = "Score: " + score;
            String highScoreMessage = "High Score: " + highScore;

            g.setColor(Color.RED);
            g.setFont(pixeloidFont.deriveFont(30f));
            g.drawString(gameOverMessage, (BOARD_WIDTH - g.getFontMetrics().stringWidth(gameOverMessage)) / 2, BOARD_HEIGHT / 2);
            g.drawString(scoreMessage, (BOARD_WIDTH - g.getFontMetrics().stringWidth(scoreMessage)) / 2, BOARD_HEIGHT / 2 + 40);
            g.drawString(highScoreMessage, (BOARD_WIDTH - g.getFontMetrics().stringWidth(highScoreMessage)) / 2, BOARD_HEIGHT / 2 + 80);
        } else {
            for (Point p : snake) {
                g.drawImage(snakeBodyImage, p.x, p.y, this);
            }

            Point head = snake.getFirst();
            Image headImage = null;
            if (direction == 'U') {
                headImage = snakeHeadUp;
            } else if (direction == 'D') {
                headImage = snakeHeadDown;
            } else if (direction == 'L') {
                headImage = snakeHeadLeft;
            } else if (direction == 'R') {
                headImage = snakeHeadRight;
            }

            if (headImage != null) {
                g.drawImage(headImage, head.x, head.y, this);
            }

            g.setColor(Color.RED);
            g.fillRect(food.x, food.y, FOOD_SIZE, FOOD_SIZE);

            if (bonusFood != null) {
                g.setColor(new Color(255, 215, 0)); // Gold color for bonus food
                g.fillRect(bonusFood.x, bonusFood.y, UNIT_SIZE * 2, UNIT_SIZE * 2);
            }

            g.setColor(Color.WHITE);
            g.setFont(pixeloidFont.deriveFont(20f));
            g.drawString("Score: " + score, 20, 40);
            g.drawString("High Score: " + highScore, 20, 80);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isRunning) {
            resetGame();
        }
    }

    public static void main(String[] args) {

        JFrame gameFrame = new JFrame("What The Snake");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        WhatTheSnake gamePanel = new WhatTheSnake();
        gameFrame.add(gamePanel);

        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
    }
}
