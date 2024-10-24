package pewpew.smash.game.states;

import java.awt.Color;
import java.awt.event.KeyEvent;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.GameTime;
import pewpew.smash.game.GameManager;
import pewpew.smash.game.gamemode.GameModeManager;
import pewpew.smash.game.hud.HudManager;
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
        HudManager.getInstance().update();
        this.overlayManager.update();
    }

    @Override
    public void render(Canvas canvas) {
        this.gameModeManager.render(canvas);
        HudManager.getInstance().render(canvas);
        this.overlayManager.render(canvas);

        canvas.renderString("FPS: " + GameTime.getCurrentFps(), 10, 60, Color.WHITE);
        canvas.renderString("UPS: " + GameTime.getCurrentUps(), 10, 80, Color.WHITE);
    }

    @Override
    public void handleKeyPress(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            GameManager.getInstance().conclude();
        }
        this.gamePad.keyPressed(e);
    }

    @Override
    public void handleKeyRelease(KeyEvent e) {
        this.gamePad.keyReleased(e);
    }

    private void init() {
        this.gameModeManager = GameModeManager.getInstance();
        this.overlayManager = OverlayManager.getInstance();
        this.gamePad = GamePad.getInstance();
    }
}
