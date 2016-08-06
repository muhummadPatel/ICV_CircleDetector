import javax.swing.*;

public class Main {

    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Circle Detector");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel rootPanel = new JPanel(); //Add everything to the rootPanel

        JLabel label = new JLabel("Hello World");
        rootPanel.add(label);

        frame.getContentPane().add(rootPanel);

        //Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null); //centered on the screen
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Invoke createAndShowGUI as a job for the EDT
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
