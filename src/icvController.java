package src;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EventListener;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;

public class icvController{
    public static BufferedImage originalImage = null;
    public static BufferedImage edgeImage = null;
    public static BufferedImage circleImage = null;

    public static void handle_openItem(Main parent) {
        JFileChooser fileChooser = new JFileChooser();
        //Only allow user to open image files that we know how to load
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images", ImageIO.getReaderFileSuffixes()));

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

            JLabel imageLabel = new JLabel(imageIcon);
            parent.originalImageTab.getViewport().removeAll();
            parent.originalImageTab.getViewport().add(imageLabel);
            parent.originalImageTab.revalidate();
            parent.imagePanel.revalidate();

            System.out.println("Done");
        }
    }

    public static void handle_detectEdgesButton(Main parent) {
        if (originalImage == null) {
            JOptionPane.showMessageDialog(null, "Please open an image before trying to detect edges.");
            return;
        }

        edgeImage = icvEdgeDetector.detectEdges(originalImage);
        JLabel imageLabel = new JLabel(new ImageIcon(edgeImage));
        parent.edgeImageTab.getViewport().removeAll();
        parent.edgeImageTab.getViewport().add(imageLabel);
        parent.edgeImageTab.revalidate();
        parent.imagePanel.revalidate();

        System.out.println("Done");
    }

    public static void handle_detectCirclesButton(Main parent) {
        if (originalImage == null || edgeImage == null) {
            JOptionPane.showMessageDialog(null, "Please open an image and detect edges before trying to detect circles.");
            return;
        }

        circleImage = icvFeatureDetector.detectCircles(edgeImage, originalImage);
        JLabel imageLabel = new JLabel(new ImageIcon(circleImage));
        parent.circlesImageTab.getViewport().removeAll();
        parent.circlesImageTab.getViewport().add(imageLabel);
        parent.circlesImageTab.revalidate();
        parent.imagePanel.revalidate();

        System.out.println("Done");
    }
}
