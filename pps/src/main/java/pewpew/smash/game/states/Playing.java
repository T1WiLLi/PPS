package pewpew.smash.game.states;

import java.awt.event.KeyEvent;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.gamemode.GameModeManager;
import pewpew.smash.game.input.GamePad;
import pewpew.smash.game.overlay.OverlayManager;

public class Playing implements State {

    private GameModeManager gameModeManager;
    private OverlayManager overlayManager;

    private GamePad gamePad;

    public Playing() {
        init();
    }

    @Override
    public void update(double deltaTime) {
        this.gameModeManager.update(deltaTime);
        this.overlayManager.update();
    }

    @Override
    public void render(Canvas canvas) {
        this.gameModeManager.render(canvas);
        this.overlayManager.render(canvas);
    }

    @Override
    public void handleKeyPress(KeyEvent e) {
        this.gamePad.keyPressed(e);
    }

    @Override
    public void handleKeyRelease(KeyEvent e) {
        this.gamePad.keyReleased(e);
    }

    private void init() {
        this.gameModeManager = GameModeManager.getInstance();
        this.overlayManager = OverlayManager.getInstance();
    }
}
