package src;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class icvFeatureDetector {

    public static BufferedImage detectCircles(BufferedImage img, BufferedImage originalImage) {
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

        int minRadius = 15;
        int maxRadius = 200;
        int radiusStep = 5;
        int numRFrames = (maxRadius - minRadius) / radiusStep;
        int[][][] accumulator = new int[numRFrames][img.getWidth()][img.getHeight()];

        for (int rIdx = 0; rIdx < numRFrames; rIdx++) {
            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {
                    if (pixels[x][y] == 0) {
                        //Edge pixel
                        //System.out.println(">>" + numRFrames + " " + rIdx + " " + (rIdx * radiusStep));
                        drawCircle(x, y, rIdx * radiusStep + minRadius, accumulator[rIdx]);
                    }
                }
            }
        }

        //TODO: Maybe run a gaussian filter here to kill noise
        double[][] gaussian = {
            {0.003765, 0.015019, 0.023792, 0.015019, 0.003765},
            {0.015019, 0.059912, 0.094907, 0.059912, 0.015019},
            {0.023792, 0.094907, 0.150342, 0.094907, 0.023792},
            {0.015019, 0.059912, 0.094907, 0.059912, 0.015019},
            {0.003765, 0.015019, 0.023792, 0.015019, 0.003765}
        };
        int[][][] smoothedAccum = new int[numRFrames][img.getWidth()][img.getHeight()];
        for (int rIdx = 0; rIdx < numRFrames; rIdx++) {
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
                                smoothed += (accumulator[rIdx][pixX][pixY] * gaussian[kerX][kerY]);
                            } catch (ArrayIndexOutOfBoundsException e) {
                                //TODO: Reflect values around the boundaries instead?
                                smoothed += 0;
                            }
                        }
                    }

                    smoothedAccum[rIdx][x][y] = (int)(smoothed + 0.5f);
                }
            }
        }

        accumulator = smoothedAccum;

        ArrayList<Circle> circles = new ArrayList<>();
        float thresh = 2.0f;
        for (int rIdx = 0; rIdx < numRFrames; rIdx++) {
            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {

                    int curr = accumulator[rIdx][x][y];

                    if (curr < (thresh * (rIdx * radiusStep + minRadius))) {
                        continue;
                    }

                    //Check that curr accum val is greater than the 8 + 9 + 9 around it
                    boolean currIsLocalMaximum =
                        curr > getAccumulatorValue(x-1, y-1, accumulator[rIdx]) &&
                        curr > getAccumulatorValue(x, y-1, accumulator[rIdx]) &&
                        curr > getAccumulatorValue(x+1, y-1, accumulator[rIdx]) &&
                        curr > getAccumulatorValue(x-1, y, accumulator[rIdx]) &&
                        curr > getAccumulatorValue(x+1, y, accumulator[rIdx]) &&
                        curr > getAccumulatorValue(x-1, y+1, accumulator[rIdx]) &&
                        curr > getAccumulatorValue(x, y+1, accumulator[rIdx]) &&
                        curr > getAccumulatorValue(x+1, y+1, accumulator[rIdx]);

                    if (rIdx > 0) {
                        currIsLocalMaximum = currIsLocalMaximum &&
                        curr > getAccumulatorValue(x-1, y-1, accumulator[rIdx-1]) &&
                        curr > getAccumulatorValue(x, y-1, accumulator[rIdx-1]) &&
                        curr > getAccumulatorValue(x+1, y-1, accumulator[rIdx-1]) &&
                        curr > getAccumulatorValue(x-1, y, accumulator[rIdx-1]) &&
                        curr > getAccumulatorValue(x, y, accumulator[rIdx-1]) &&
                        curr > getAccumulatorValue(x+1, y, accumulator[rIdx-1]) &&
                        curr > getAccumulatorValue(x-1, y+1, accumulator[rIdx-1]) &&
                        curr > getAccumulatorValue(x, y+1, accumulator[rIdx-1]) &&
                        curr > getAccumulatorValue(x+1, y+1, accumulator[rIdx-1]);
                    }

                    if ((rIdx + 1) < numRFrames) {
                        currIsLocalMaximum = currIsLocalMaximum &&
                        curr > getAccumulatorValue(x-1, y-1, accumulator[rIdx+1]) &&
                        curr > getAccumulatorValue(x, y-1, accumulator[rIdx+1]) &&
                        curr > getAccumulatorValue(x+1, y-1, accumulator[rIdx+1]) &&
                        curr > getAccumulatorValue(x-1, y, accumulator[rIdx+1]) &&
                        curr > getAccumulatorValue(x, y, accumulator[rIdx+1]) &&
                        curr > getAccumulatorValue(x+1, y, accumulator[rIdx+1]) &&
                        curr > getAccumulatorValue(x-1, y+1, accumulator[rIdx+1]) &&
                        curr > getAccumulatorValue(x, y+1, accumulator[rIdx+1]) &&
                        curr > getAccumulatorValue(x+1, y+1, accumulator[rIdx+1]);
                    }

                    if (currIsLocalMaximum) {
                        circles.add(new Circle(x, y, (rIdx * radiusStep + minRadius), curr));
                    }
                }
            }
        }

        // Collections.sort(circles);
        // for (Circle c: circles) {
        //     System.out.println(c);
        // }
        System.out.println("CIRCLES FOUND:_____" + circles.size());

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
                File outputfile = new File((rIdx * radiusStep + minRadius) + "R_accum.png");
                ImageIO.write(accumImage, "png", outputfile);
            } catch (IOException e) {
                System.out.println("Error saving accum: " + (rIdx * radiusStep + minRadius));
            }
        }

        //Draw circles onto a copy of img and return it.
        Collections.sort(circles);
        int[][] circleMatrix = new int[img.getWidth()][img.getHeight()];
        for (int i = 0; i < Math.min(circles.size(), 20); i++) {
            Circle c = circles.get(i);
            drawCircle(c.x, c.y, c.radius, circleMatrix);
        }
        BufferedImage circleImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
        // Graphics g = circleImage.getGraphics();
        // g.drawImage(originalImage, 0, 0, null);
        // g.dispose();
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {

                int colour = circleMatrix[x][y];
                if (colour > 255) { colour = 255; }
                if (colour < 0) { colour = 0; }

                if (colour > 0) {
                    circleImage.setRGB(x, y, (new Color(1.0f, 0.0f, 0.0f)).getRGB());
                } else {
                    circleImage.setRGB(x, y, originalImage.getRGB(x, y));
                }
            }
        }

        return circleImage;
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

    private static int getAccumulatorValue(int x, int y, int[][] accumulator) {
        try {
            return accumulator[x][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            return 999;
        }
    }

    private static void incrementAccumulator(int x, int y, int[][] accumulator) {
        try {
            accumulator[x][y] += 1;
        } catch (ArrayIndexOutOfBoundsException e) { }
    }

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
