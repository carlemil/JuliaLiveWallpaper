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
//
//    private int mWidth;
//    private int mHeight;

    @Override
    public Engine onCreateEngine() {
        return new DemoEngine();
    }

    class DemoEngine extends Engine {

        private final String LOG_TAG = DemoEngine.class.getCanonicalName();
        JuliaEngine mJuliaRenderer = new JuliaEngine();
        private long seedTime;

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);

            Rect rect = holder.getSurfaceFrame();
//            mHeight = rect.height();
//            mWidth = rect.width();
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

            // mScript stop/start?
            seedTime = System.currentTimeMillis() % 10000000;
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep,
                float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
            draw(xOffset);
            /* if (xOffset == 1.0f) { mJuliaRenderer.setPrecision(128);
             * draw(xOffset); } */
            // mJuliaRenderer.setPrecision(32);

        }

        // fada in på vissible med separat timestamp var som alltid checkas vid
        // render, som sätter hur ljus paletten är, 0-1f typ.
        // om 1 så skit i den å kör default, annars gånga ner alla färger för
        // att få en fadein under .3 sekunder eller så
        // låt julia cx cy state va beroende på timestam från senaste fade in

        private void draw(float xOffset) {

            long startTime = System.currentTimeMillis();
            final float OFFSET_MULT = 50000f;
            float x = getX((long) (seedTime + xOffset * OFFSET_MULT));
            float y = getY((long) (seedTime + xOffset * OFFSET_MULT));
            Log.d(LOG_TAG, "X: " + x + "  Y: " + y);
            Bitmap bitmap = mJuliaRenderer.renderJulia(x, y);
            long renderTime = System.currentTimeMillis() - startTime;
            // Log.d(TAG, "Rendertime: " + (renderTime));

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

        // Used for the large circle tracing the edge of the Mandelbrot set.
        private static final double MAX_X = 0.75;
        private static final double MIN_X = -0.82;
        private static final double MAX_Y = 0.8;
        private static final double MIN_Y = -MAX_Y;
        private static final double BIG_C = 10000; //514229;
        // Used to create smaller circles to avoid repetitions in the julia seed
        // values
        private static final double DIV_X1 = 50;
        private static final double DIV_Y1 = 50;
        private static final double DIV_X2 = 400;
        private static final double DIV_Y2 = 400;
        private static final double MED_C = 15000;//  96557
        private static final double SMAL_C = 10000;//33461

        private float getY(long i) {
            return (float) (((((Math.cos(i / BIG_C) + 1d) / 2d) * (MAX_Y - MIN_Y)) + MIN_Y));// +
            // (Math.cos(i / MED_C) / DIV_Y1) + (Math.cos(i / SMAL_C) /
            // DIV_Y2));
        }

        private float getX(long i) {
            return (float) (((((Math.sin(i / BIG_C) + 1d) / 2d) * (MAX_X - MIN_X)) + MIN_X));// +
            // (Math.sin(i / MED_C) / DIV_X1) + (Math.sin(i / SMAL_C) /
            // DIV_X2));
        }
    }
}
