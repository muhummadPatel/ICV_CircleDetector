package src;

import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class icvFeatureDetector {

    public static BufferedImage detectCircles(BufferedImage img) {
        //Hough transform to find circles in the image.
        System.out.println("Detecting circles");

        //TODO: this is being duplicated when we find the edges. Fix that.
        int[][] pixels = new int[img.getWidth()][img.getHeight()];
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb & 0xFF);

                double gray = (0.2126 * r) + (0.7152 * g) + (0.0722 * b);
                pixels[x][y] = (int)(gray + 0.5);
            }
        }

        int minRadius = 10;
        int maxRadius = 200;
        int radiusStep = 5;
        int numRFrames = (maxRadius - minRadius) / radiusStep;
        int[][][] accumulator = new int[numRFrames][img.getWidth()][img.getHeight()];

        for (int rIdx = 0; rIdx < numRFrames; rIdx++) {
            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {
                    if (pixels[x][y] == 0) {
                        //Edge pixel
                        System.out.println(">>" + numRFrames + " " + rIdx + " " + (rIdx * radiusStep));
                        drawCircle(x, y, rIdx * radiusStep, accumulator[rIdx]);
                    }
                }
            }
        }

        for (int rIdx = 0; rIdx < numRFrames; rIdx++) {
            BufferedImage accumImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {

                    int colour = accumulator[rIdx][x][y];
                    if (colour > 255) { colour = 255; }
                    if (colour < 0) { colour = 0; }

                    accumImage.setRGB(x, y, (new Color(colour, colour, colour)).getRGB());
                }
            }

            //TODO: Let the user control whether to write this out through the UI or not
            try {
                File outputfile = new File((rIdx * radiusStep) + "R_accum.png");
                ImageIO.write(accumImage, "png", outputfile);
            } catch (IOException e) {
                System.out.println("Error saving accum: " + (rIdx * radiusStep));
            }
        }

        return null;
    }

    private static void drawCircle(int x0, int y0, int radius, int[][] accumulator) {
        int x = radius;
        int y = 0;
        int err = 0;

        while (x >= y) {
            incrementAccumulator(x0 + x, y0 + y, accumulator);
            incrementAccumulator(x0 + y, y0 + x, accumulator);
            incrementAccumulator(x0 - y, y0 + x, accumulator);
            incrementAccumulator(x0 - x, y0 + y, accumulator);
            incrementAccumulator(x0 - x, y0 - y, accumulator);
            incrementAccumulator(x0 - y, y0 - x, accumulator);
            incrementAccumulator(x0 + y, y0 - x, accumulator);
            incrementAccumulator(x0 + x, y0 - y, accumulator);

            y += 1;
            err += 1 + (2 * y);
            if ((2 * (err - x)) + 1 > 0) {
                x -= 1;
                err += 1 - (2 * x);
            }
        }
    }

    private static void incrementAccumulator(int x, int y, int[][] accumulator) {
        try {
            accumulator[x][y] += 1;
        } catch (ArrayIndexOutOfBoundsException e) { }
    }
}
