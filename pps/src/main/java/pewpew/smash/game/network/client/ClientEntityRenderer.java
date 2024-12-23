package pewpew.smash.game.network.client;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.entities.StaticEntity;
import pewpew.smash.game.Camera;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.world.entities.Bush;

import java.awt.geom.Rectangle2D;

public class ClientEntityRenderer {
    private static final double FOV_BUFFER = 0.1;
    private final EntityManager entityManager;

    public ClientEntityRenderer(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void render(Canvas canvas, Camera camera) {
        Rectangle2D screenBounds = calculateScreenBounds(camera);

        renderStaticEntities(canvas, camera, screenBounds);
        renderMovableEntities(canvas, camera, screenBounds);
        renderPlayers(canvas, camera, screenBounds);
        renderBullets(canvas, camera, screenBounds);
    }

    private Rectangle2D calculateScreenBounds(Camera camera) {
        float screenWidth = camera.getViewportWidth();
        float screenHeight = camera.getViewportHeight();

        double bufferX = screenWidth * FOV_BUFFER;
        double bufferY = screenHeight * FOV_BUFFER;

        return new Rectangle2D.Double(
                camera.getX() - bufferX,
                camera.getY() - bufferY,
                screenWidth + 2 * bufferX,
                screenHeight + 2 * bufferY);
    }

    private void renderStaticEntities(Canvas canvas, Camera camera, Rectangle2D screenBounds) {
        entityManager.getStaticEntities().forEach(entity -> {
            if (entity.getHitbox().intersects(screenBounds)) {
                renderEntity(canvas, camera, entity);
            }
        });
    }

    private void renderMovableEntities(Canvas canvas, Camera camera, Rectangle2D screenBounds) {
        entityManager.getMovableEntities().forEach(entity -> {
            if (entity.getHitbox().intersects(screenBounds)) {
                renderEntity(canvas, camera, entity);
            }
        });
    }

    private void renderPlayers(Canvas canvas, Camera camera, Rectangle2D screenBounds) {
        entityManager.getPlayerEntities().forEach(player -> {
            if (player.getHitbox().intersects(screenBounds)) {
                renderEntity(canvas, camera, player);
            }
        });
    }

    private void renderBullets(Canvas canvas, Camera camera, Rectangle2D screenBounds) {
        entityManager.getBulletEntities().forEach(bullet -> {
            if (bullet.getHitbox().intersects(screenBounds)) {
                canvas.translate(-camera.getX(), -camera.getY());
                bullet.render(canvas);
                canvas.translate(camera.getX(), camera.getY());
            }
        });
    }

    private void renderEntity(Canvas canvas, Camera camera, StaticEntity entity) {
        if (entity instanceof Bush bush) {
            bush.isIn(entityManager.getPlayerEntity(User.getInstance().getLocalID().get()));
        }

        canvas.translate(-camera.getX(), -camera.getY());
        entity.render(canvas);
        canvas.translate(camera.getX(), camera.getY());
    }
}
