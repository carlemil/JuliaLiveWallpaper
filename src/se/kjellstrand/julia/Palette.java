
package se.kjellstrand.julia;

import android.content.Context;
import android.graphics.Color;

public class Palette {

    public static byte[] getPalette(Context context, Theme theme, int precision, int brightness) {

        adjustBrightness(theme.palette, brightness);

        int[] palette = new int[precision];

        if (theme.drawMode == DrawMode.ZEBRA) {
            setFlagBands(palette, theme.palette);
        } else if (theme.drawMode == DrawMode.ZEBRA_GRADIENT) {
            setGradient(context, palette, theme.palette, theme.blendMode);
            zebraify(palette);
        } else if (theme.drawMode == DrawMode.GRADIENT) {
            setGradient(context, palette, theme.palette, theme.blendMode);
        }

        if (theme.blackCenter) {
            palette[palette.length - 1] = 0;
        }

        return byteify(palette);
    }

    private static void adjustBrightness(int[] colors, int brightness) {
        for (int i = 0; i < colors.length; i++) {
            float[] f3 = rgbToFloat3(colors[i]);
            for (int j = 0; j < 3; j++) {
                f3[j] = f3[j] * (brightness / 100f);
            }
            colors[i] = float3ToInt(f3);
        }
    }

    private static void setGradient(Context context, int[] palette, int[] colors, BlendMode blendMode) {
        for (int i = 1; i < colors.length; i++) {
            int pl = palette.length;
            int cl = colors.length - 1;
            int i1 = Math.round((pl / (float) (cl)) * (i - 1));
            int i2 = Math.round((pl / (float) (cl)) * i);
            int c1 = colors[i - 1];
            int c2 = colors[i];
            int[] p = new int[i2 - i1];
            setGradient(context, p, c1, c2, blendMode);
            System.arraycopy(p, 0, palette, i1, p.length);
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

    private static void setGradient(Context context, int[] palette, int startColor, int endColor, BlendMode blendMode) {
        float[] start = new float[3];
        float[] end = new float[3];
        float[] tmp = new float[3];

        if (blendMode == BlendMode.HSV) {
            Color.colorToHSV(startColor, start);
            Color.colorToHSV(endColor, end);
        } else {
            start = rgbToFloat3(startColor);
            end = rgbToFloat3(endColor);
        }

        for (int i = 0; i < palette.length; i++) {
            float p = ((float) i) / palette.length;
            for (int j = 0; j < 3; j++) {
                tmp[j] = start[j] * (1f - p) + end[j] * p;
            }
            if (blendMode == BlendMode.HSV) {
                palette[i] = Color.HSVToColor(tmp);
            } else {
                palette[i] = float3ToInt(tmp);
            }
        }
    }

    private static int float3ToInt(float[] f3) {
        return (int) f3[0] + ((int) f3[1] << 8) + ((int) f3[2] << 16);
    }

    private static float[] rgbToFloat3(int color) {
        float[] f3 = new float[3];
        f3[0] = color & 255;
        f3[1] = (color >> 8) & 255;
        f3[2] = (color >> 16) & 255;
        return f3;
    }

}
