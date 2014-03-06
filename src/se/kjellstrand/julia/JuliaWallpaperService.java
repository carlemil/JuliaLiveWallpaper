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

        private JuliaRSWrapper juliaHighQualityRSWrapper;
        private JuliaRSWrapper juliaLowQualityRSWrapper;

        private float xOffset = 0.0f;

        private int timeBasedSeed;

        private int width;

        private int height;

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
            juliaHighQualityRSWrapper.destroy();
            juliaLowQualityRSWrapper.destroy();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            juliaHighQualityRSWrapper = new JuliaRSWrapper(JuliaWallpaperService.this.getBaseContext(), width,
                    height / 2, 1f);
            juliaLowQualityRSWrapper = new JuliaRSWrapper(JuliaWallpaperService.this.getBaseContext(), width,
                    height / 2, 2f);
            draw(juliaHighQualityRSWrapper);
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
            Log.d(LOG_TAG, "Begin lq draw.");
            draw(juliaLowQualityRSWrapper);
            Log.d(LOG_TAG, "Finish lq draw.");
            long drawFinished = System.currentTimeMillis();
            hqTimer.setLastFrameTime(drawFinished - drawStart);

            // Log.d(LOG_TAG, "drawtime: " + hqTimer.getLastFrameTime());

            TODO
            kolla hur denna anropas, kanske får vi anrop som blockas här? borde kanske sätta
            xOffset != null, och sen ha en render tråd som koller efter xOffset!=null och kör en draw
            sålänge som den e xOffset!=null, och som kör en draw(juliaHighQualityRSWrapper); sist.

            hqTimer.startTimer();
        }


        @Override
        public void timeout() {
            Log.d(LOG_TAG, "---Begin hq draw.");
            draw(juliaHighQualityRSWrapper);
            Log.d(LOG_TAG, "---Finish hq draw.");
        }

        private void draw(JuliaRSWrapper juliaRSWrapper) {
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
