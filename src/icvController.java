package src;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * icvController - Handles the actions performed in the GUI. This helps keep the
 * code modular and seperates the logic from the GUI.
 *
 * Muhummad Patel ptlmuh006
 * Aug 2016
 */
public class icvController{
    //Keeps copies of the various images for future reference
    public static BufferedImage originalImage = null;
    public static BufferedImage edgeImage = null;
    public static BufferedImage circleImage = null;

    /*
     * When the user clicks the open file button, they are presented with a
     * filechooser and the selected image file is then opened and displayed in
     * the originalImage in the GUI.
     */
    public static void handle_openItem(Main parent) {
        JFileChooser fileChooser = new JFileChooser();
        //Only allow user to open image files that we know how to load
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images", ImageIO.getReaderFileSuffixes()));

        //try to open the chosen image file
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getAbsolutePath();
            System.out.println(">Opening image: " + filename);

            ImageIcon imageIcon;
            try {
                originalImage = icvImageLoader.loadImage(filename);
                imageIcon = new ImageIcon(originalImage);
            } catch (IOException e) {
                System.out.println("icvController:openImage Could not open image - " + filename + "\n>" + e.getMessage());
                JOptionPane.showMessageDialog(null, "Sorry, image could not be opened.\nSee console output for errors.");
                originalImage = null;
                return;
            }

            //display the image in the original image tab of the GUI
            JLabel imageLabel = new JLabel(imageIcon);
            parent.originalImageTab.getViewport().removeAll();
            parent.originalImageTab.getViewport().add(imageLabel);
            parent.originalImageTab.revalidate();
            parent.imagePanel.revalidate();

            parent.frame.setTitle("Circle Detector - " + filename);

            System.out.println("Done");
        }
    }

    /*
     * Find the edges in the originalImage and display the edge image in the
     * edge image tab in the GUI.
     */
    public static void handle_detectEdges(Main parent) {
        edgeImage = icvEdgeDetector.detectEdges(originalImage);
        JLabel imageLabel = new JLabel(new ImageIcon(edgeImage));
        parent.edgeImageTab.getViewport().removeAll();
        parent.edgeImageTab.getViewport().add(imageLabel);
        parent.edgeImageTab.revalidate();
        parent.imagePanel.revalidate();

        System.out.println("Done");
    }

    /*
     * Find the circles in the originalImage and display the circle image in the
     * circle image tab in the GUI.
     */
    public static void handle_detectCircles(Main parent) {
        circleImage = icvFeatureDetector.detectCircles(edgeImage, originalImage);
        JLabel imageLabel = new JLabel(new ImageIcon(circleImage));
        parent.circlesImageTab.getViewport().removeAll();
        parent.circlesImageTab.getViewport().add(imageLabel);
        parent.circlesImageTab.revalidate();
        parent.imagePanel.revalidate();

        System.out.println("Done");
    }
}
