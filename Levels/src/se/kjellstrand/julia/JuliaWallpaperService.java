package se.kjellstrand.julia;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public class JuliaWallpaperService extends WallpaperService {

    private final String TAG = JuliaWallpaperService.class.getCanonicalName();

    float mScale = 2f;

    private int mWidth;
    private int mHeight;

    @Override
    public Engine onCreateEngine() {
        return new DemoEngine();
    }

    class DemoEngine extends Engine {

        JuliaEngine mJuliaRenderer = new JuliaEngine();

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);

            Rect rect = holder.getSurfaceFrame();
            mHeight = rect.height();
            mWidth = rect.width();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mJuliaRenderer.destroy();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            width = (int) (width / mScale);
            height = (int) (height / mScale);

            mJuliaRenderer.init(JuliaWallpaperService.this.getBaseContext(), width, height, mScale);

            draw(0.5f);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            // TODO !!!!!!!!!!!!!!!
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep,
                float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
            Log.d(TAG, "xOffset: " + xOffset);
            draw(xOffset);
            /* if (xOffset == 1.0f) { mJuliaRenderer.setPrecision(128);
             * draw(xOffset); } */
            mJuliaRenderer.setPrecision(32);

        }

        private void draw(float xOffset) {

            long startTime = System.currentTimeMillis();
            Bitmap bitmap = mJuliaRenderer.renderJulia(0.5f - xOffset / 5, 0.2f);
            long renderTime = System.currentTimeMillis() - startTime;
            Log.d(TAG, "Rendertime: " + (renderTime));

            Canvas c = null;
            SurfaceHolder holder = getSurfaceHolder();
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    c.drawBitmap(bitmap, mJuliaRenderer.getScaleMatrix(), null);
                }
            } finally {
                if (c != null) {
                    holder.unlockCanvasAndPost(c);
                }
            }
        }

    }
}
