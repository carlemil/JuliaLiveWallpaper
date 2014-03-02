package se.kjellstrand.julia;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.util.Log;

public class RenderHighQualityTimer extends Thread {

    private static final String LOG_TAG = RenderHighQualityTimer.class.getCanonicalName();
    private long lastFrameTime = 0l;
    private float averageFrameTime = 0l;
    private float lastFrameWeight = 0.4f;

    private final Handler handler = new Handler();

    private Timer timer;

    private TimeoutListener listener;

    public RenderHighQualityTimer(TimeoutListener listener) {
        this.listener = listener;
    }

    public long getLastFrameTime() {
        return lastFrameTime;
    }

    public void setLastFrameTime(long lastFrameTime) {
        this.lastFrameTime = lastFrameTime;
        Log.d(LOG_TAG, "1 averageFrameTime " + averageFrameTime);
        if (averageFrameTime == 0) {
            averageFrameTime = lastFrameWeight;
        } else {
            averageFrameTime = averageFrameTime * lastFrameWeight + lastFrameTime * (1 - lastFrameWeight);
        }
        Log.d(LOG_TAG, "2 averageFrameTime " + averageFrameTime + " lastFrameTime " + lastFrameTime);

        if (averageFrameTime <= 0) {
            averageFrameTime = lastFrameWeight;
        }
        // averageFrameTime -923.0245
        Log.d(LOG_TAG, "3 averageFrameTime " + averageFrameTime);
    }

    public void startTimer() {
        try {
            if (timer != null) {
                Log.d(LOG_TAG, "Cancel timer: " + timer);
                timer.cancel();
            }
            timer = new Timer();
            Log.d(LOG_TAG, "Start timer: " + timer);
            timer.schedule(new TimerTask() {
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            listener.timeout();
                            Log.d(LOG_TAG, "+++ timeout timer: " + timer);
                            timer.cancel();
                        }
                    });
                }
            }, lastFrameTime * 2);
        } catch (IllegalStateException ise) {
            Log.d(LOG_TAG, "ISE: " + ise);
        }
    }

    public interface TimeoutListener {
        public void timeout();
    }
}
