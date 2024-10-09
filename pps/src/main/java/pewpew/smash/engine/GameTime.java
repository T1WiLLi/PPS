package pewpew.smash.engine;

import lombok.Setter;

public class GameTime {

    @Setter
    private int FPS_TARGET = 144;
    private int UPS_TARGET = 200;

    private static volatile GameTime instance;

    private static volatile int currentFps;
    private static volatile int currentUps;
    private static volatile int fpsCount;
    private static volatile int upsCount;
    private static volatile long fpsTimeDelta;
    private static volatile long upsTimeDelta;
    private static volatile long gameStartTime;

    private long lastUpdateTime;
    private long lastRenderTime;
    private long accumulatedTime;
    private final long UPDATE_INTERVAL;

    private GameTime() {
        gameStartTime = System.nanoTime();
        lastUpdateTime = System.nanoTime();
        lastRenderTime = System.nanoTime();
        UPDATE_INTERVAL = 1000000000 / UPS_TARGET;
        accumulatedTime = 0;
    }

    public static GameTime getInstance() {
        if (instance == null) {
            synchronized (GameTime.class) {
                if (instance == null) {
                    instance = new GameTime();
                }
            }
        }
        return instance;
    }

    public boolean shouldUpdate() {
        long currentTime = System.nanoTime();
        accumulatedTime += currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        if (accumulatedTime >= UPDATE_INTERVAL) {
            accumulatedTime -= UPDATE_INTERVAL;
            updateUPS();
            return true;
        }
        return false;
    }

    public boolean shouldRender() {
        long currentTime = System.nanoTime();
        if (currentTime - lastRenderTime >= (1000000000 / FPS_TARGET)) {
            lastRenderTime = currentTime;
            updateFPS();
            return true;
        }
        return false;
    }

    private void updateFPS() {
        fpsCount++;
        long currentSecond = (System.nanoTime() - gameStartTime) / 1000000000;
        if (fpsTimeDelta != currentSecond) {
            currentFps = fpsCount;
            fpsCount = 0;
            fpsTimeDelta = currentSecond;
        }
    }

    private void updateUPS() {
        upsCount++;
        long currentSecond = (System.nanoTime() - gameStartTime) / 1000000000;
        if (upsTimeDelta != currentSecond) {
            currentUps = upsCount;
            upsCount = 0;
            upsTimeDelta = currentSecond;
        }
    }

    public static long getCurrentTime() {
        return System.nanoTime();
    }

    public static int getCurrentFps() {
        return (currentFps > 0) ? currentFps : fpsCount;
    }

    public static int getCurrentUps() {
        return (currentUps > 0) ? currentUps : upsCount;
    }

    public static long getElapsedTime() {
        return (System.nanoTime() - gameStartTime) / 1000000;
    }

    public static String getFormattedElapsedTime() {
        long seconds = getElapsedTime() / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
    }
}