package se.kjellstrand.julia;

public class RenderHighQualityTimer {

    private long lastFrameTimestamp = 0l;
    private long lastFrameTime = 0l;

    public long getLastFrameTime() {
        return lastFrameTime;
    }

    public void setLastFrameTime(long lastFrameTime) {
        this.lastFrameTime = lastFrameTime;
    }

    public long getLastFrameTimestamp() {
        return lastFrameTimestamp;
    }

    public void setLastFrameTimestamp(long lastFrameTimestamp) {
        this.lastFrameTimestamp = lastFrameTimestamp;
    }
}
