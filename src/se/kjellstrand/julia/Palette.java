package se.kjellstrand.julia;

import android.content.Context;
import android.graphics.Color;

public class Palette {

    private static final String LOG_TAG = Palette.class.getCanonicalName();

    public static byte[] getPalette(Context context, String colors, int precision) {
        byte[] palette = new byte[precision * 3];

        if (context.getString(R.string.palette_kazakh_flagg).equals(colors)) {
            palette = getHSVGradient(palette, 0x00ddff, 0xfeed00);
        } else if (context.getString(R.string.palette_white_to_black).equals(colors)) {
            palette = getHSVGradient(palette, 0xffffff, 0x000000);
        } else if (context.getString(R.string.palette_black_to_white).equals(colors)) {
            palette = getHSVGradient(palette, 0x000000, 0xffffff);
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
