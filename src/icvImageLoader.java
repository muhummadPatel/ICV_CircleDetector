package src;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class icvImageLoader {

    public static BufferedImage loadImage(String filename) throws IOException {
        //Load a bufferedImage and return it (RGB colourspace)
        BufferedImage img = ImageIO.read(new File(filename));
        return img;
    }

    public static BufferedImage loadGrayscaleImage(String filename) throws IOException {
        //Load an image as normal
        BufferedImage colourImg = loadImage(filename);

        //Create an empty grayscale image with same dimensions as the loaded image
        BufferedImage grayImg = new BufferedImage(colourImg.getWidth(), colourImg.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        //Draw the loaded image into the grayscale image
        Graphics g = grayImg.getGraphics();
        g.drawImage(colourImg, 0, 0, null);
        g.dispose();

        return grayImg;
    }
}