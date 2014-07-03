
package se.kjellstrand.julia;

import se.kjellstrand.julia.prefs.BlendMode;
import se.kjellstrand.julia.prefs.CenterMode;
import se.kjellstrand.julia.prefs.DrawMode;
import android.content.Context;

public class Theme {
    public BlendMode blendMode = BlendMode.RGB;

    public DrawMode drawMode = DrawMode.ZEBRA;

    public CenterMode centerMode = CenterMode.DEFAULT;

    public int[] palette = new int[] {
            0xffffff, 0x000000
    };

    public int precission = 64;

    public Theme(String themeName, Context context) {
        if (context.getResources().getString(R.string.theme_black_n_white, "").equals(themeName)) {
            drawMode = DrawMode.GRADIENT;
        } else if (context.getResources().getString(R.string.theme_bee_stripes, "").equals(themeName)) {
            drawMode = DrawMode.ZEBRA_GRADIENT;
            centerMode = CenterMode.BLACK;
            palette = new int[] {
                    0xffff00, 0x222200
            };
            precission = 63;
        } else if (context.getResources().getString(R.string.theme_blue_hole, "").equals(themeName)) {
            drawMode = DrawMode.GRADIENT;
            palette = new int[] {
                    0xffffff, 0x0000ff, 0x000022
            };
        } else if (context.getResources().getString(R.string.theme_blue_yellow, "").equals(themeName)) {
            blendMode = BlendMode.HSV;
            drawMode = DrawMode.ZEBRA_GRADIENT;
            palette = new int[] {
                    0x00ddff, 0xfeed00
            };
            precission = 63;
        } else if (context.getResources().getString(R.string.theme_bright_pink, "").equals(themeName)) {
            drawMode = DrawMode.GRADIENT;
            palette = new int[] {
                    0xff0096, 0xffffff
            };
        } else if (context.getResources().getString(R.string.theme_pale_pink, "").equals(themeName)) {
            palette = new int[] {
                    0xfadadd, 0xd4999e, 0xae636a, 0x873940, 0xae636a, 0xd4999e
            };
            precission = 61;
        } else if (context.getResources().getString(R.string.theme_red_white, "").equals(themeName)) {
            palette = new int[] {
                    0xffffff, 0xed2939
            };
            precission = 63;
        } else if (context.getResources().getString(R.string.theme_sunrise, "").equals(themeName)) {
            drawMode = DrawMode.GRADIENT;
            palette = new int[] {
                    0x0068ff, 0xb4a2cc, 0xe0af22, 0xffb628, 0xfffc46, 0xffffaf
            };
        } else if (context.getResources().getString(R.string.theme_shiny_scales, "").equals(themeName)) {
            drawMode = DrawMode.ZEBRA_GRADIENT;
            centerMode = CenterMode.BLACK;
            palette = new int[] {
                    0x00f0ba, 0xd1857e, 0x502065, 0x17b4c7, 0x7315b7, 0x000000
            };
        } else if (context.getResources().getString(R.string.theme_slime_green, "").equals(themeName)) {
            blendMode = BlendMode.HSV;
            drawMode = DrawMode.GRADIENT;
            palette = new int[] {
                    0x8efc00, 0x779d00
            };
        } else if (context.getResources().getString(R.string.theme_sunset, "").equals(themeName)) {
            drawMode = DrawMode.GRADIENT;
            palette = new int[] {
                    0x000000, 0x422343, 0xaa5167, 0xf06659, 0xf19516, 0xfff312
            };
        } else if (context.getResources().getString(R.string.theme_milky_way, "").equals(themeName)) {
            drawMode = DrawMode.GRADIENT;
            palette = new int[] {
                    0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0xffffff
            };

        } else if (context.getResources().getString(R.string.theme_teal, "").equals(themeName)) {
            drawMode = DrawMode.GRADIENT;
            blendMode = BlendMode.HSV;
            palette = new int[] {
                    0x00a0a0, 0x002020
            };
        } else if (context.getResources().getString(R.string.theme_terracotta, "").equals(themeName)) {
            drawMode = DrawMode.GRADIENT;
            palette = new int[] {
                    0x6f1300, 0xe2725b, 0xffa08d
            };
        } else if (context.getResources().getString(R.string.theme_brick_wall, "").equals(themeName)) {
            drawMode = DrawMode.ZEBRA;
            palette = new int[] {
                    0x841f27, 0xaa3311, 0xa94140, 0x888888
            };
        } else if (context.getResources().getString(R.string.theme_chrome, "").equals(themeName)) {
            drawMode = DrawMode.GRADIENT;
            palette = new int[] {
                    0xcccccc, 0x999999, 0xbbbbbb, 0xeeeeee, 0xaaaaaa, 0xdddddd
            };
        } else if (context.getResources().getString(R.string.theme_dessert, "").equals(themeName)) {
            drawMode = DrawMode.ZEBRA_GRADIENT;
            palette = new int[] {
                    0xa28d68, 0xd5ccbb, 0xa28d68, 0xeae6dd
            };
        } else if (context.getResources().getString(R.string.theme_feeling_blue, "").equals(themeName)) {
            drawMode = DrawMode.GRADIENT;
            palette = new int[] {
                    0x000080, 0x000040, 0x000020, 0x000010, 0x0000f0, 0x000000
            };
        } else if (context.getResources().getString(R.string.theme_forest, "").equals(themeName)) {
            drawMode = DrawMode.ZEBRA_GRADIENT;
            blendMode = BlendMode.HSV;
            centerMode = CenterMode.BLACK;
            palette = new int[] {
                    0x266A2E, 0x4F4F2F, 0x228b22, 0x855E42, 0x347235, 0x8B864E, 0x254117, 0x8B7355
            };
        } else if (context.getResources().getString(R.string.theme_heavy_rain, "").equals(themeName)) {
            drawMode = DrawMode.ZEBRA_GRADIENT;
            palette = new int[] {
                    0x000000, 0x0055bb, 0x000000
            };
        } else if (context.getResources().getString(R.string.theme_zebra_stripes, "").equals(themeName)) {
        }

    }

}
