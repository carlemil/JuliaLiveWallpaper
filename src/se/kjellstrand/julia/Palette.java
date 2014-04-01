
package se.kjellstrand.julia;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

public class Palette {

    private static final String LOG_TAG = Palette.class.getCanonicalName();

    public static byte[] getPalette(Context context, String paletteName, int precision) {
        byte[] palette = new byte[precision * 3];

        int[] colors = null;

        if (context.getString(R.string.palette_kazakh_flagg).equals(paletteName)) {
            colors = new int[] {
                    0x00ddff, 0xfeed00
            };
        } else if (context.getString(R.string.palette_white_to_black).equals(paletteName)) {
            colors = new int[] {
                    0xffffff, 0x000000
            };
        } else if (context.getString(R.string.palette_black_to_white).equals(paletteName)) {
            colors = new int[] {
                    0x000000, 0xffffff
            };
        } else if (context.getString(R.string.palette_norway_flagg).equals(paletteName)) {
            colors = new int[] {
                    0x0000ff, 0xff0000, 0xffffff
            };
        }

        if (colors.length == 2) {
            setHSVGradient(palette, colors[0], colors[1]);
        } else if (colors.length == 3) {
            setTrippleHSVGradient(palette, colors[0], colors[1], colors[2]);
        }
        return palette;
    }

    private static byte[] setTrippleHSVGradient(byte[] palette, int startColor, int middleColor,
            int endColor) {
        byte[] paletteStart = new byte[((palette.length / 3) / 2) * 3];
        setHSVGradient(paletteStart, startColor, middleColor);
        byte[] paletteEnd = new byte[palette.length - paletteStart.length];
        setHSVGradient(paletteEnd, middleColor, endColor);

        Log.d("pal", "sl: " + paletteStart.length + " el: " + paletteEnd.length + " pl: "
                + palette.length);

        System.arraycopy(paletteStart, 0, palette, 0, paletteStart.length);

        System.arraycopy(paletteEnd, 0, palette, paletteStart.length, paletteEnd.length);

        return palette;
    }

    private static void setHSVGradient(byte[] palette, int startColor, int endColor) {
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
    }

}
