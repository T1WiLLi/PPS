package pewpew.smash.game.network.client;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.entities.StaticEntity;
import pewpew.smash.game.Camera;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.utils.ViewUtils;

public class ClientEntityRenderer {
    private final EntityManager entityManager;

    public ClientEntityRenderer(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void render(Canvas canvas, Camera camera) {
        renderPlayers(canvas, camera);
        renderStaticEntities(canvas, camera);
        renderMovableEntities(canvas, camera);
        renderBulletEntities(canvas, camera);

        canvas.resetScale();
    }

    private void renderPlayers(Canvas canvas, Camera camera) {
        entityManager.getPlayerEntities().forEach(player -> {
            if (ViewUtils.isInView(player.getX(), player.getY())) {
                renderEntity(canvas, camera, player);
            }
        });
    }

    private void renderStaticEntities(Canvas canvas, Camera camera) {
        entityManager.getStaticEntities().forEach(entity -> {
            if (ViewUtils.isInView(entity.getX(), entity.getY())) {
                renderEntity(canvas, camera, entity);
            }
        });
    }

    private void renderMovableEntities(Canvas canvas, Camera camera) {
        entityManager.getMovableEntities().forEach(entity -> {
            if (ViewUtils.isInView(entity.getX(), entity.getY())) {
                renderEntity(canvas, camera, entity);
            }
        });
    }

    private void renderBulletEntities(Canvas canvas, Camera camera) {
        entityManager.getBulletEntities().forEach(entity -> {
            if (ViewUtils.isInView((int) entity.getX(), (int) entity.getY())) {
                canvas.translate(-camera.getX(), -camera.getY());
                entity.render(canvas);
                canvas.translate(camera.getX(), camera.getY());
            }
        });
    }

    private void renderEntity(Canvas canvas, Camera camera, StaticEntity entity) {
        canvas.translate(-camera.getX(), -camera.getY());
        entity.render(canvas);
        canvas.translate(camera.getX(), camera.getY());
    }
}
