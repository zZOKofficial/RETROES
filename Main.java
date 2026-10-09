import app.AppVersion;

import javax.swing.*;
import login.LoginFrame;

public class Main {
    public static void main(String[] args) {
        System.out.println("RETROES v" + AppVersion.VERSION);
        SwingUtilities.invokeLater(() -> {

            new LoginFrame();
        });
    }
}
