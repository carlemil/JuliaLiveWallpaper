
package se.kjellstrand.julia;

public class JuliaSeeds {

    /**
     * Starting points from the Mandelbrot set used to seed the Julia set.
     */
    private static double[][] juliaSeeds = new double[][] {
            {
                    -0.9259259, 0.30855855
            },//
            {
                    0.41851854, 0.42567563
            },//
            {
                    -0.08888888, 0.93468475
            },//
            {
                    -0.57777774, 0.6554055
            },//
            {
                    -1.1592593, 0.33333325
            },//
            {
                    -1.3962963, 0.10810804
            },//
            {
                    -0.7740741, -0.34909904
            },//
            {
                    -0.67777777, -0.5112612
            },//
            {
                    -0.2481482, -0.8626126
            },//
            {
                    0.437037, 0.18693686
            },//
            {
                    0.47777772, -0.22072077
            },//
            {
                    -0.26666665, -0.7995496
            },//
            {
                    -0.17037034, -0.8941442
            },//
            {
                    -1.0333333, -0.40090096
            },//
            {
                    0.34074068, -0.6486486
            },//
            {
                    0.36666656, 0.06531525
            },//
            {
                    0.45555544, 0.13513517
            },//
            {
                    0.36296296, -0.042792797
            },//
            {
                    0.44074082, 0.25900912
            },//
            {
                    -0.79629624, 0.21846843
            },//
            {
                    -0.80370367, -0.1981982
            },//
            {
                    -0.88518524, 0.27702713
            }
    };

    private static final double FIRST_SIZE = 0.1;

    private static final double FIRST_SEED_MUL = 4.0;

    private static final double SECOND_SIZE = FIRST_SIZE * 0.2;

    private static final double SECOND_SEED_MUL = FIRST_SEED_MUL * 6;

    // /*
    // * right x: 3.916667e-1 y: 4.166667e-3
    // * bottom x: -1.250000e-1 y: -6.500000e-1
    // * left x: -7.541667e-1 y: 8.333333e-3
    // * top x: -1.333333e-1 y: 6.625000e-1
    // */

    private static int[] getCircle(double radius, int n) {
        final int[] data = new int[n * 2];

        long time = System.currentTimeMillis();
        long unixHours = time / 1000 / 60 / 60;
        System.out.println("hour " + unixHours);
        double swipeXOffset = 0;
        double swipeYOffset = 0;

        double timeOffset = unixHours / 21d; // time based, div by 21 to slow it
                                             // down and avoid daily repetition
        double bigArc = swipeYOffset + timeOffset;

        double smalArc = swipeXOffset / 2000d; // swipe up and down

        for (int i = 0; i < n; i++) {

            double a = i / (double) n * Math.PI * 2;
            data[i * 2] = (int) (Math.sin(a) * 13d - 13d / 2d);
            data[i * 2 + 1] = (int) ((Math.cos(a) * 11.5d) + 5.75d);
            System.out.println("data[i] " + data[i * 2] + "  " + data[i * 2 + 1]);
        }

        return data;
    }

    static double getX(double xOffset, int seed) {
        return (double) ((Math.sin(xOffset * FIRST_SEED_MUL) * FIRST_SIZE) + (Math.sin(xOffset / SECOND_SEED_MUL) * SECOND_SIZE))
                + juliaSeeds[seed][0];
    }

    static double getY(double xOffset, int seed) {
        return (double) ((Math.cos(xOffset * FIRST_SEED_MUL) * FIRST_SIZE) + (Math.cos(xOffset / SECOND_SEED_MUL) * SECOND_SIZE))
                + juliaSeeds[seed][1];
    }

    static int getNumberOfSeeds() {
        return juliaSeeds.length;
    }

}
