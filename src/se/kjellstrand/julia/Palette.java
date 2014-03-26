
package se.kjellstrand.julia;

import android.graphics.Color;

public class Palette {

    public enum Type {
        KAZAKH_FLAG, WHITE_TO_BLACK_SCALE, BLACK_TO_WHITE_SCALE
    }

    private static final String LOG_TAG = Palette.class.getCanonicalName();

    public static byte[] getPalette(Type type, int precision) {
        byte[] palette = new byte[precision * 3];

        switch (type) {
            case KAZAKH_FLAG:
                palette = getHSVGradient(palette, 0x00ddff, 0xfeed00);
                break;
            case WHITE_TO_BLACK_SCALE:
                palette = getHSVGradient(palette, 0xffffff, 0x000000);
                break;
            case BLACK_TO_WHITE_SCALE:
                palette = getHSVGradient(palette, 0x000000, 0xffffff);
                break;

            default:
                break;
        }
        return palette;
    }

    private static byte[] getHSVGradient(byte[] palette, int startColor, int endColor) {
        float[] startHSV = new float[3];
        Color.colorToHSV(startColor, startHSV);
        float[] endHSV = new float[3];
        Color.colorToHSV(endColor, endHSV);

        float[] tmpHSV = new float[3];

        for (int i = 0; i < palette.length / 3; i++) {
            float p = ((float) i * 3f) / palette.length;
            for (int j = 0; j < startHSV.length; j++) {
                tmpHSV[j] = startHSV[j] * (1f - p) + endHSV[j] * p;
            }
            int c = Color.HSVToColor(tmpHSV);
            palette[i * 3 + 0] = (byte) Color.blue(c);
            palette[i * 3 + 1] = (byte) Color.green(c);
            palette[i * 3 + 2] = (byte) Color.red(c);
        }
        return palette;
    }

}
