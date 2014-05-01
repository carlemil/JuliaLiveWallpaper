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

        private static final float MIN_ZOOM = 0.7f;

        private static final float MAX_ZOOM = 3.0f;

        private float xOffset = 0.0f;

        private float touchYaccumulated = 0.0f;

        private float oldTouchY = 0.0f;

        private int timeBasedSeed;

        private double startPinchDist;

        private int previousPointerCount;

        private float startPinchZoomZoom;

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
                Log.d(LOG_TAG, "getPointerCount " + event.getPointerCount());
                if (event.getPointerCount() == 1 && oldTouchY != -1) {
                    touchYaccumulated += event.getY() - oldTouchY;
                    drawLowQuality();
                    previousPointerCount = 1;
                } else if (event.getPointerCount() == 2) {
                    double pinchDist = Math.sqrt( //
                            Math.pow((event.getY(0) - event.getY(1)), 2)
                                    + Math.pow((event.getX(0) - event.getX(1)), 2));
                    if (previousPointerCount <= 1) {
                        previousPointerCount = 2;
                        startPinchDist = pinchDist;
                        startPinchZoomZoom = getZoom();
                    }
                    double pinchDistChange = pinchDist / startPinchDist;

                    float zoom = (float) (startPinchZoomZoom * pinchDistChange);

                    Log.d(LOG_TAG, "zoom " + zoom + "  pinchDistChange " + pinchDistChange + "  pinchDist "
                            + pinchDist + " ,startPinchDist " + startPinchDist);

                    setZoom(zoom);
                    drawLowQuality();
                }
                oldTouchY = event.getY();
            } else {
                oldTouchY = -1;
            }
            super.onTouchEvent(event);
        }

        private float getZoom() {
            return juliaHighQualityRSWrapper.getZoom();
        }

        private void setZoom(float zoom) {
            if (zoom < MIN_ZOOM) {
                zoom = MIN_ZOOM;
            } else if (zoom > MAX_ZOOM) {
                zoom = MAX_ZOOM;
            }
            juliaHighQualityRSWrapper.setZoom(zoom);
            juliaLowQualityRSWrapper.setZoom(zoom);
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
            // Log.d(LOG_TAG, "Begin lq draw.");
            draw(juliaLowQualityRSWrapper);
            // Log.d(LOG_TAG, "Finish lq draw.");

            hqTimer.startTimer();
        }

        private void drawHighQuality() {
            // Log.d(LOG_TAG, "---Begin hq draw.");
            draw(juliaHighQualityRSWrapper);
            // Log.d(LOG_TAG, "---Finish hq draw.");
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
