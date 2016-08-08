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
                BufferedImage bufferedImage = icvImageLoader.loadImage(filename);
                imageIcon = new ImageIcon(bufferedImage);
            } catch (IOException e) {
                System.out.println("icvController:openImage Could not open image - " + filename + "\n>" + e.getMessage());
                JOptionPane.showMessageDialog(null, "Sorry, image could not be opened.\nSee console output for errors.");
                return;
            }

            JLabel imageLabel = new JLabel(imageIcon);
            parent.originalImageTab.add(imageLabel);
            parent.originalImageTab.revalidate();
            parent.imagePanel.revalidate();
            parent.frame.pack();

            System.out.println("Done");
        }
    }
}
