package se.kjellstrand.julia;

public class RenderHighQualityTimer extends Thread {

    private long lastFrameTime = 0l;

    public long getLastFrameTime() {
        return lastFrameTime;
    }

    public void setLastFrameTime(long lastFrameTime) {
        this.lastFrameTime = lastFrameTime;
    }

    public static void startTimer() {
        // TODO klyftig delay, 2x senaste frametiden kanske, sen kicka igång draw med highres
    }
}
