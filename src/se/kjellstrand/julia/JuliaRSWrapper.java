package se.kjellstrand.julia;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.RenderScript.Priority;

public class JuliaRSWrapper {

    private static final String LOG_TAG = JuliaRSWrapper.class.getCanonicalName();

    private final String TAG = JuliaRSWrapper.class.getCanonicalName();

    private static final int INITIAL_PRECISION = 28 + 5;

    private static final float INITIAL_ZOOM = 1.2f;

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

        script.set_zoom(INITIAL_ZOOM);

        script.set_precision(INITIAL_PRECISION);

        String colors = context.getString(R.string.palette_black_to_white);
        String drawMode = context.getString(R.string.draw_mode_gradient);
        setPalette(context, colors, drawMode);
    }

    public void setPalette(Context context, String palette, String drawMode) {
        byte[] d = Palette.getPalette(context, palette, drawMode, INITIAL_PRECISION);

        Element type = Element.U8(rs);
        Allocation colorAllocation = Allocation.createSized(rs, type, INITIAL_PRECISION * 3);
        script.bind_color(colorAllocation);

        colorAllocation.copy1DRangeFrom(0, INITIAL_PRECISION * 3, d);
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
