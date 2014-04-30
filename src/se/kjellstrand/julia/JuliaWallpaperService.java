package se.kjellstrand.julia;

import se.kjellstrand.julia.RenderHighQualityTimer.TimeoutListener;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
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

        private static final int Y_ACC_DIV = 2000;

        private float xOffset = 0.0f;

        private float touchYaccumulated = 0.0f;

        private float oldTouchY = 0.0f;

        private int timeBasedSeed;

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            Rect rect = holder.getSurfaceFrame();
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
            juliaHighQualityRSWrapper = new JuliaRSWrapper(
                    JuliaWallpaperService.this.getBaseContext(), width, height, 1f);
            juliaLowQualityRSWrapper = new JuliaRSWrapper(
                    JuliaWallpaperService.this.getBaseContext(), width, height, 2f);
            draw(juliaHighQualityRSWrapper);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            // TODO !!!!!!!!!!!!!!!

            // mScript stop/start?
            // mJuliaRenderer.getScript

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            String colorsKey = getResources().getString(R.string.pref_palette_key);
            String colors = sharedPreferences.getString(colorsKey, null);
            String drawModeKey = getResources().getString(R.string.pref_draw_mode_key);
            String drawMode = sharedPreferences.getString(drawModeKey, null);
            juliaHighQualityRSWrapper.setPalette(getApplicationContext(), colors, drawMode);
            juliaLowQualityRSWrapper.setPalette(getApplicationContext(), colors, drawMode);

            timeBasedSeed = (int) ((System.currentTimeMillis() / (1000 * 60 * 60)) % JuliaSeeds
                    .getNumberOfSeeds());
            Log.d(TAG, "seedTime " + timeBasedSeed);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (event.getPointerCount() == 1 && oldTouchY != -1) {
                    Log.d(LOG_TAG, "-- " + (event.getY() - oldTouchY));
                    touchYaccumulated += event.getY() - oldTouchY;
                    drawLowQuality();
                }
                oldTouchY = event.getY();
            } else {
                oldTouchY = -1;
            }
            super.onTouchEvent(event);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep,
                float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset,
                    yPixelOffset);

            this.xOffset = xOffset;

            drawLowQuality();
        }

        @Override
        public void timeout() {
            drawHighQuality();
        }

        private void drawLowQuality() {
            Log.d(LOG_TAG, "Begin lq draw.");
            draw(juliaLowQualityRSWrapper);
            Log.d(LOG_TAG, "Finish lq draw.");

            hqTimer.startTimer();
        }

        private void drawHighQuality() {
            Log.d(LOG_TAG, "---Begin hq draw.");
            draw(juliaHighQualityRSWrapper);
            Log.d(LOG_TAG, "---Finish hq draw.");
        }

        private void draw(JuliaRSWrapper juliaRSWrapper) {
            float offset = xOffset + touchYaccumulated / Y_ACC_DIV;
            double x = JuliaSeeds.getX(offset, timeBasedSeed);
            double y = JuliaSeeds.getY(offset, timeBasedSeed);
            Bitmap bitmap = juliaRSWrapper.renderJulia(x, y);
            Canvas c = null;
            SurfaceHolder holder = getSurfaceHolder();
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    matrix.reset();
                    matrix.setScale(juliaRSWrapper.getScale(), juliaRSWrapper.getScale());
                    // rs.setscale?
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
