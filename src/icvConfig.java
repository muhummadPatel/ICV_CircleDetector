package src;

public class icvConfig {

    public static int SOBEL_EDGE_DETECTION_THRESHOLD = 200;

    public static int HOUGH_MIN_RADIUS = 10;
    public static int HOUGH_MAX_RADIUS = 120;
    public static int HOUGH_RADIUS_STEP = 5;
    public static float HOUGH_CIRCLE_DETECTION_THRESHOLD = 2.0f;
    public static boolean HOUGH_SAVE_BUFFER = false;
    public static int HOUGH_MAX_EXPECTED_CIRCLES = 25;

    public static double[][] GAUSSIAN_5 = {
        {0.003765, 0.015019, 0.023792, 0.015019, 0.003765},
        {0.015019, 0.059912, 0.094907, 0.059912, 0.015019},
        {0.023792, 0.094907, 0.150342, 0.094907, 0.023792},
        {0.015019, 0.059912, 0.094907, 0.059912, 0.015019},
        {0.003765, 0.015019, 0.023792, 0.015019, 0.003765}
    };
}
