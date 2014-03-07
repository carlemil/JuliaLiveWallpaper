
package se.kjellstrand.julia;

import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

public class RenderHighQualityTimer extends Thread {

    private static final String LOG_TAG = RenderHighQualityTimer.class.getCanonicalName();

    private Timer timer;

    private TimeoutListener listener;

    public RenderHighQualityTimer(TimeoutListener listener) {
        this.listener = listener;
    }

    public void startTimer() {
        try {
            if (timer != null) {
                Log.d(LOG_TAG, "Cancel timer: " + timer);
                timer.cancel();
            }
            timer = new Timer();
            Log.d(LOG_TAG, "Start timer: " + timer );
            timer.schedule(new TimerTask() {
                public void run() {
                    Log.d(LOG_TAG, "+++ timeout timer: " + timer);
                    listener.timeout();
                    timer.cancel();
                }
            }, 200);
        } catch (IllegalStateException ise) {
            Log.d(LOG_TAG, "ISE: " + ise);
        }
    }

    public interface TimeoutListener {
        public void timeout();
    }
}
