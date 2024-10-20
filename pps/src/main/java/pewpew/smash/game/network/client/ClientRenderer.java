package pewpew.smash.game.network.client;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.RenderingEngine;
import pewpew.smash.game.Camera;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.manager.EntityManager;

import java.awt.image.BufferedImage;

public class ClientRenderer {

    private final RenderingEngine renderingEngine;

    private final Camera camera;
    private final EntityManager entityManager;
    private final BufferedImage worldImage;

    private volatile int fps;
    private volatile int ups;

    public ClientRenderer(EntityManager entityManager, BufferedImage worldImage) {
        this.renderingEngine = RenderingEngine.getInstance();
        this.camera = new Camera();
        this.entityManager = entityManager;
        this.worldImage = worldImage;
    }

    public void process() {
        updateCamera();
        render();
        this.renderingEngine.renderCanvasOnScreen();
    }

    private void render() {
        Canvas canvas = this.renderingEngine.getCanvas();

        // Later, we can add delta render, to exclude rendering object / entity and
        // player that are not on the local player's fov :)

        canvas.scale(camera.getX(), camera.getY());
        canvas.renderImage(worldImage, (int) -this.camera.getX(), (int) -this.camera.getY());

        this.entityManager.playerEntitiesIterator().forEachRemaining(player -> {
            canvas.translate(-this.camera.getX(), -this.camera.getY());
            player.render(canvas);
            canvas.translate(this.camera.getX(), this.camera.getY());
        });

        canvas.resetScale();

        // Render other things :)
    }

    private void updateCamera() {
        Player player = this.entityManager.getPlayerEntity(User.getInstance().getID());

        if (player != null) {
            this.camera.centerOn(player);
        }
    }
}
