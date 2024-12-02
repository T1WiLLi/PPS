package pewpew.smash.game.network.client;

import lombok.Getter;
import lombok.Setter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.Camera;
import pewpew.smash.game.event.StormEvent;

public class ClientEventManager {

    @Getter
    @Setter
    private StormEvent storm;

    public void update() {
        if (storm != null) {
            storm.update();
        }
    }

    public void render(Canvas canvas, Camera camera) {
        canvas.translate(-camera.getX(), -camera.getY());
        if (storm != null) {
            storm.render(canvas);
        }
        canvas.translate(camera.getX(), camera.getY());
    }
}