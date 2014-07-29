
package se.kjellstrand.julia;

public class SeedPoint {

    private static final double SWIPE_OFFSET_DIV = 10061;

    public static double[] get(double swipeXOffset, double swipeYOffset) {
        double[] point = new double[2];

        // ......0.67....
        // -0.79......0.5
        // .....-0.67....
        double minX = -0.79d;
        double maxX = 0.5d;
        double maxY = 0.67d;

        double bigArcX = ((Math.cos(swipeYOffset / SWIPE_OFFSET_DIV) + 1d) / 2d) * (maxX - minX) + minX;
        double bigArcY = Math.sin(swipeYOffset / SWIPE_OFFSET_DIV) * maxY;

        double maxSmalXY = 0.03;

        double smalArcX = Math.cos(swipeXOffset) * maxSmalXY;
        double smalArcY = Math.sin(swipeXOffset) * maxSmalXY;

        point[0] = bigArcX + smalArcX;
        point[1] = bigArcY + smalArcY;

        return point;
    }

}
