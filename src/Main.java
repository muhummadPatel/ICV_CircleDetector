package src;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main implements ActionListener {

    JFrame frame;
    JButton openButton;
    JPanel rootPanel;
    JPanel controlPanel;
    JScrollPane originalImageTab;
    JScrollPane edgeImageTab;
    JScrollPane circlesImageTab;
    JTabbedPane imagePanel;

    public Main() {
        //Invoke createAndShowGUI as a job for the EDT
        javax.swing.SwingUtilities.invokeLater(new
            Runnable() {
                public void run () {
                    createAndShowGUI();
                }
            }
        );
    }

    public static void main(String[] args) {
        new Main();
    }

    private void createAndShowGUI() {
        //Frame setup with root panel=====
        frame = new JFrame("Circle Detector");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(600, 600));

        rootPanel = new JPanel(); //Add everything to this rootPanel
        rootPanel.setLayout(new BorderLayout());

        //Tabbed Image Panel=====
        imagePanel = new JTabbedPane();
        rootPanel.add(imagePanel, BorderLayout.CENTER);
        originalImageTab = new JScrollPane();
        edgeImageTab = new JScrollPane();
        circlesImageTab = new JScrollPane();

        imagePanel.addTab("Original Image", originalImageTab);
        imagePanel.addTab("Detected Edges", edgeImageTab);
        imagePanel.addTab("Detected Circles", circlesImageTab);

        //Control Panel=====
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        openButton = new JButton("Open Image");
        openButton.addActionListener(this);
        controlPanel.add(openButton);

        rootPanel.add(controlPanel, BorderLayout.LINE_START);


        //Add root panel to frame and display=====
        frame.getContentPane().add(rootPanel);
        frame.pack();
        frame.setLocationRelativeTo(null); //centered on the screen
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        System.out.println("Main:actionPerformed handling event with actionCommand=" + actionCommand);

        if (e.getSource() == openButton) {
            icvController.handle_openItem(this);
        }

        icvController.handle_detectEdges(this);
        icvController.handle_detectCircles(this);
    }
}
