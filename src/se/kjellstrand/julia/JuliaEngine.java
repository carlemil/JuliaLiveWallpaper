
package se.kjellstrand.julia;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.RenderScript.Priority;

public class JuliaEngine {

    private final String TAG = JuliaEngine.class.getCanonicalName();

    private Bitmap mBitmap;

    private Matrix mMatrix;

    private ScriptC_julia mScript;

    private Allocation mInPixelsAllocation;

    private Allocation mOutPixelsAllocation;

    private int mPrecision = 24;

    public void init(Context context, int width, int height, float scale) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        mBitmap = Bitmap.createBitmap(width, height, conf);
        mBitmap.setHasAlpha(false);

        RenderScript rs = RenderScript.create(context, RenderScript.ContextType.DEBUG);
        rs.setPriority(Priority.LOW);

        mInPixelsAllocation = Allocation.createFromBitmap(rs, mBitmap,
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        mOutPixelsAllocation = Allocation.createFromBitmap(rs, mBitmap,
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

        mScript = new ScriptC_julia(rs, context.getResources(), R.raw.julia);

        mScript.set_width(width);
        mScript.set_height(height);

        mScript.set_precision(mPrecision);
        mScript.set_scale(scale);

        byte[] d = getPalette();

        Element type = Element.U8(rs);
        Allocation colorAllocation = Allocation.createSized(rs, type, mPrecision * 3);
        mScript.bind_color(colorAllocation);

        colorAllocation.copy1DRangeFrom(0, mPrecision * 3, d);
        // 2DRangeFrom(0, mPrecision, d);

        // Allocation color = mScript.get_color();
        // color.copyFrom(d);

        mMatrix = new Matrix();
        mMatrix.postScale(scale, scale);
    }

    private byte[] getPalette() {
        byte[] d = new byte[mPrecision * 3];

        for (int i = 0; i < mPrecision; i++) {
            d[i * 3 + 0] = (byte) (((Math.cos(i / (double) mPrecision * Math.PI) + 1d) / 2d) * 255);
            d[i * 3 + 1] = (byte) (((Math.sin(i / (double) mPrecision * Math.PI / 3) + 1d) / 2d) * 255);
            d[i * 3 + 2] = (byte) (((Math.sin(i / (double) mPrecision * Math.PI) + 1d) / 2d) * 255);
        }
        return d;
    }

    public Bitmap renderJulia(double x, double y) {
        mScript.set_cx((float) x);
        mScript.set_cy((float) y);
        mScript.forEach_root(mInPixelsAllocation, mOutPixelsAllocation);
        mOutPixelsAllocation.copyTo(mBitmap);
        return mBitmap;
    }

    public Matrix getScaleMatrix() {
        return mMatrix;
    }

    public int getPrecision() {
        return mPrecision;
    }

    public void setPrecision(int mPrecision) {
        this.mPrecision = mPrecision;
        mScript.set_precision(mPrecision);
    }

    public void destroy() {
        mScript.destroy();
    }
}
