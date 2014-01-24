package se.kjellstrand.julia;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.RenderScript.Priority;

import com.android.rs.levels.ScriptC_levels;
<<<<<<< HEAD
import com.android.rs.levels.ScriptField_Point;
=======
>>>>>>> fb1c661c2d6c4ecab8bc3fe4580d0c6247fb8674

public class JuliaEngine {

    private Bitmap mBitmap;

    private Matrix mMatrix;

<<<<<<< HEAD
=======
    private RenderScript mRS;
>>>>>>> fb1c661c2d6c4ecab8bc3fe4580d0c6247fb8674
    private ScriptC_levels mScript;

    private Allocation mInPixelsAllocation;
    private Allocation mOutPixelsAllocation;

    private int mPrecision = 16;

<<<<<<< HEAD
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
=======
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
>>>>>>> fb1c661c2d6c4ecab8bc3fe4580d0c6247fb8674

        mScript.set_precision(mPrecision);
        mScript.set_scale(scale);

<<<<<<< HEAD
        // mScript.set

        ScriptField_Point point = null;

=======
>>>>>>> fb1c661c2d6c4ecab8bc3fe4580d0c6247fb8674
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
<<<<<<< HEAD
        mScript.set_precision(mPrecision);
    }

    public void destroy() {
        mScript.destroy();
=======
>>>>>>> fb1c661c2d6c4ecab8bc3fe4580d0c6247fb8674
    }
}
