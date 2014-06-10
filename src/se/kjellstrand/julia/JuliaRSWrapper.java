
package se.kjellstrand.julia;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.RenderScript.Priority;

public class JuliaRSWrapper {

    private static final String LOG_TAG = JuliaRSWrapper.class.getCanonicalName();

    private static final String DEFAULT_PALETTE_COLORS = "0x000000, 0xffffff, 0x000000";

    private Bitmap bitmap;

    private ScriptC_julia script;

    private Allocation pxelsAllocation;

    private int scaledWidth;

    private int scaledHeight;

    private final float scale;

    private RenderScript rs;

    public JuliaRSWrapper(Context context, int width, int height, float scale) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;

        this.scale = scale;

        this.scaledWidth = (int) (width / scale);
        this.scaledHeight = (int) (height / scale);

        bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, conf);
        bitmap.setHasAlpha(false);

        rs = RenderScript.create(context, RenderScript.ContextType.DEBUG);
        rs.setPriority(Priority.LOW);

        pxelsAllocation = Allocation.createFromBitmap(rs, bitmap,
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

        script = new ScriptC_julia(rs, context.getResources(), R.raw.julia);

        script.set_width(scaledWidth);
        script.set_height(scaledHeight);

        script.set_zoom(JuliaWallpaperService.INITIAL_ZOOM);

        script.set_precision(JuliaWallpaperService.INITIAL_PRECISION);

        setPalette(context);
    }

    public void setPalette(Context context){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        String colorsKey = context.getResources().getString(R.string.pref_palette_key);
        String palette = sharedPreferences.getString(colorsKey, DEFAULT_PALETTE_COLORS);

        String drawModeKey = context.getResources().getString(R.string.pref_draw_mode_key);
        String drawMode = sharedPreferences.getString(drawModeKey, null);

        String blendModeKey = context.getResources().getString(R.string.pref_blend_mode_key);
        String blendMode = sharedPreferences.getString(blendModeKey, null);

        String brightnessKey = context.getResources().getString(R.string.pref_brightness_key);
        int brightness = sharedPreferences.getInt(brightnessKey, 80);

        String blackCenterKey = context.getResources().getString(R.string.pref_black_center_key);
        boolean blackCenter = sharedPreferences.getBoolean(blackCenterKey, false);

        String reversePaletteKey = context.getResources().getString(R.string.pref_reverse_palette_key);
        boolean reversePalette = sharedPreferences.getBoolean(reversePaletteKey, false);

        byte[] d = Palette.getPalette(context, palette, drawMode, blendMode, blackCenter,
                reversePalette, JuliaWallpaperService.INITIAL_PRECISION, brightness);

        Element type = Element.U8(rs);
        Allocation colorAllocation = Allocation.createSized(rs, type,
                JuliaWallpaperService.INITIAL_PRECISION * 3);
        script.bind_color(colorAllocation);

        colorAllocation.copy1DRangeFrom(0, JuliaWallpaperService.INITIAL_PRECISION * 3, d);
    }

    public Bitmap renderJulia(double x, double y) {
        script.set_cx((float) x);
        script.set_cy((float) y);
        script.forEach_root(pxelsAllocation, pxelsAllocation);
        pxelsAllocation.copyTo(bitmap);
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
