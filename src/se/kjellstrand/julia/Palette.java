
package se.kjellstrand.julia;

public class Palette {

    public enum Type {
        KAZAKH_FLAG, GRAY_SCALE, INVERTED_GRAY_SCALE
    }

    public static byte[] getPalette(Type type, int precision) {
        byte[] d = new byte[precision * 3];

        for (int i = 0; i < precision; i++) {
            switch (type) {
                case KAZAKH_FLAG:
                    d[i * 3 + 0] = (byte) (((Math.cos(i / (double) precision * Math.PI) + 1d) / 2d) * 255);
                    d[i * 3 + 1] = (byte) (((Math.sin(i / (double) precision * Math.PI / 3) + 1d) / 2d) * 255);
                    d[i * 3 + 2] = (byte) (((Math.sin(i / (double) precision * Math.PI) + 1d) / 2d) * 255);
                    break;
                case GRAY_SCALE:
                    d[i * 3 + 0] = (byte) ((i / (double) precision) * 255);
                    d[i * 3 + 1] = d[i * 3];
                    d[i * 3 + 2] = d[i * 3];
                    break;
                case INVERTED_GRAY_SCALE:
                    d[i * 3 + 0] = (byte) (255 - (i / (double) precision) * 255);
                    d[i * 3 + 1] = d[i * 3];
                    d[i * 3 + 2] = d[i * 3];
                    break;

                default:
                    break;
            }
        }
        return d;
    }

}
