package pewpew.smash.game.states;

import java.awt.Color;
import java.awt.event.KeyEvent;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.GameTime;
import pewpew.smash.game.GameManager;
import pewpew.smash.game.audio.AudioPlayer;
import pewpew.smash.game.gamemode.GameModeManager;
import pewpew.smash.game.hud.HudManager;
import pewpew.smash.game.input.GamePad;
import pewpew.smash.game.overlay.OverlayFactory;
import pewpew.smash.game.overlay.OverlayManager;
import pewpew.smash.game.overlay.OverlayType;
import pewpew.smash.game.utils.FontFactory;

public class Playing implements State {

    private GameModeManager gameModeManager;
    private OverlayManager overlayManager;

    private GamePad gamePad;

    public Playing() {
        init();
        AudioPlayer.getInstance().stopAll();
    }

    @Override
    public void update() {
        this.gameModeManager.update();
        HudManager.getInstance().update();
        this.overlayManager.update();
        if (this.gamePad.isPauseKeyPressed()) {
            this.overlayManager.push(OverlayFactory.getOverlay(OverlayType.PAUSE));
        }
    }

    @Override
    public void render(Canvas canvas) {
        this.gameModeManager.render(canvas);
        HudManager.getInstance().render(canvas);
        this.overlayManager.render(canvas);

        FontFactory.IMPACT_SMALL.applyFont(canvas);
        canvas.renderString("FPS: " + GameTime.getCurrentFps(), 220, 30, Color.WHITE);
        FontFactory.resetFont(canvas);
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
        HudManager.getInstance();
        this.gameModeManager = GameModeManager.getInstance();
        this.overlayManager = OverlayManager.getInstance();
        this.gamePad = GamePad.getInstance();
    }
}
