package pewpew.smash.game.hud;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.Camera;
import pewpew.smash.game.entities.Player;

public class CircleLoaderManager {
    private final ScheduledExecutorService scheduler;
    private final CircleLoader circleLoader;
    private boolean isLoading;
    private long startTime;
    private long delayMillis;

    public CircleLoaderManager() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.circleLoader = new CircleLoader(Integer.MIN_VALUE, Integer.MIN_VALUE, 24, 24);
        this.isLoading = false;
    }

    public void render(Canvas canvas) {
        if (isLoading) {
            canvas.scale(Camera.getZoom(), Camera.getZoom());
            canvas.translate(-Camera.getInstance().getX(), -Camera.getInstance().getY());
            circleLoader.render(canvas);
            canvas.translate(Camera.getInstance().getX(), Camera.getInstance().getY());
            canvas.resetScale();
        }
    }

    public void update(Player local) {
        if (isLoading) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            float progress = (float) elapsedTime / delayMillis;
            circleLoader.teleport(local.getX() + 10, local.getY() + 42);
            circleLoader.setCurrentValue(Math.min(progress * 100, 100));
        }
    }

    public void startLoader(long delaySeconds, Runnable action, Player player) {
        if (isLoading) {
            return;
        }

        this.isLoading = true;
        this.delayMillis = delaySeconds * 1000;
        this.startTime = System.currentTimeMillis();
        circleLoader.setCurrentValue(0);

        scheduler.schedule(() -> {
            action.run();
            isLoading = false;
        }, delaySeconds, TimeUnit.SECONDS);
    }
}
