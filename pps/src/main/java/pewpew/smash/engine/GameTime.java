package pewpew.smash.engine;

import lombok.Getter;
import lombok.Setter;

public class GameTime {

    @Setter
    private int FPS_TARGET = 120;
    @Getter
    private int UPS_TARGET = 240;

    private static volatile GameTime gameInstance;
    private static volatile GameTime serverInstance;

    private static volatile int currentFps;
    private static volatile int currentUps;
    private static volatile int fpsCount;
    private static volatile int upsCount;
    private static volatile long fpsTimeDelta;
    private static volatile long upsTimeDelta;
    private static volatile long gameStartTime;

    private volatile long lastUpdateTime;
    private volatile long lastRenderTime;
    private volatile long accumulatedTime;
    private volatile double deltaTime;
    private final long UPDATE_INTERVAL;

    private GameTime() {
        gameStartTime = System.nanoTime();
        lastUpdateTime = System.nanoTime();
        lastRenderTime = System.nanoTime();
        UPDATE_INTERVAL = 1000000000 / UPS_TARGET;
        accumulatedTime = 0;
        deltaTime = 0.0;
    }

    public synchronized static GameTime getInstance() {
        if (gameInstance == null) {
            synchronized (GameTime.class) {
                if (gameInstance == null) {
                    gameInstance = new GameTime();
                }
            }
        }
        return gameInstance;
    }

    public synchronized static GameTime getServerInstance() {
        if (serverInstance == null) {
            synchronized (GameTime.class) {
                if (serverInstance == null) {
                    serverInstance = new GameTime();
                }
            }
        }
        return serverInstance;
    }

    public double getDeltaTime() {
        return deltaTime;
    }

    public synchronized boolean shouldUpdate() {
        long currentTime = System.nanoTime();
        long elapsedTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        accumulatedTime += elapsedTime;
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
