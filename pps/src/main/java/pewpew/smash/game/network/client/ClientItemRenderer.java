package pewpew.smash.game.network.client;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.Camera;
import pewpew.smash.game.network.manager.ItemManager;

// Implement delta rendering at some point
public class ClientItemRenderer {
    public void render(Canvas canvas, Camera camera) {
        ItemManager.getInstance().getItems().forEach(i -> {
            canvas.translate(-camera.getX(), -camera.getY());
            i.preview(canvas);
            canvas.translate(camera.getX(), camera.getY());
        });
    }
}
