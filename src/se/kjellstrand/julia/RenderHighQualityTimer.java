
package se.kjellstrand.julia;

import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

public class RenderHighQualityTimer extends Thread {

    private static final String LOG_TAG = RenderHighQualityTimer.class.getCanonicalName();

    private long lastFrameTime = 0l;

    private float averageFrameTime = 0l;

    private Timer timer;

    private TimeoutListener listener;

    public RenderHighQualityTimer(TimeoutListener listener) {
        this.listener = listener;
    }

    public long getLastFrameTime() {
        return lastFrameTime;
    }

    public void setLastFrameTime(long lastFrameTime) {
        float lastFrameWeight = 0.4f;
        this.lastFrameTime = lastFrameTime;
        if (averageFrameTime == 0) {
            averageFrameTime = lastFrameWeight;
        } else {
            averageFrameTime = averageFrameTime * lastFrameWeight + lastFrameTime
                    * (1 - lastFrameWeight);
        }
        if (averageFrameTime <= 0) {
            averageFrameTime = lastFrameWeight;
        }
    }

    public void startTimer() {
        try {
            if (timer != null) {
                Log.d(LOG_TAG, "Cancel timer: " + timer);
                timer.cancel();
            }
            timer = new Timer();
            Log.d(LOG_TAG, "Start timer: " + timer + " TIME: " + (lastFrameTime * 2));
            timer.schedule(new TimerTask() {
                public void run() {
                    Log.d(LOG_TAG, "+++ timeout timer: " + timer);
                    listener.timeout();
                    timer.cancel();
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
