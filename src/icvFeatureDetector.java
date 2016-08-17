package src;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

/**
 * icvFeatureDetector - This component uses the Hough transform to find the circles in the
 * given edge image and then returns a copy of the original image with the detected
 * circles overlayed in magenta. The behaviour of this circle detection can be tweaked
 * by adjusting the variables in icvConfig.java. Most notably, you can choose to save every
 * frame of the Hough accumulator (this looks very cool and helps visualise the behaviour
 * of the hough transform).
 * Note that the hough transform is space efficient and rather than using a full 3d buffer,
 * it uses a sliding window of 3 buffer frames.
 *
 * Muhummad Patel ptlmuh006
 * Aug 2016
 */
public class icvFeatureDetector {

    /*
     * Uses the hough transform to detect the circles in the edge image (img). It then
     * returns the originalImage with the detected circles overlayed in magenta.
     */
    public static BufferedImage detectCircles(BufferedImage img, BufferedImage originalImage) {
        System.out.println("Detecting circles");

        //pull the pixels out of the BufferedImage
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

        //Set up the hough transform parameters
        int minRadius = icvConfig.HOUGH_MIN_RADIUS;
        int maxRadius = icvConfig.HOUGH_MAX_RADIUS;
        int radiusStep = icvConfig.HOUGH_RADIUS_STEP;
        int numRFrames = (maxRadius - minRadius) / radiusStep;
        int[][][] accumulator = new int[3][img.getWidth()][img.getHeight()];
        float thresh = icvConfig.HOUGH_CIRCLE_DETECTION_THRESHOLD;
        ArrayList<Circle> circles = new ArrayList<>();

        //Compute the 0th and 1st frames of the sliding window
        for (int i = 0; i < 2; i++) {
            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {
                    if (pixels[x][y] == 0) {
                        //Edge pixel
                        //System.out.println(">>" + numRFrames + " " + rIdx + " " + (rIdx * radiusStep));
                        drawCircle(x, y, i * radiusStep + minRadius, accumulator[i+1]);
                    }
                }
            }

            accumulator[i+1] = smoothFrame(accumulator[i+1]);
        }

        //looping over the radii
        for (int rIdx = 0; rIdx < numRFrames; rIdx++) {

            //Find the local maxima in the current frame and add them to the circles list
            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {

                    int curr = accumulator[1][x][y];

                    if (curr < (thresh * (rIdx * radiusStep + minRadius))) {
                        continue;
                    }

                    //Check that curr accum val is greater than any value in the 'cube' around it (local maxima)
                    boolean currIsLocalMaximum =
                        curr > getAccumulatorValue(x-1, y-1, accumulator[1]) &&
                        curr > getAccumulatorValue(x, y-1, accumulator[1]) &&
                        curr > getAccumulatorValue(x+1, y-1, accumulator[1]) &&
                        curr > getAccumulatorValue(x-1, y, accumulator[1]) &&
                        curr > getAccumulatorValue(x+1, y, accumulator[1]) &&
                        curr > getAccumulatorValue(x-1, y+1, accumulator[1]) &&
                        curr > getAccumulatorValue(x, y+1, accumulator[1]) &&
                        curr > getAccumulatorValue(x+1, y+1, accumulator[1]);

                        if (rIdx > 0) {
                            currIsLocalMaximum = currIsLocalMaximum &&
                            curr > getAccumulatorValue(x-1, y-1, accumulator[0]) &&
                            curr > getAccumulatorValue(x, y-1, accumulator[0]) &&
                            curr > getAccumulatorValue(x+1, y-1, accumulator[0]) &&
                            curr > getAccumulatorValue(x-1, y, accumulator[0]) &&
                            curr > getAccumulatorValue(x, y, accumulator[0]) &&
                            curr > getAccumulatorValue(x+1, y, accumulator[0]) &&
                            curr > getAccumulatorValue(x-1, y+1, accumulator[0]) &&
                            curr > getAccumulatorValue(x, y+1, accumulator[0]) &&
                            curr > getAccumulatorValue(x+1, y+1, accumulator[0]);
                        }

                        if ((rIdx + 1) < numRFrames) {
                            currIsLocalMaximum = currIsLocalMaximum &&
                            curr > getAccumulatorValue(x-1, y-1, accumulator[2]) &&
                            curr > getAccumulatorValue(x, y-1, accumulator[2]) &&
                            curr > getAccumulatorValue(x+1, y-1, accumulator[2]) &&
                            curr > getAccumulatorValue(x-1, y, accumulator[2]) &&
                            curr > getAccumulatorValue(x, y, accumulator[2]) &&
                            curr > getAccumulatorValue(x+1, y, accumulator[2]) &&
                            curr > getAccumulatorValue(x-1, y+1, accumulator[2]) &&
                            curr > getAccumulatorValue(x, y+1, accumulator[2]) &&
                            curr > getAccumulatorValue(x+1, y+1, accumulator[2]);
                        }


                    if (currIsLocalMaximum) {
                        circles.add(new Circle(x, y, (rIdx * radiusStep + minRadius), curr));
                    }
                }
            }

