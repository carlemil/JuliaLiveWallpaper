
package se.kjellstrand.julia;

import java.util.concurrent.TimeUnit;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import se.kjellstrand.julia.RenderHighQualityTimer.TimeoutListener;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class JuliaWallpaperService extends WallpaperService {

    // private final String TAG =
    // JuliaWallpaperService.class.getCanonicalName();

    public static final float INITIAL_ZOOM = 1.6f;

    public static final int INITIAL_PRECISION = 32;

    private Tracker tracker = null;

    private SharedPreferences sharedPreferences = null;

    private String swipeMorphKey = null;

    synchronized public Tracker getTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(JuliaWallpaperService.this);
            tracker = analytics.newTracker(R.xml.tracker);
        }
        return tracker;
    }

    @Override
    public Engine onCreateEngine() {
        return new JuliaEngine();
    }

    public boolean isSwipeMorphEnabled() {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(JuliaWallpaperService.this);
            swipeMorphKey = getResources().getString(R.string.pref_swipe_morph_key);
        }
        return sharedPreferences.getBoolean(swipeMorphKey, false);
    }

    class JuliaEngine extends Engine implements TimeoutListener {

        private RenderHighQualityTimer hqTimer = new RenderHighQualityTimer(this);

        private Matrix matrix = new Matrix();

        private JuliaRSWrapper juliaHighQualityRSWrapper;

        private JuliaRSWrapper juliaLowQualityRSWrapper;

        private int yAccDiv = 2000;

        private static final float MIN_ZOOM = 0.7f;

        private static final float MAX_ZOOM = 2.5f;

        private float xOffset = 0.0f;

        private float touchYaccumulated = 0.0f;

        private float oldTouchY = 0.0f;

        private float oldTouchX = 0.0f;

        private int timeBasedSeed;

        private double previousPinchDist;

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
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
            // Sets the yAccDiv so that it will make scrolling in y feel similar
            // to scrolling in x-axis.
            yAccDiv = height * 2;
            juliaHighQualityRSWrapper = new JuliaRSWrapper(JuliaWallpaperService.this.getBaseContext(), width, height, 1f);
            juliaLowQualityRSWrapper = new JuliaRSWrapper(JuliaWallpaperService.this.getBaseContext(), width, height, 2f);
            drawLowQuality();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                setZoom(Settings.getZoom(getApplicationContext()));

                touchYaccumulated = Settings.getTouchYaccumulated(getApplicationContext());

                juliaHighQualityRSWrapper.setPalette(getApplicationContext());
                juliaLowQualityRSWrapper.setPalette(getApplicationContext());

                timeBasedSeed = (int) ((System.currentTimeMillis() / TimeUnit.HOURS.toMillis(1)) % JuliaSeeds.getNumberOfSeeds());

                drawLowQuality();
            } else {
                Settings.setZoom(getApplicationContext(), getZoom());
                Settings.setTouchYaccumulated(getApplicationContext(), touchYaccumulated);
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (isSwipeMorphEnabled()) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        if (event.getPointerCount() == 1) {
                            if (oldTouchY != 0) {
                                float dy = event.getY() - oldTouchY;
                                float dx = event.getX() - oldTouchX;
                                // Only activate if we have dragged at least as
                                // 2x much on the y axis as x axis.
                                if (Math.abs(dy) / 2 >= Math.abs(dx)) {
                                    touchYaccumulated += dy / yAccDiv;
                                    drawLowQuality();
                                }
                            }
                            oldTouchY = event.getY();
                            oldTouchX = event.getX();

                        } else if (event.getPointerCount() >= 2) {
                            double pinchDist = Math.sqrt(Math.pow((event.getY(0) - event.getY(1)), 2)
                                    + Math.pow((event.getX(0) - event.getX(1)), 2));
                            if (previousPinchDist != 0) {
                                double pinchDistChange = pinchDist / previousPinchDist;
                                float zoom = (float) (getZoom() * pinchDistChange);
                                setZoom(zoom);
                                drawLowQuality();
                            }
                            previousPinchDist = pinchDist;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        previousPinchDist = 0;
                        oldTouchY = 0;
                        break;

                    default:
                        break;
                }
            }
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
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset,
                int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);

            this.xOffset = xOffset;

            drawLowQuality();
        }

        @Override
        public void timeout() {
            drawHighQuality();
        }

        private void drawLowQuality() {
            draw(juliaLowQualityRSWrapper);
            hqTimer.startTimer();
        }

        private void drawHighQuality() {
            draw(juliaHighQualityRSWrapper);
        }

        private void draw(JuliaRSWrapper juliaRSWrapper) {
            if (isVisible()) {
                float offset = xOffset + touchYaccumulated;
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
}
