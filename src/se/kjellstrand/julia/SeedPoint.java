
package se.kjellstrand.julia;

public class SeedPoint {

    private static final double SWIPE_SMAL_OFFSET_DIV = 1009;

    private static final double SWIPE_BIG_OFFSET_DIV = 10061;

    public static double[] get(double swipeOffset) {
        double[] point = new double[2];

        // ......0.67....
        // -0.79......0.5
        // .....-0.67....
        double minX = -0.79d;
        double maxX = 0.5d;
        double maxY = 0.67d;

        double bigArcX = ((Math.cos(swipeOffset / SWIPE_BIG_OFFSET_DIV) + 1d) / 2d) * (maxX - minX) + minX;
        double bigArcY = Math.sin(swipeOffset / SWIPE_BIG_OFFSET_DIV) * maxY;

        double maxSmalXY = 0.03;

        double smalArcX = Math.cos(swipeOffset / SWIPE_SMAL_OFFSET_DIV) * maxSmalXY;
        double smalArcY = Math.sin(swipeOffset / SWIPE_SMAL_OFFSET_DIV) * maxSmalXY;

        point[0] = bigArcX + smalArcX;
        point[1] = bigArcY + smalArcY;

        return point;
    }

}
