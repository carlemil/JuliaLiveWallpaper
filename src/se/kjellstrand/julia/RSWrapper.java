
package se.kjellstrand.julia;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.RenderScript.Priority;

public class RSWrapper {

    private static final String LOG_TAG = RSWrapper.class.getCanonicalName();

    private Bitmap bitmap;

    private ScriptC_julia script;

    private Allocation allocation;

    private int scaledWidth;

    private int scaledHeight;

    private final float scale;

    private RenderScript rs;

    public RSWrapper(Context context, int width, int height, float scale) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;

        this.scale = scale;

        this.scaledWidth = (int) (width / scale);
        this.scaledHeight = (int) (height / scale);

        bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, conf);
        bitmap.setHasAlpha(false);

        rs = RenderScript.create(context, RenderScript.ContextType.DEBUG);
        rs.setPriority(Priority.LOW);

        allocation = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

        script = new ScriptC_julia(rs, context.getResources(), R.raw.julia);

        script.set_width(scaledWidth);
        script.set_height(scaledHeight);

        script.set_zoom(JuliaWallpaperService.INITIAL_ZOOM);

        setPalette(context);
    }

    public void setPalette(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String themesKey = context.getResources().getString(R.string.pref_theme_key);
        String themeName = sharedPreferences.getString(themesKey, context.getResources().getString(R.string.theme_black_n_white));
        Theme theme = new Theme(themeName, context);

        String brightnessKey = context.getResources().getString(R.string.pref_brightness_key);
        int brightness = sharedPreferences.getInt(brightnessKey, 70);

        script.set_precision(theme.precission);

        byte[] d = Palette.getPalette(context, theme, brightness);

        Element type = Element.U8(rs);
        Allocation colorAllocation = Allocation.createSized(rs, type, theme.precission * 3);
        script.bind_color(colorAllocation);

        colorAllocation.copy1DRangeFrom(0, theme.precission * 3, d);
    }

    public Bitmap renderJulia(double x, double y) {
        script.set_cx((float) x);
        script.set_cy((float) y);
        script.forEach_root(allocation, allocation);
        allocation.copyTo(bitmap);
        return bitmap;
    }

    public float getZoom() {
        return script.get_zoom();
    }

    public void setZoom(float zoom) {
        script.set_zoom(zoom);
    }

    public int getPrecision() {
        return script.get_precision();
    }

    public void setPrecision(int mPrecision) {
        script.set_precision(mPrecision);
    }

    public void destroy() {
        script.destroy();
    }

    public int getScaledWidth() {
        return scaledWidth;
    }

    public int getScaledHeight() {
        return scaledHeight;
    }

    public float getScale() {
        return scale;
    }

}
