
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

    private int mWidth;

    private int mHeight;

    @Override
    public Engine onCreateEngine() {
        return new DemoEngine();
    }

    class DemoEngine extends Engine {

        private final String LOG_TAG = DemoEngine.class.getCanonicalName();

        JuliaEngine mJuliaRenderer = new JuliaEngine();

        private int timeBasedSeed;

        private RenderHighQualityTimer hqTimer = new RenderHighQualityTimer();

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

            mJuliaRenderer.init(JuliaWallpaperService.this.getBaseContext(), width, height / 2);

            draw(0.5f);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            // TODO !!!!!!!!!!!!!!!

            // mScript stop/start?
            timeBasedSeed = (int) ((System.currentTimeMillis() / (1000 * 60 * 60)) % JuliaSeeds
                    .getNumberOfSeeds());
            Log.d(TAG, "seedTime " + timeBasedSeed);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep,
                float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset,
                    yPixelOffset);
            draw(xOffset);
            /*
             * if (xOffset == 1.0f) { mJuliaRenderer.setPrecision(128);
             * draw(xOffset); }
             */
            mJuliaRenderer.setPrecision(12);
            hqTimer.setLastFrameTimestamp(System.currentTimeMillis());
        }

        // fada in på vissible med separat timestamp var som alltid checkas vid
        // render, som sätter hur ljus paletten är, 0-1f typ.
        // om 1 så skit i den å kör default, annars gånga ner alla färger för
        // att få en fadein under .3 sekunder eller så
        // låt julia cx cy state va beroende på timestam från senaste fade in

        private void draw(float xOffset) {

            // long startTime = System.currentTimeMillis();

            double x = JuliaSeeds.getX(xOffset, timeBasedSeed);
            double y = JuliaSeeds.getY(xOffset, timeBasedSeed);

            Log.d(LOG_TAG, "X: " + x + "  Y: " + y);
            Bitmap bitmap = mJuliaRenderer.renderJulia(x, y);
            // long renderTime = System.currentTimeMillis() - startTime;
            // Log.d(TAG, "Rendertime: " + (renderTime));

            Canvas c = null;
            SurfaceHolder holder = getSurfaceHolder();
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    Matrix rotateMatrix = new Matrix();
                    c.drawBitmap(bitmap, rotateMatrix, null);
                    rotateMatrix.setRotate(180, 0, 0);
                    rotateMatrix.postTranslate(mWidth, mHeight);
                    c.drawBitmap(bitmap, rotateMatrix, null);

                }
            } finally {
                if (c != null) {
                    holder.unlockCanvasAndPost(c);
                }
                long lastFrameTime = System.currentTimeMillis() - hqTimer.getLastFrameTimestamp();
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
