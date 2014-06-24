
package se.kjellstrand.julia;

import android.content.Context;

public class Theme {
    public BlendMode blendMode;

    public DrawMode drawMode;

    public boolean blackCenter;

    public int[] palette;

    public Theme(String themeName, Context context) {
        if(context.getResources().getString(R.string.theme_black_n_white, "").equals(themeName)){
            blendMode = BlendMode.HSV;
            drawMode = DrawMode.ZEBRA;
            blackCenter = false;
            palette = new int[]{0xffffff, 0x000000, 0xffffff};
        }


    }

}
