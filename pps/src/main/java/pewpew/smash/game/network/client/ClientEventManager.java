package pewpew.smash.game.network.client;

import lombok.Getter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.Camera;
import pewpew.smash.game.event.StormEvent;
import pewpew.smash.game.hud.HudManager;

public class ClientEventManager {

    @Getter
    private StormEvent storm;

    public void setStorm(StormEvent storm) {
        if (storm != null) {
            this.storm = storm;
            HudManager.getInstance().setStorm(this.storm);
        }
    }

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