package pewpew.smash.game.network.client;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.entities.StaticEntity;
import pewpew.smash.game.Camera;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.utils.ScaleUtils;

public class EntityRenderer {
    private final EntityManager entityManager;
    private static final double FOV_BUFFER = 0.05; // 5% buffer for FOV calculations
    private static final int BASE_WIDTH = 800;
    private static final int BASE_HEIGHT = 600;

    public EntityRenderer(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void render(Canvas canvas, Camera camera) {
        ViewBounds bounds = calculateBounds(canvas, camera);

        renderPlayers(canvas, camera, bounds);
        renderMovableEntities(canvas, camera, bounds);
        renderUpdatableEntities(canvas, camera, bounds);

        canvas.resetScale();
    }

    private void renderPlayers(Canvas canvas, Camera camera, ViewBounds bounds) {
        entityManager.getPlayerEntities().forEach(player -> {
            renderEntity(canvas, camera, player);
        });
    }

    private void renderMovableEntities(Canvas canvas, Camera camera, ViewBounds bounds) {
        entityManager.getMovableEntities().forEach(entity -> {
            if (isInView(entity.getX(), entity.getY(), bounds)) {
                renderEntity(canvas, camera, entity);
            }
        });
    }

    private void renderUpdatableEntities(Canvas canvas, Camera camera, ViewBounds bounds) {
        entityManager.getUpdatableEntities().forEach(entity -> {
            if (isInView(entity.getX(), entity.getY(), bounds)) {
                renderEntity(canvas, camera, entity);
            }
        });
    }

    private void renderEntity(Canvas canvas, Camera camera, StaticEntity entity) {
        canvas.translate(-camera.getX(), -camera.getY());
        entity.render(canvas);
        canvas.translate(camera.getX(), camera.getY());
    }

    private ViewBounds calculateBounds(Canvas canvas, Camera camera) {
        double scaleX = ScaleUtils.getScaleX();
        double scaleY = ScaleUtils.getScaleY();

        double bufferX = BASE_WIDTH * FOV_BUFFER;
        double bufferY = BASE_HEIGHT * FOV_BUFFER;

        return new ViewBounds(
                camera.getX() / scaleX - bufferX,
                camera.getX() / scaleX + BASE_WIDTH + bufferX,
                camera.getY() / scaleY - bufferY,
                camera.getY() / scaleY + BASE_HEIGHT + bufferY);
    }

    private boolean isInView(int x, int y, ViewBounds bounds) {
        double scaleX = ScaleUtils.getScaleX();
        double scaleY = ScaleUtils.getScaleY();

        double scaledX = x / scaleX;
        double scaledY = y / scaleY;

        return scaledX >= bounds.minX && scaledX <= bounds.maxX &&
                scaledY >= bounds.minY && scaledY <= bounds.maxY;
    }

    private record ViewBounds(double minX, double maxX, double minY, double maxY) {
    }
}
