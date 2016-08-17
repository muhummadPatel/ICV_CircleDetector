package src;

/**
 * icvConfig - Allows the user to tweak the behaviour of the program by
 * adjusting the parameters for the sobel edge detection, and the hough
 * transform. Note that these default values have been set to work well with the
 * type of test images provided as part of the assignment.
 *
 * Muhummad Patel ptlmuh006
 * Aug 2016
 */
public class icvConfig {

    //Higher values will be more strict about what constitutes an 'edge' and
    //may result in thinner edges.
    public static int SOBEL_EDGE_DETECTION_THRESHOLD = 200;

    //minimum radius of circles to find in the input image
    public static int HOUGH_MIN_RADIUS = 10;

    //maximum radius of circles to find in the input image
    public static int HOUGH_MAX_RADIUS = 120;

    //increment used to step between the min and max radii
    public static int HOUGH_RADIUS_STEP = 5;

    //Higher values are more strict and will require more votes in the hough
    //accumulation buffer to be considered a circle
    public static float HOUGH_CIRCLE_DETECTION_THRESHOLD = 2.0f;

    //if true, the program will save all the frames in the buffer as images in
    //the working directory. This is good to visualise the behaviour of the hough
    //transform but it will really slow down the process (~2/3 seconds)
    public static boolean HOUGH_SAVE_BUFFER = false;

    //The maximum number of circles we are expecting to find in our input images.
    public static int HOUGH_MAX_EXPECTED_CIRCLES = 25;

    //Gaussian filter used to smooth the images.
    public static double[][] GAUSSIAN_5 = {
        {0.003765, 0.015019, 0.023792, 0.015019, 0.003765},
        {0.015019, 0.059912, 0.094907, 0.059912, 0.015019},
        {0.023792, 0.094907, 0.150342, 0.094907, 0.023792},
        {0.015019, 0.059912, 0.094907, 0.059912, 0.015019},
        {0.003765, 0.015019, 0.023792, 0.015019, 0.003765}
    };
}