            //Compute next frame and move the window forward
            //accumulator[0] = accumulator[1]; and accumulator[1] = accumulator[2];
            for (int i = 0; i < 2; i++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    for (int y = 0; y < img.getHeight(); y++) {
                        accumulator[i][x][y] = accumulator[i+1][x][y];
                    }
                }
            }
            //compute accumulator[2]
            if (rIdx+2 < numRFrames) {
                accumulator[2] = new int[img.getWidth()][img.getHeight()];
                for (int x = 0; x < img.getWidth(); x++) {
                    for (int y = 0; y < img.getHeight(); y++) {
                        if (pixels[x][y] == 0) {
                            //Edge pixel
                            drawCircle(x, y, (rIdx+2) * radiusStep + minRadius, accumulator[2]);
                        }
                    }
                }
                accumulator[2] = smoothFrame(accumulator[2]);
            }

            //If the user chose to write out the hough accum buffer, then save the current frame
            if (icvConfig.HOUGH_SAVE_BUFFER) {
                BufferedImage accumImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
                for (int x = 0; x < img.getWidth(); x++) {
                    for (int y = 0; y < img.getHeight(); y++) {

                        int colour = accumulator[1][x][y];
                        if (colour > 255) { colour = 255; }
                        if (colour < 0) { colour = 0; }

                        accumImage.setRGB(x, y, (new Color(colour, colour, colour)).getRGB());
                    }
                }

                try {
                    File outputfile = new File((rIdx * radiusStep + minRadius) + "R_accum.png");
                    ImageIO.write(accumImage, "png", outputfile);
                } catch (IOException e) {
                    System.out.println("Error saving accum: " + (rIdx * radiusStep + minRadius));
                }
            }
        }

        //sort detected circles in order of confidence and print them out to the console
        Collections.sort(circles);
        for (Circle c: circles) {
            System.out.println(c);
        }
        System.out.println("CIRCLES FOUND:_____" + circles.size());

        //Draw circles (up to the specified max) onto a copy of img and return it.
        Collections.sort(circles);
        int[][] circleMatrix = new int[img.getWidth()][img.getHeight()];
        for (int i = 0; i < Math.min(circles.size(), icvConfig.HOUGH_MAX_EXPECTED_CIRCLES); i++) {
            Circle c = circles.get(i);
            drawCircle(c.x, c.y, c.radius, circleMatrix);
        }

        BufferedImage circleImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {

                int colour = circleMatrix[x][y];
                if (colour > 255) { colour = 255; }
                if (colour < 0) { colour = 0; }

                if (colour > 0) {
                    circleImage.setRGB(x, y, (new Color(1.0f, 0.0f, 1.0f)).getRGB());
                } else {
                    circleImage.setRGB(x, y, originalImage.getRGB(x, y));
                }
            }
        }

        return circleImage;
    }

    /*
     * Increments the value of accumulator in the circle with radius centred at x0 and y0
     */
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

    /*
     * tries to return the value of the accumulator at the given x and y. Returns 999
     * if the value does not exist.
     */
    private static int getAccumulatorValue(int x, int y, int[][] accumulator) {
        try {
            return accumulator[x][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            return 999; //So as to not interfere with the maxima calculations.
        }
    }

    /*
     * tries to increment the value of the accumulator at the given x and y. Does nothing
     * if the value does not exist.
     */
    private static void incrementAccumulator(int x, int y, int[][] accumulator) {
        try {
            accumulator[x][y] += 1;
        } catch (ArrayIndexOutOfBoundsException e) { }
    }

    /*
     * Returns a copy of accumulator that has been smoothed using a gaussian filter.
     */
    private static int[][] smoothFrame (int[][] accumulator) {
        double[][] gaussian = icvConfig.GAUSSIAN_5;
        int width = accumulator.length;
        int height = accumulator[0].length;
        int[][] smoothedAccum = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                double smoothed = 0;
                for (int xOff = -2; xOff < 3; xOff++) {
                    for (int yOff = -2; yOff < 3; yOff++) {
                        int pixX = x + xOff;
                        int pixY = y + yOff;

                        int kerX = xOff + 1;
                        int kerY = yOff + 1;

                        try {
                            smoothed += (accumulator[pixX][pixY] * gaussian[kerX][kerY]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            //TODO: Reflect values around the boundaries instead?
                            smoothed += 0;
                        }
                    }
                }

                smoothedAccum[x][y] = (int)(smoothed + 0.5f);
            }
        }

        return smoothedAccum;
    }

    /*
     * Class to represent the circles we finf in each frame. Circles can be compared based on their votes.
     * Circles with more votes are circles that we are more confident about.
     */
    private static class Circle implements Comparable<Circle>{
        int x, y, radius, votes;

        Circle(int x, int y, int radius, int votes) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.votes = votes;
        }

        @Override
        public String toString() {
            return "<Circle: " + "x=" + x + ", y=" + y + ", r=" + radius + ", votes=" + votes + ">";
        }

        @Override
        public int compareTo(Circle that) {
            Integer thisVotes = new Integer(this.votes);
            Integer thatVotes = new Integer(that.votes);

            return thatVotes.compareTo(thisVotes);
        }
    }
}
