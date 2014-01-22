package se.kjellstrand.julia;

import android.graphics.Bitmap;
import android.graphics.Canvas;
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

    // private ImageView mDisplayView;

    Matrix3f satMatrix = new Matrix3f();
    float mInWMinInB;
    float mOutWMinOutB;
    // float mOverInWMinInB;

    private RenderScript mRS;
    private Allocation mInPixelsAllocation;
    private Allocation mOutPixelsAllocation;
    private ScriptC_levels mScript;
    // private int i = 0;
    private int mWidth;
    private int mHeight;

    @Override
    public Engine onCreateEngine() {
        return new DemoEngine();
    }

    class DemoEngine extends Engine {

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);

            // mRenderScriptGL.setPriority(RenderScript.Priority.LOW);

            Rect rect = holder.getSurfaceFrame();
            mHeight = rect.height();
            mWidth = rect.width();

            // Log.d(TAG, "### h: " + mHeight + " w: " + mWidth);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            // Log.d(TAG, "### onSurfaceChanged h: " + mHeight + " w: " +
            // mWidth);

            mWidth = width;
            mHeight = height;
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;

            mBitmapIn = Bitmap.createBitmap(mWidth, mHeight, conf);
            mBitmapOut = Bitmap.createBitmap(mWidth, mHeight, conf);

            // Log.d(TAG, "### mBitmapIn h: " + mBitmapIn.getHeight() + " w: " +
            // mBitmapIn.getWidth());

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
            mScript.set_zoom(1f);

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
            // Log.d(TAG, "### On offset change! " + xOffset + " : " + yOffset);

            draw(xOffset);
        }

        private void draw(float xOffset) {

            /* for (int i = 1; i < 1080; i++) { int color = 0xffffff00;
             * mBitmapOut.setPixel(i, i, color); mBitmapOut.setPixel(i, i + 1,
             * color); mBitmapOut.setPixel(i, i - 1, color); } */

            long startTime = System.currentTimeMillis();

            renderJulia(xOffset, 0.5f);

            Log.d(TAG, "Rendertime: " + (System.currentTimeMillis() - startTime));

            Canvas c = null;
            SurfaceHolder holder = getSurfaceHolder();
            try {
                c = holder.lockCanvas();
                // Log.d(TAG, "canvas: " + c.getHeight() + "  " + c.getWidth());
                // Log.d(TAG, "mBitmapOut: " + mBitmapOut.getHeight() + "  " +
                // mBitmapOut.getWidth());
                // Log.d(TAG, "mBitmapOu 500500t: " + mBitmapOut.getPixel(500,
                // 500));

                if (c != null) {

                    c.drawBitmap(mBitmapOut, 0f, 0f, null);
                }
            } finally {
                if (c != null) {
                    Log.d(TAG, "unlock");
                    holder.unlockCanvasAndPost(c);
                }
            }
        }

        private void renderJulia(float cx, float cy) {
            mScript.set_cx(cx);
            mScript.set_cy(cy);
            mScript.forEach_root(mInPixelsAllocation, mOutPixelsAllocation);
            mOutPixelsAllocation.copyTo(mBitmapOut);


            // Log.d(TAG, "alloc 500500t: " + mBitmapOut.getPixel(500, 500));
            // Log.d(TAG, "alloc 5001080 t: " + mBitmapOut.getPixel(100, 1081));

            // Log.d(TAG, "in alloc size :" +
            // mInPixelsAllocation.getBytesSize());
            // Log.d(TAG, "out alloc size :" +
            // mOutPixelsAllocation.getSurface());
        }

    }
}
