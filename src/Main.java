package src;

import java.awt.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main implements ActionListener {

    private JFrame frame;
    private JMenu fileMenu;
    private JMenuBar menuBar;
    private JMenuItem openItem;
    private JPanel rootPanel;
    private JTabbedPane imagePanel;

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

        //MenuBar=====
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");

        openItem = new JMenuItem("Open Image");
        openItem.addActionListener(this);

        fileMenu.add(openItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);

        //Image Panel=====
        imagePanel = new JTabbedPane();
        rootPanel.add(imagePanel, BorderLayout.CENTER);


        //Add root panel to frame and display=====
        frame.getContentPane().add(rootPanel);
        frame.pack();
        frame.setLocationRelativeTo(null); //centered on the screen
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        System.out.println("Main:actionPerformed handling event with actionCommand=" + actionCommand);

        if (e.getSource() == openItem) {
            JFileChooser fileChooser = new JFileChooser();
            //Only allow user to open image files that we know how to load
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Images", ImageIO.getReaderFileSuffixes()));

            int returnVal = fileChooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                System.out.println(">Opening image: " + filename);

                JLabel imageLabel = new JLabel(new ImageIcon(icvController.openImage(filename)));
                JPanel imageTab = new JPanel();
                imageTab.add(imageLabel);
                imageTab.revalidate();

                imagePanel.addTab("Original Image", imageTab);
                imagePanel.revalidate();

                frame.pack();
                System.out.println("Done");

            }

        }
    }
}
