package GUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        JFrame window = new JFrame("Chess");

        MyButton button = new MyButton("x");
        window.add(button);

        // Closes the window to ensure it is not running when not in use
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Make the window size fixed
        window.setResizable(false);

        // Add GamePanel to the window
        GamePanel gp = new GamePanel();
        window.add(gp);
        // window adjusts its size to the GamePanel
        window.pack();

        // Set window location to middle of monitor
        window.setLocationRelativeTo(null);

        // Make the window visible
        window.setVisible(true);

        // Once the window is created, we call the launchGame method to start the thread and call the run() method
        gp.launchGame();
    }
}
