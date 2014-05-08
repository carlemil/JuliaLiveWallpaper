
package se.kjellstrand.julia;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

public class Palette {

    private static final String LOG_TAG = Palette.class.getCanonicalName();

    private static int[] colors;

    public static byte[] getPalette(Context context, String paletteString, String drawMode,
            String blendMode, int precision) {
        Log.d(LOG_TAG, "paletteString " + paletteString);
        String[] split = paletteString.split(",");
        colors = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            colors[i] = Long.decode(split[i].trim()).intValue();
        }

        int[] palette = new int[precision];

        if (context.getString(R.string.draw_mode_gradient).equals(drawMode)) {
            setGradient(context, palette, colors, blendMode);
        } else if (context.getString(R.string.draw_mode_zebra).equals(drawMode)) {
            setFlagBands(palette, colors);
        } else if (context.getString(R.string.draw_mode_zebra_gradient).equals(drawMode)) {
            setGradient(context, palette, colors, blendMode);
            zebraify(palette);
        }

        return byteify(palette);
    }

    private static void setGradient(Context context, int[] palette, int[] colors, String blendMode) {
        if (context.getString(R.string.blend_mode_hvs).equals(blendMode)) {
            if (colors.length == 2) {
                setHSVGradient(palette, colors[0], colors[1]);
            } else if (colors.length == 3) {
                setTrippleHSVGradient(palette, colors[0], colors[1], colors[2]);
            }
        } else {
            if (colors.length == 2) {
                setRGBGradient(palette, colors[0], colors[1]);
            } else if (colors.length == 3) {
                setTrippleRGBGradient(palette, colors[0], colors[1], colors[2]);
            }
        }
    }

    private static void zebraify(int[] palette) {
        for (int i = 0; i < palette.length; i++) {
            if (i % 2 == 1) {
                int c = palette[i];
                int r = ((c & 0xff0000) >> 1) & 0xff0000;
                int g = ((c & 0x00ff00) >> 1) & 0x00ff00;
                int b = ((c & 0x0000ff) >> 1) & 0x0000ff;
                palette[i] = r + g + b;
            }
        }
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

    private static int[] setTrippleRGBGradient(int[] palette, int startColor, int middleColor,
            int endColor) {
        int[] paletteStart = new int[palette.length / 2];
        setRGBGradient(paletteStart, startColor, middleColor);
        int[] paletteEnd = new int[palette.length - paletteStart.length + 1];
        setRGBGradient(paletteEnd, middleColor, endColor);
        System.arraycopy(paletteStart, 0, palette, 0, paletteStart.length);
        System.arraycopy(paletteEnd, 0, palette, paletteStart.length - 1, paletteEnd.length);
        return palette;
    }

    private static void setRGBGradient(int[] palette, int startColor, int endColor) {
        float[] startRGB = new float[3];
        startRGB = rgbToFloat3(startColor);
        float[] endRGB = new float[3];
        endRGB = rgbToFloat3(endColor);

        float[] tmpRGB = new float[3];

        for (int i = 0; i < palette.length; i++) {
            float p = ((float) i) / palette.length;
            for (int j = 0; j < 3; j++) {
                tmpRGB[j] = startRGB[j] * (1f - p) + endRGB[j] * p;
            }
            palette[i] = float3ToInt(tmpRGB);
        }
    }

    private static int float3ToInt(float[] f3) {
        return ((int) f3[0] & 255) + ((int) f3[1] << 8) + ((int) f3[2] << 16);
    }

    private static float[] rgbToFloat3(int color) {
        float[] f3 = new float[3];
        f3[0] = color & 255;
        f3[1] = (color >> 8) & 255;
        f3[2] = (color >> 16) & 255;
        return f3;
    }

}
