package credits;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Credits extends JFrame {
    private JPanel panel;
    private Timer timer;
    private int yOffset;
    private static final int SPEED = 2;
    private static final String FONT_PATH_04B03 = "fonts/04B03.ttf";
    private static final String FONT_PATH_JOYSTIX = "fonts/joystixmonospace.otf";

    public Credits() {
        setTitle("Credits");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
                drawCredits(g);
            }
        };
        panel.setPreferredSize(new Dimension(1280, 3500));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getViewport().setBackground(Color.BLACK);

        add(scrollPane);
        setLocationRelativeTo(null);
        setVisible(true);

        yOffset = getHeight();
        startScrolling();
    }

    private void drawCredits(Graphics g) {

        Font categoryFont = loadFont(FONT_PATH_04B03, 36);
        Font subcategoryFont = loadFont(FONT_PATH_04B03, 28);
        Font nameFont = loadFont(FONT_PATH_04B03, 24);
        Font retroesFont = loadFont(FONT_PATH_JOYSTIX, 72);

        g.setColor(Color.WHITE);  // White color for text

        List<String> credits = getCreditsText();
        int y = yOffset;


        for (String line : credits) {
            if (line.equals("CREDITS") || line.equals("Game Development") || line.equals("Quality Assurance") || line.equals("Music") || line.equals("Fonts Used") || line.equals("Thank You For Playing Our Game!") ||line.equals("Special Thanks")) {
                g.setFont(categoryFont);
            } else if (line.contains(":") || line.contains("by")) {
                g.setFont(subcategoryFont);
            } else if (line.equals("RETROES")) {
                g.setFont(retroesFont);
            } else {
                g.setFont(nameFont);
            }

            g.drawString(line, getWidth() / 3, y);
            y += 50;
        }
    }

    private void startScrolling() {
        timer = new Timer(50, e -> {
            yOffset -= SPEED;
            panel.repaint();
            if (yOffset + (50 * getCreditsText().size()) < 0) {
                // Reset the scroll once it's finished
                yOffset = getHeight();
            }
        });
        timer.start();
    }

    private Font loadFont(String path, float size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(path)).deriveFont(size);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            return font;
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("SansSerif", Font.PLAIN, (int) size);  // Fallback font
        }
    }

    private List<String> getCreditsText() {
        List<String> lines = new ArrayList<>();
        lines.add("RETROES");
        lines.add("CREDITS");
        lines.add("");
        lines.add("");
        lines.add("Game Development");
        lines.add("");
        lines.add("Lead Developer: ");
        lines.add("Md. Maruf Hossain");
        lines.add("");  
        lines.add("Programmers: ");
        lines.add("Farah Azam Mirdha Toshy,");
        lines.add("Mohammad Fahim");
        lines.add("");  
        lines.add("Game Idea: ");
        lines.add("Farah Azam Mirdha Toshy");
        lines.add("");   
        lines.add("Game Designer: ");
        lines.add("Md. Maruf Hossain");
        lines.add("");   
        lines.add("UI/UX Designer: ");
        lines.add("Md. Maruf Hossain");
        lines.add("");   
        lines.add("Environmental Artist: ");
        lines.add("Md. Maruf Hossain");
        lines.add("");   
        lines.add("Composer: ");
        lines.add("Md. Maruf Hossain");
        lines.add("");
        lines.add("");   
        lines.add("Quality Assurance");
        lines.add("");   
        lines.add("Alpha Tester: ");
        lines.add("Mohammad Fahim");
        lines.add("");   
        lines.add("QA Tester: ");
        lines.add("Farah Azam Mirdha Toshy");
        lines.add("");
        lines.add("");   
        lines.add("Music");
        lines.add("Dragonscale by Float Overblow");
        lines.add("");
        lines.add("");  
        lines.add("Fonts Used");
        lines.add("04b03 by 04");
        lines.add("");
        lines.add("");
        lines.add("Special Thanks");
        lines.add("Raima Adhikary - AIUB");
        lines.add("Chris - YouTuber, Bro Code");
        lines.add("Mosh Hamedani - Programming with Mosh");
        lines.add("");
        lines.add("");
        lines.add("");
        lines.add("Thank You For Playing Our Game!");
        return lines;
    }

    public static void main(String[] args) {
        new Credits();
    }
}
