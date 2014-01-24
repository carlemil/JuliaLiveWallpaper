package se.kjellstrand.julia;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.RenderScript.Priority;

import com.android.rs.levels.ScriptC_levels;
import com.android.rs.levels.ScriptField_Point;

public class JuliaEngine {

    private Bitmap mBitmap;

    private Matrix mMatrix;

    private ScriptC_levels mScript;

    private Allocation mInPixelsAllocation;
    private Allocation mOutPixelsAllocation;

    private int mPrecision = 16;

    public void init(Context context, int width, int height, float scale) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        mBitmap = Bitmap.createBitmap(width, height, conf);
        mBitmap.setHasAlpha(false);

        RenderScript rs = RenderScript.create(context, RenderScript.ContextType.DEBUG);
        rs.setPriority(Priority.LOW);

        mInPixelsAllocation = Allocation.createFromBitmap(rs, mBitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        mOutPixelsAllocation = Allocation.createFromBitmap(rs, mBitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);

        mScript = new ScriptC_levels(rs, context.getResources(), R.raw.levels);

        mScript.set_width(width);
        mScript.set_height(height);

        mScript.set_precision(mPrecision);
        mScript.set_scale(scale);

        // mScript.set

        ScriptField_Point point = null;

        mMatrix = new Matrix();
        mMatrix.postScale(scale, scale);
    }

    public Bitmap renderJulia(float cx, float cy) {
        mScript.set_cx(cx);
        mScript.set_cy(cy);
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
