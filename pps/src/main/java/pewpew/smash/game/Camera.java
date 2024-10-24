package pewpew.smash.game;

import pewpew.smash.engine.entities.StaticEntity;

public class Camera {
    private static Camera instance;

    private float x, y;
    private final int MAP_WIDTH = 2000;
    private final int MAP_HEIGHT = 2000;
    private float zoom = 1.0f;

    public static float getOffsetX() {
        return instance != null ? instance.x : 0;
    }

    public static float getOffsetY() {
        return instance != null ? instance.y : 0;
    }

    public static float getZoom() {
        return instance != null ? instance.zoom : 1.0f;
    }

    public Camera() {
        this.x = 0;
        this.y = 0;
        instance = this;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
        clamp();
    }

    public void centerOn(StaticEntity entity) {
        this.x = entity.getX() - (getViewportWidth() / 2) + (entity.getWidth() / 2);
        this.y = entity.getY() - (getViewportHeight() / 2) + (entity.getHeight() / 2);
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
}
