
package se.kjellstrand.julia;

import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

public class RenderHighQualityTimer extends Thread {

    private static final int TIMEOUT = 1000;

    private static final String LOG_TAG = RenderHighQualityTimer.class.getCanonicalName();

    private Timer timer;

    private TimeoutListener listener;

    public RenderHighQualityTimer(TimeoutListener listener) {
        this.listener = listener;
    }

    public void startTimer() {
        try {
            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    listener.timeout();
                    timer.cancel();
                }
            }, TIMEOUT);
        } catch (IllegalStateException ise) {
            Log.d(LOG_TAG, "ISE: " + ise);
        }
    }

    public interface TimeoutListener {
        public void timeout();
    }
}
