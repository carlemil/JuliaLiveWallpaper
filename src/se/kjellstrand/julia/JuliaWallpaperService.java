
package se.kjellstrand.julia;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public class JuliaWallpaperService extends WallpaperService {

    private final String TAG = JuliaWallpaperService.class.getCanonicalName();

    @Override
    public Engine onCreateEngine() {
        return new DemoEngine();
    }

    class DemoEngine extends Engine {

        private final String LOG_TAG = DemoEngine.class.getCanonicalName();

        private JuliaEngine juliaRenderer = new JuliaEngine();

        private RenderHighQualityTimer hqTimer = new RenderHighQualityTimer();

        private int timeBasedSeed;

        private int width;

        private int height;

        private float xOffset = 0.5f;

        private Matrix rotateMatrix;

        private float scale = 1.5f;

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);

            Rect rect = holder.getSurfaceFrame();
            height = (int) (rect.height() / scale);
            width = (int) (rect.width() / scale) ;
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            juliaRenderer.destroy();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            juliaRenderer.init(JuliaWallpaperService.this.getBaseContext(), this.width,
                    this.height / 2);

            draw();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            // TODO !!!!!!!!!!!!!!!

            // mScript stop/start?
            // mJuliaRenderer.getScript

            timeBasedSeed = (int) ((System.currentTimeMillis() / (1000 * 60 * 60)) % JuliaSeeds
                    .getNumberOfSeeds());
            Log.d(TAG, "seedTime " + timeBasedSeed);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep,
                float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset,
                    yPixelOffset);

            this.xOffset = xOffset;

            if ((int) (xOffset / xOffsetStep) == xOffset / xOffsetStep) {
                RenderHighQualityTimer.startTimer();
            }
            juliaRenderer.setPrecision(12);

            draw();

        }

        private void draw() {
            long startTime = System.currentTimeMillis();

            double x = JuliaSeeds.getX(xOffset, timeBasedSeed);
            double y = JuliaSeeds.getY(xOffset, timeBasedSeed);

            Log.d(LOG_TAG, "X: " + x + "  Y: " + y);
            Bitmap bitmap = juliaRenderer.renderJulia(x, y);
            // long renderTime = System.currentTimeMillis() - startTime;
            // Log.d(TAG, "Rendertime: " + (renderTime));

            Canvas c = null;
            SurfaceHolder holder = getSurfaceHolder();
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    rotateMatrix = new Matrix();
                    c.drawBitmap(bitmap, rotateMatrix, null);
                    rotateMatrix.setRotate(180, 0, 0);
                    rotateMatrix.postTranslate(width, height);
                    c.drawBitmap(bitmap, rotateMatrix, null);
                }
            } finally {
                if (c != null) {
                    holder.unlockCanvasAndPost(c);
                }
                long lastFrameTime = System.currentTimeMillis() - startTime;
                hqTimer.setLastFrameTime(lastFrameTime);
            }
        }

        /*
         * array med x y size för seed punkter som ger "bra" julias array med
         * paletter (hur nu de ska funka med dynamisk size på paletten, kanske 5
         * färger och sen auto smooth mellan dom? en handler eller liknande för
         * att vänta 2x senaste tiden det tog att rita en frame, och sen rita
         * med scale == 1
         */
    }
}
