
package se.kjellstrand.julia;

import android.content.Context;

public class Theme {
    public BlendMode blendMode;

    public DrawMode drawMode;

    public boolean blackCenter;

    public int[] palette;

    public Theme(String themeName, Context context) {
        if (context.getResources().getString(R.string.theme_black_n_white, "").equals(themeName)) {
            blendMode = BlendMode.RGB;
            drawMode = DrawMode.ZEBRA;
            blackCenter = false;
            palette = new int[] {
                    0xffffff, 0x000000
            };
        } else if (context.getResources().getString(R.string.theme_bee_stripes, "").equals(themeName)) {
            blendMode = BlendMode.RGB;
            drawMode = DrawMode.ZEBRA_GRADIENT;
            blackCenter = false;
            palette = new int[] {
                    0xffff00, 0x000000
            };
        } else if (context.getResources().getString(R.string.theme_blue_hole, "").equals(themeName)) {
            blendMode = BlendMode.RGB;
            drawMode = DrawMode.GRADIENT;
            blackCenter = false;
            palette = new int[] {
                    0xffffff, 0x0000ff, 0x000022
            };
        } else if (context.getResources().getString(R.string.theme_blue_yellow, "").equals(themeName)) {
            blendMode = BlendMode.HSV;
            drawMode = DrawMode.ZEBRA_GRADIENT;
            blackCenter = false;
            palette = new int[] {
                    0x00ddff, 0xfeed00
            };
        } else if (context.getResources().getString(R.string.theme_bright_pink, "").equals(themeName)) {
            blendMode = BlendMode.RGB;
            drawMode = DrawMode.GRADIENT;
            blackCenter = false;
            palette = new int[] {
                    0xff0096, 0xffffff
            };
        } else if (context.getResources().getString(R.string.theme_pale_pink, "").equals(themeName)) {
            blendMode = BlendMode.RGB;
            drawMode = DrawMode.ZEBRA;
            blackCenter = false;
            palette = new int[] {
                    0xfadadd, 0xd4999e, 0xae636a, 0x873940
            };
        } else if (context.getResources().getString(R.string.theme_red_white, "").equals(themeName)) {
            blendMode = BlendMode.RGB;
            drawMode = DrawMode.ZEBRA;
            blackCenter = false;
            palette = new int[] {
                    0xffffff, 0xed2939
            };
        } else if (context.getResources().getString(R.string.theme_sunrise, "").equals(themeName)) {
            blendMode = BlendMode.RGB;
            drawMode = DrawMode.ZEBRA;
            blackCenter = false;
            palette = new int[] {
                    0x0068ff, 0xb4a2cc, 0xe0af22, 0xffb628, 0xfffc46, 0xffffaf
            };
        } else if (context.getResources().getString(R.string.theme_shiny_scales, "").equals(themeName)) {
            blendMode = BlendMode.RGB;
            drawMode = DrawMode.ZEBRA;
            blackCenter = false;
            palette = new int[] {
                    0xe0e0ba, 0xd1857e, 0x502065, 0x17b4c7, 0x7315b7, 0x000000
            };
        } else if (context.getResources().getString(R.string.theme_slime_green, "").equals(themeName)) {
            blendMode = BlendMode.RGB;
            drawMode = DrawMode.ZEBRA;
            blackCenter = false;
            palette = new int[] {
                    0x8efc00, 0x779d00
            };
        } else if (context.getResources().getString(R.string.theme_sunset, "").equals(themeName)) {
            blendMode = BlendMode.RGB;
            drawMode = DrawMode.ZEBRA;
            blackCenter = false;
            palette = new int[] {
                    0x000000, 0x422343, 0xaa5167, 0xf06659, 0xf19516, 0xfff312
            };
        } else if (context.getResources().getString(R.string.theme_zebra_stripes, "").equals(themeName)) {
            blendMode = BlendMode.RGB;
            drawMode = DrawMode.ZEBRA;
            blackCenter = false;
            palette = new int[] {
                    0xffffff, 0x000000, 0xffffff, 0x000000, 0xffffff, 0x000000
            };
        }

    }

}
