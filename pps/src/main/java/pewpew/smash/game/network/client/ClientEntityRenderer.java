package pewpew.smash.game.network.client;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.entities.StaticEntity;
import pewpew.smash.game.Camera;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.utils.ViewUtils;
import pewpew.smash.game.world.entities.Bush;

public class ClientEntityRenderer {
    private final EntityManager entityManager;

    public ClientEntityRenderer(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void render(Canvas canvas, Camera camera) {
        // Cache view bounds
        ViewUtils.ViewBounds viewBounds = ViewUtils.getCurrentBounds();

        renderBulletEntities(canvas, camera, viewBounds);
        renderPlayers(canvas, camera, viewBounds);
        renderStaticEntities(canvas, camera, viewBounds);
        renderMovableEntities(canvas, camera, viewBounds);
    }

    private void renderPlayers(Canvas canvas, Camera camera, ViewUtils.ViewBounds viewBounds) {
        entityManager.getPlayerEntities().forEach(player -> {
            if (isInView(player.getX(), player.getY(), viewBounds)) {
                renderEntity(canvas, camera, player);
            }
        });
    }

    private void renderStaticEntities(Canvas canvas, Camera camera, ViewUtils.ViewBounds viewBounds) {
        entityManager.getStaticEntities().forEach(entity -> {
            if (isInView(entity.getX(), entity.getY(), viewBounds)) {
                renderEntity(canvas, camera, entity);
            }
        });
    }

    private void renderMovableEntities(Canvas canvas, Camera camera, ViewUtils.ViewBounds viewBounds) {
        entityManager.getMovableEntities().forEach(entity -> {
            if (isInView(entity.getX(), entity.getY(), viewBounds)) {
                renderEntity(canvas, camera, entity);
            }
        });
    }

    private void renderBulletEntities(Canvas canvas, Camera camera, ViewUtils.ViewBounds viewBounds) {
        entityManager.getBulletEntities().forEach(entity -> {
            if (isInView((int) entity.getX(), (int) entity.getY(), viewBounds)) {
                canvas.translate(-camera.getX(), -camera.getY());
                entity.render(canvas);
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

    private boolean isInView(int x, int y, ViewUtils.ViewBounds bounds) {
        return x >= bounds.minX() && x <= bounds.maxX() &&
                y >= bounds.minY() && y <= bounds.maxY();
    }
}