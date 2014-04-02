
package se.kjellstrand.julia;

import android.content.Context;
import android.graphics.Color;

public class Palette {

    private static final String LOG_TAG = Palette.class.getCanonicalName();

    public static byte[] getPalette(Context context, String paletteName, String drawMode,
            int precision) {
        int[] palette = new int[precision];

        int[] colors = null;

        if (context.getString(R.string.palette_white_to_black).equals(paletteName)) {
            colors = new int[] {
                    0xffffff, 0x000000
            };
        } else if (context.getString(R.string.palette_black_to_white).equals(paletteName)) {
            colors = new int[] {
                    0x000000, 0xffffff
            };
        } else if (context.getString(R.string.palette_norway).equals(paletteName)) {
            colors = new int[] {
                    0x002868, 0xffffff, 0xef2b2d
            };
        } else if (context.getString(R.string.palette_sweden).equals(paletteName)) {
            colors = new int[] {
                    0x005293, 0xfecb00
            };
        } else if (context.getString(R.string.palette_kazakhstan).equals(paletteName)) {
            colors = new int[] {
                    0x00ddff, 0xfeed00
            };
        } else if (context.getString(R.string.palette_india).equals(paletteName)) {
            colors = new int[] {
                    0xff9933, 0xffffff, 0x128807
            };
        } else if (context.getString(R.string.palette_china).equals(paletteName)) {
            colors = new int[] {
                    0xde2910, 0xffde00
            };
        } else if (context.getString(R.string.palette_russia).equals(paletteName)) {
            colors = new int[] {
                    0x0039a6, 0xffffff, 0xd52b1e
            };
        } else if (context.getString(R.string.palette_britain).equals(paletteName)) {
            colors = new int[] {
                    0xcf142b, 0xffffff, 0x00247d
            };
        } else {
            // Default colors: black - white - black
            colors = new int[] {
                    0x000000, 0xffffff, 0x000000
            };
        }

        if (context.getString(R.string.draw_mode_smooth_blend).equals(drawMode)) {
            if (colors.length == 2) {
                setHSVGradient(palette, colors[0], colors[1]);
            } else if (colors.length == 3) {
                setTrippleHSVGradient(palette, colors[0], colors[1], colors[2]);
            }
        } else if (context.getString(R.string.draw_mode_flag_bands).equals(drawMode)) {
            setFlagBands(palette, colors);
        } else if (context.getString(R.string.draw_mode_zebra_blend).equals(drawMode)) {
            if (colors.length == 2) {
                setHSVGradient(palette, colors[0], colors[1]);
            } else if (colors.length == 3) {
                setTrippleHSVGradient(palette, colors[0], colors[1], colors[2]);
            }
            zebraify(palette);
        }
        return byteify(palette);
    }

    private static void zebraify(int[] palette) {

    }

    private static byte[] byteify(int[] palette) {
        byte[] bytePalette = new byte[palette.length * 3];
        for (int i = 0; i < palette.length; i++) {
            bytePalette[i * 3 + 2] = (byte) ((palette[i] >> 16) & 0xff);
            bytePalette[i * 3 + 1] = (byte) ((palette[i] >> 8) & 0xff);
            bytePalette[i * 3 + 0] = (byte) ((palette[i] >> 0) & 0xff);
        }
        return bytePalette;
    }

    private static void setFlagBands(int[] palette, int[] colors) {
        for (int i = 0; i < palette.length; i++) {
            palette[i] = colors[i % colors.length];
        }
    }

    private static int[] setTrippleHSVGradient(int[] palette, int startColor, int middleColor,
            int endColor) {
        int[] paletteStart = new int[palette.length / 2];
        setHSVGradient(paletteStart, startColor, middleColor);
        int[] paletteEnd = new int[palette.length - paletteStart.length + 1];
        setHSVGradient(paletteEnd, middleColor, endColor);
        System.arraycopy(paletteStart, 0, palette, 0, paletteStart.length);
        System.arraycopy(paletteEnd, 0, palette, paletteStart.length - 1, paletteEnd.length);
        return palette;
    }

    private static void setHSVGradient(int[] palette, int startColor, int endColor) {
        float[] startHSV = new float[3];
        Color.colorToHSV(startColor, startHSV);
        float[] endHSV = new float[3];
        Color.colorToHSV(endColor, endHSV);

        float[] tmpHSV = new float[3];

        for (int i = 0; i < palette.length; i++) {
            float p = ((float) i) / palette.length;
            for (int j = 0; j < 3; j++) {
                tmpHSV[j] = startHSV[j] * (1f - p) + endHSV[j] * p;
            }
            palette[i] = Color.HSVToColor(tmpHSV);

        }
    }

}
