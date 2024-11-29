package pewpew.smash.game;

import pewpew.smash.engine.entities.StaticEntity;
import pewpew.smash.game.world.WorldGenerator;

public class Camera {
    private volatile static Camera instance;

    private static final float SMOOTHING_FACTOR = 0.05f;

    private float x, y;
    private final int MAP_WIDTH = WorldGenerator.getWorldWidth();
    private final int MAP_HEIGHT = WorldGenerator.getWorldHeight();
    private float zoom = 1.0f;

    public static Camera getInstance() {
        if (instance == null) {
            instance = new Camera();
        }
        return instance;
    }

    public static float getOffsetX() {
        return instance != null ? instance.x : 0;
    }

    public static float getOffsetY() {
        return instance != null ? instance.y : 0;
    }

    public static float getZoom() {
        return instance != null ? instance.zoom : 1.0f;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
        clamp();
    }

    public void centerOn(StaticEntity entity) {
        float targetX = entity.getX() - (getViewportWidth() / 2) + (entity.getWidth() / 2);
        float targetY = entity.getY() - (getViewportHeight() / 2) + (entity.getHeight() / 2);
        this.x += (targetX - this.x) * SMOOTHING_FACTOR;
        this.y += (targetY - this.y) * SMOOTHING_FACTOR;
        clamp();
    }

    public float getViewportWidth() {
        return 800 / zoom;
    }

    public float getViewportHeight() {
        return 600 / zoom;
    }

    private void clamp() {
        if (x < 0) {
            x = 0;
        } else if (x > MAP_WIDTH - getViewportWidth()) {
            x = MAP_WIDTH - getViewportWidth();
        }

        if (y < 0) {
            y = 0;
        } else if (y > MAP_HEIGHT - getViewportHeight()) {
            y = MAP_HEIGHT - getViewportHeight();
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
        clamp();
    }

    public void setY(float y) {
        this.y = y;
        clamp();
    }

    private Camera() {
        this.x = 0;
        this.y = 0;
    }
}
