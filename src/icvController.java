package src;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EventListener;

public class icvController{

    public static BufferedImage openImage(String filename) {
        try {
            return icvImageLoader.loadImage(filename);
        } catch (IOException e) {
            System.out.println("icvController:openImage Could not open image - " + filename + "\n>" + e.getMessage());
            return null;
        }
    }
}