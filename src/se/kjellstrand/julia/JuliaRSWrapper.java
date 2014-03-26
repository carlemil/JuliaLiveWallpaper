package se.kjellstrand.julia;

import se.kjellstrand.julia.Palette.Type;
import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.RenderScript.Priority;

public class JuliaRSWrapper {

    private final String TAG = JuliaRSWrapper.class.getCanonicalName();

    private Bitmap bitmap;

    private ScriptC_julia script;

    private Allocation pxelsAllocation;

    private int scaledWidth;

    private int scaledHeight;

    private int precision = 34;

    private final float scale;

    public JuliaRSWrapper(Context context, int width, int height, float scale) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;

        this.scale = scale;

        this.scaledWidth = (int) (width / scale);
        this.scaledHeight = (int) (height / scale);

        bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, conf);
        bitmap.setHasAlpha(false);

        RenderScript rs = RenderScript.create(context, RenderScript.ContextType.DEBUG);
        rs.setPriority(Priority.LOW);

        pxelsAllocation = Allocation.createFromBitmap(rs, bitmap,
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

        script = new ScriptC_julia(rs, context.getResources(), R.raw.julia);

        script.set_width(scaledWidth);
        script.set_height(scaledHeight);

        script.set_precision(precision);

        byte[] d = Palette.getPalette(Type.KAZAKH_FLAG, precision);

        Element type = Element.U8(rs);
        Allocation colorAllocation = Allocation.createSized(rs, type, precision * 3);
        script.bind_color(colorAllocation);

        colorAllocation.copy1DRangeFrom(0, precision * 3, d);
    }

    public Bitmap renderJulia(double x, double y) {
        script.set_cx((float) x);
        script.set_cy((float) y);
        script.forEach_root(pxelsAllocation, pxelsAllocation);
        pxelsAllocation.copyTo(bitmap);
        return bitmap;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int mPrecision) {
        this.precision = mPrecision;
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
