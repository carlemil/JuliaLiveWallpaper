
package se.kjellstrand.julia;

import java.util.concurrent.atomic.AtomicBoolean;

import se.kjellstrand.julia.RenderHighQualityTimer.TimeoutListener;
import se.kjellstrand.julia.prefs.Settings;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class JuliaWallpaperService extends WallpaperService {

    private static final float MIN_ZOOM = 0.5f;

    private static final float MAX_ZOOM = 3.0f;

    public static final float INITIAL_ZOOM = MAX_ZOOM;

    private Tracker tracker = null;

    private SharedPreferences sharedPreferences = null;

    private String verticalSwipeMorphKey = null;

    private String horizontalSwipeMorphKey = null;;

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

    public boolean isVerticalSwipeMorphEnabled() {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(JuliaWallpaperService.this);
            verticalSwipeMorphKey = getResources().getString(R.string.pref_swipe_ver_morph_key);
        }
        return sharedPreferences.getBoolean(verticalSwipeMorphKey, true);
    }

    public boolean isHorizontalSwipeMorphEnabled() {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(JuliaWallpaperService.this);
            horizontalSwipeMorphKey = getResources().getString(R.string.pref_swipe_hor_morph_key);
        }

        return sharedPreferences.getBoolean(horizontalSwipeMorphKey, true);
    }

    class JuliaEngine extends Engine implements TimeoutListener {

        private final String LOG_TAG = JuliaEngine.class.getCanonicalName();

        private RenderHighQualityTimer hqTimer = new RenderHighQualityTimer(this);

        private Matrix matrix = new Matrix();

        private RSWrapper juliaHighQualityRSWrapper;

        private RSWrapper juliaLowQualityRSWrapper;

        private float swipeYOffset = 0.0f;

        private float swipeXOffset = 0.0f;

        private float oldTouchY = 0.0f;

        private float oldTouchX = 0.0f;

        private double previousPinchDist;

        private AtomicBoolean drawing = new AtomicBoolean(false);

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
            juliaHighQualityRSWrapper = new RSWrapper(JuliaWallpaperService.this.getBaseContext(), width, height, 1f);
            juliaLowQualityRSWrapper = new RSWrapper(JuliaWallpaperService.this.getBaseContext(), width, height, 3f);
            drawLowQuality();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                setZoom(Settings.getZoom(getApplicationContext()));

                swipeYOffset = Settings.getTouchYaccumulated(getApplicationContext());
                swipeXOffset = Settings.getTouchXaccumulated(getApplicationContext());

                juliaHighQualityRSWrapper.setPalette(getApplicationContext());
                juliaLowQualityRSWrapper.setPalette(getApplicationContext());

                drawLowQuality();
            } else {
                Settings.setZoom(getApplicationContext(), getZoom());

                Settings.setTouchYaccumulated(getApplicationContext(), swipeYOffset);
                Settings.setTouchXaccumulated(getApplicationContext(), swipeXOffset);
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (isVerticalSwipeMorphEnabled()) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        if (event.getPointerCount() == 1) {
                            if (oldTouchY != 0) {
                                float dy = event.getY() - oldTouchY;
                                float dx = event.getX() - oldTouchX;
                                // Only activate if we have dragged at least as
                                // 2x much on the y axis as x axis.
                                if (Math.abs(dy) / 2 >= Math.abs(dx)) {
                                    swipeYOffset += dy;
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

            // Use this to move along the small circle
            this.swipeXOffset = xOffset;

            drawLowQuality();
        }

        @Override
        public void timeout() {
            drawHighQuality();
        }

        private void drawLowQuality() {
            if (drawing.compareAndSet(false, true)) {
                draw(juliaLowQualityRSWrapper);
                hqTimer.startTimer();
            }
        }

        private void drawHighQuality() {
            if (drawing.compareAndSet(false, true)) {
                draw(juliaHighQualityRSWrapper);
            }
        }

        private void draw(final RSWrapper juliaRSWrapper) {
            if (isVisible()) {
                double[] seedPoint = SeedPoint.get(swipeXOffset, swipeYOffset);
                final Bitmap bitmap = juliaRSWrapper.renderJulia(seedPoint[0], seedPoint[1]);
                SurfaceHolder holder = getSurfaceHolder();
                Canvas c = null;
                try {
                    c = holder.lockCanvas();
                    if (c != null) {
                        matrix.reset();
                        matrix.setScale(juliaRSWrapper.getScale(), juliaRSWrapper.getScale());
                        c.drawBitmap(bitmap, matrix, null);
                    }
                } catch (IllegalArgumentException e) {
                    Log.e(LOG_TAG, "BORK: ", e);
                    c = null;
                }
                if (c != null) {
                    holder.unlockCanvasAndPost(c);
                }
            }
            drawing.set(false);
        }
    }
}
