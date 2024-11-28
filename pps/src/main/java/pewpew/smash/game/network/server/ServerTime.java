package pewpew.smash.game.network.server;

public class ServerTime {

    private static volatile ServerTime instance;
    private volatile long gameStartTime;
    private volatile long lastUpdateTime;
    private volatile long accumulatedTime;
    private volatile double deltaTime;

    private static final int UPS_TARGET = 240;
    private final long UPDATE_INTERVAL;

    private volatile int currentUps;
    private volatile int upsCount;
    private volatile long lastUpsTime;

    private ServerTime() {
        this.gameStartTime = System.nanoTime();
        this.lastUpdateTime = System.nanoTime();
        this.accumulatedTime = 0;
        this.deltaTime = 0.0;
        this.UPDATE_INTERVAL = 1000000000 / UPS_TARGET;
        this.lastUpsTime = System.nanoTime();
        this.upsCount = 0;
        this.currentUps = 0;
    }

    public static synchronized ServerTime getInstance() {
        if (instance == null) {
            instance = new ServerTime();
        }
        return instance;
    }

    public boolean shouldUpdate() {
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

    public double getDeltaTime() {
        return deltaTime;
    }

    public long getCurrentTime() {
        return System.nanoTime();
    }

    public long getElapsedTimeMillis() {
        return (System.nanoTime() - gameStartTime) / 1_000_000;
    }

    public String getFormattedElapsedTime() {
        long seconds = getElapsedTimeMillis() / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
    }

    public int getCurrentUps() {
        return currentUps;
    }

    private void updateUPS() {
        upsCount++;
        long currentSecond = (System.nanoTime() - lastUpsTime) / 1_000_000_000;
        if (currentSecond >= 1) {
            currentUps = upsCount;
            upsCount = 0;
            lastUpsTime = System.nanoTime();
        }
    }

    public static void reset() {
        instance = null;
    }
}
