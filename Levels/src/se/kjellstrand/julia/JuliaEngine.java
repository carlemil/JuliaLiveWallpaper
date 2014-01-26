package se.kjellstrand.julia;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.RenderScript.Priority;
import android.renderscript.Short4;
import android.util.Log;

public class JuliaEngine {

    private final String TAG = JuliaEngine.class.getCanonicalName();

    private Bitmap mBitmap;

    private Matrix mMatrix;

    private ScriptC_julia mScript;

    private Allocation mInPixelsAllocation;
    private Allocation mOutPixelsAllocation;

    private int mPrecision = 32;

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

        mScript = new ScriptC_julia(rs, context.getResources(), R.raw.julia);

        mScript.set_width(width);
        mScript.set_height(height);

        mScript.set_precision(mPrecision);
        mScript.set_scale(scale);



        ScriptField_Palette p = new ScriptField_Palette(rs, mPrecision);
        for (int i = 0; i < mPrecision; i++) {
            Short4 v = new Short4();
            v.w = (short) 55;
            v.x = (short) 55;// ((mPrecision / ((float) i)) * 255);
            v.y = (short) 55;// (255 - (mPrecision / ((float) i)) * 255);
            v.z = (short) 55;
            Log.d(TAG, "i: " + i);
            p.set_c(i, v, true);
        }

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
