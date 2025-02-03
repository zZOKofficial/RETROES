import javax.swing.*;
import login.LoginFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            new LoginFrame();
        });
    }
}
