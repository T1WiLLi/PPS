package pewpew.smash.game.network.client;

import lombok.Getter;
import lombok.Setter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.Camera;
import pewpew.smash.game.event.StormEvent;
import pewpew.smash.game.gamemode.GameModeType;

public class ClientEventManager {

    private final GameModeType gameMode;

    @Getter
    @Setter
    private StormEvent storm;

    public ClientEventManager(GameModeType gameMode) {
        this.gameMode = gameMode;
    }

    public void update() {
        switch (gameMode) {
            case SANDBOX -> updateEventSandbox();
            case BATTLE_ROYALE -> updateEventBattleRoyale();
            case ARENA -> updateEventArena();
        }
    }

    public void render(Canvas canvas, Camera camera) {
        canvas.translate(-camera.getX(), -camera.getY());
        if (storm != null) {
            storm.render(canvas);
        }
        canvas.translate(camera.getX(), camera.getY());
    }

    private void updateEventSandbox() {

    }

    private void updateEventBattleRoyale() {
        if (storm != null) {
            storm.update();
        }
    }

    private void updateEventArena() {

    }
}
