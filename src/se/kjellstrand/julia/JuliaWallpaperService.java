package se.kjellstrand.julia;

import se.kjellstrand.julia.RenderHighQualityTimer.TimeoutListener;
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
        return new JuliaEngine();
    }

    class JuliaEngine extends Engine implements TimeoutListener {

        private final String LOG_TAG = JuliaEngine.class.getCanonicalName();

        private RenderHighQualityTimer hqTimer = new RenderHighQualityTimer(this);

        private Matrix matrix = new Matrix();

        private JuliaRSWrapper[] juliaRSWrappers = new JuliaRSWrapper[2];

        private JuliaRSWrapper juliaRSWrapper;

        private int timeBasedSeed;

        private int width;

        private int height;

        private float xOffset = 0.0f;

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);

            Rect rect = holder.getSurfaceFrame();
            width = rect.width();
            height = rect.height();

        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            juliaRSWrapper.destroy();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            juliaRSWrappers[0] = new JuliaRSWrapper(JuliaWallpaperService.this.getBaseContext(), width,
                    height / 2, 1f);
            juliaRSWrappers[1] = new JuliaRSWrapper(JuliaWallpaperService.this.getBaseContext(), width,
                    height / 2, 2f);

            juliaRSWrapper = juliaRSWrappers[1];

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

            long drawStart = System.currentTimeMillis();
            draw();
            long drawFinished = System.currentTimeMillis();
            hqTimer.setLastFrameTime(drawFinished - drawStart);

            // Log.d(LOG_TAG, "drawtime: " + hqTimer.getLastFrameTime());

            hqTimer.startTimer();
        }


        @Override
        public void timeout() {
            juliaRSWrapper = juliaRSWrappers[0];
            draw();
            juliaRSWrapper = juliaRSWrappers[1];
        }

        private void draw() {
            double x = JuliaSeeds.getX(xOffset, timeBasedSeed);
            double y = JuliaSeeds.getY(xOffset, timeBasedSeed);
            Bitmap bitmap = juliaRSWrapper.renderJulia(x, y);
            Canvas c = null;
            SurfaceHolder holder = getSurfaceHolder();
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    matrix.reset();
                    matrix.setScale(juliaRSWrapper.getScale(), juliaRSWrapper.getScale());
                    c.drawBitmap(bitmap, matrix, null);
                    matrix.postRotate(180, 0, 0);
                    matrix.postTranslate(width, height);
                    c.drawBitmap(bitmap, matrix, null);
                }
            } finally {
                if (c != null) {
                    holder.unlockCanvasAndPost(c);
                }
            }
        }

    }
}
