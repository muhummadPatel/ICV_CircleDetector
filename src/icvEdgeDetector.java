package src;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.image.*;

public class icvEdgeDetector {

    public static BufferedImage detectEdges(BufferedImage img) {
        //make sure the image is a grayscale image. If not, convert it to grayscale.
        //Use sobel filter to detect the edges in the image.
        //Return a new BufferedImage with only edge pixels and bg pixels.
        //DONT touch the input image img!

        int[][] gx = {
            {-1,  0,  1},
            {-2,  0,  2},
            {-1,  0,  1}
        };

        int[][] gy = {
            {-1, -2, -1},
            { 0,  0,  0},
            { 1,  2,  1}
        };

        double[][] pixels = new double[img.getWidth()][img.getHeight()];
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb & 0xFF);

                double gray = (0.2126 * r) + (0.7152 * g) + (0.0722 * b);
                pixels[x][y] = gray;
            }
        }

        //gaussian smooth
        double[][] smoothedPixels = new double[img.getWidth()][img.getHeight()];
        double[][] gaussian = icvConfig.GAUSSIAN_5;
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {

                double smoothed = 0;
                for (int xOff = -2; xOff < 3; xOff++) {
                    for (int yOff = -2; yOff < 3; yOff++) {
                        int pixX = x + xOff;
                        int pixY = y + yOff;

                        int kerX = xOff + 1;
                        int kerY = yOff + 1;

                        try {
                            smoothed += (pixels[pixX][pixY] * gaussian[kerX][kerY]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            //TODO: Reflect values around the boundaries instead?
                            smoothed += 0;
                        }
                    }
                }

                smoothedPixels[x][y] = smoothed;
            }
        }
        pixels = smoothedPixels;

        //For each pixel at (x,y):
        double[][] newPixels = new double[img.getWidth()][img.getHeight()];
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {

                double xGradMagnitude = 0;
                //Perform Gx convolution
                for (int xOff = -1; xOff < 2; xOff++) {
                    for (int yOff = -1; yOff < 2; yOff++) {
                        int pixX = x + xOff;
                        int pixY = y + yOff;

                        int kerX = xOff + 1;
                        int kerY = yOff + 1;

                        try {
                            xGradMagnitude += (pixels[pixX][pixY] * gx[kerX][kerY]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            //TODO: Reflect values around the boundaries instead?
                            xGradMagnitude += 0;
                        }
                    }
                }

                double yGradMagnitude = 0;
                //Perform Gy convolution
                for (int xOff = -1; xOff < 2; xOff++) {
                    for (int yOff = -1; yOff < 2; yOff++) {
                        int currX = x + xOff;
                        int currY = y + yOff;

                        int kerX = xOff + 1;
                        int kerY = yOff + 1;

                        try {
                            yGradMagnitude += (pixels[currX][currY] * gy[kerX][kerY]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            //TODO: Reflect values around the boundaries instead?
                            yGradMagnitude += 0;
                        }
                    }
                }

                double grad = Math.sqrt(Math.pow(xGradMagnitude, 2) + Math.pow(yGradMagnitude, 2));
                newPixels[x][y] = grad;
            }
        }

        //Generate the edgeImage
        BufferedImage edgeImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        int threshold = icvConfig.SOBEL_EDGE_DETECTION_THRESHOLD;
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {

                if (newPixels[x][y] > -1 * threshold && newPixels[x][y] < threshold) {
                    edgeImage.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    edgeImage.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }

        return edgeImage;
    }
}
