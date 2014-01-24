package se.kjellstrand.julia;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.RenderScript.Priority;

import com.android.rs.levels.ScriptC_levels;

public class JuliaEngine {

    private Bitmap mBitmap;

    private Matrix mMatrix;

    private RenderScript mRS;
    private ScriptC_levels mScript;

    private Allocation mInPixelsAllocation;
    private Allocation mOutPixelsAllocation;

    private int mPrecision = 16;

    public void init(Context context, int mWidth, int mHeight, float scale) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, conf);
        mBitmap.setHasAlpha(false);

        mRS = RenderScript.create(context, RenderScript.ContextType.DEBUG);
        mRS.setPriority(Priority.LOW);

        mInPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        mOutPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);

        mScript = new ScriptC_levels(mRS, context.getResources(), R.raw.levels);

        mScript.set_width(mWidth);
        mScript.set_height(mHeight);

        mScript.set_precision(mPrecision);
        mScript.set_scale(scale);

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
    }
}
