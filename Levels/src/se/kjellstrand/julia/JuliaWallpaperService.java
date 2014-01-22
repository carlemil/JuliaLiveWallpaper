package se.kjellstrand.julia;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.renderscript.Allocation;
import android.renderscript.Matrix3f;
import android.renderscript.RenderScript;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import com.android.rs.levels.ScriptC_levels;

public class JuliaWallpaperService extends WallpaperService {

    // private static final float SCALE = 1;
    private final String TAG = "Img";
    private Bitmap mBitmapIn;
    private Bitmap mBitmapOut;

    Matrix3f satMatrix = new Matrix3f();
    float mInWMinInB;
    float mOutWMinOutB;

    float mScale = 2f;

    private RenderScript mRS;
    private Allocation mInPixelsAllocation;
    private Allocation mOutPixelsAllocation;
    private ScriptC_levels mScript;
    private int mWidth;
    private int mHeight;

    @Override
    public Engine onCreateEngine() {
        return new DemoEngine();
    }

    class DemoEngine extends Engine {

        private Matrix mMatrix;

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);

            // mRenderScriptGL.setPriority(RenderScript.Priority.LOW);

            Rect rect = holder.getSurfaceFrame();
            mHeight = rect.height();
            mWidth = rect.width();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            mWidth = (int) (width / mScale);
            mHeight = (int) (height / mScale);
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            mBitmapIn = Bitmap.createBitmap(mWidth, mHeight, conf);
            mBitmapOut = Bitmap.createBitmap(mWidth, mHeight, conf);
            mBitmapOut.setHasAlpha(false);

            mRS = RenderScript.create(JuliaWallpaperService.this.getBaseContext(), RenderScript.ContextType.DEBUG);
            mInPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmapIn,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmapOut,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);

            mScript = new ScriptC_levels(mRS, getResources(), R.raw.levels);

            mScript.set_width(mWidth);
            mScript.set_height(mHeight);

            mScript.set_precision(16);
            mScript.set_scale(mScale);

            mMatrix = new Matrix();
            mMatrix.postScale(mScale, mScale);

            draw(0.5f);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep,
                float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
            draw(xOffset);
        }

        private void draw(float xOffset) {

            long startTime = System.currentTimeMillis();

            renderJulia(0.5f - xOffset / 5, 0.2f);

            Log.d(TAG, "Rendertime: " + (System.currentTimeMillis() - startTime));
            Canvas c = null;
            SurfaceHolder holder = getSurfaceHolder();
            try {
                c = holder.lockCanvas();
                if (c != null) {

                    c.drawBitmap(mBitmapOut, mMatrix, null);
                }
            } finally {
                if (c != null) {
                    holder.unlockCanvasAndPost(c);
                }
            }
        }

        private void renderJulia(float cx, float cy) {
            mScript.set_cx(cx);
            mScript.set_cy(cy);
            mScript.forEach_root(mInPixelsAllocation, mOutPixelsAllocation);
            mOutPixelsAllocation.copyTo(mBitmapOut);
        }

    }
}
