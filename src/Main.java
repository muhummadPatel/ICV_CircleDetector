package src;

import java.awt.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main implements ActionListener {

    JButton detectEdgesButton;
    JButton detectCirclesButton;
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
        frame.setMinimumSize(new Dimension(60, 40));

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

        detectEdgesButton = new JButton("Detect Edges");
        detectEdgesButton.addActionListener(this);
        controlPanel.add(detectEdgesButton);

        detectCirclesButton = new JButton("Detect Circles");
        detectCirclesButton.addActionListener(this);
        controlPanel.add(detectCirclesButton);

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
        } else if (e.getSource() == detectEdgesButton) {
            icvController.handle_detectEdgesButton(this);
        } else if (e.getSource() == detectCirclesButton) {
            icvController.handle_detectCirclesButton(this);
        }
    }
}
