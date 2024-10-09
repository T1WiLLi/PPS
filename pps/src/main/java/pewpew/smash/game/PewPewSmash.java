package pewpew.smash.game;

import pewpew.smash.database.Database;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.Game;
import pewpew.smash.engine.GameTime;
import pewpew.smash.game.audio.AudioPlayer;
import pewpew.smash.game.input.KeyHandler;
import pewpew.smash.game.input.MouseHandler;
import pewpew.smash.game.settings.SettingsManager;
import pewpew.smash.game.states.GameStateType;
import pewpew.smash.game.states.StateManager;
import pewpew.smash.game.utils.FontFactory;

import java.awt.Color;

public class PewPewSmash extends Game {

    private StateManager stateManager;

    @Override
    public void init() {
        this.stateManager = new StateManager(this);
        this.stateManager.setState(GameStateType.MENU);

        new MouseHandler(this.stateManager);
        new KeyHandler(this.stateManager);

        // Preload settings
        SettingsManager.getInstance();
        AudioPlayer.getInstance();
        SettingsManager.getInstance().updateGameSettings();
    }

    @Override
    public void update() {
        this.stateManager.update();
    }

    @Override
    public void render(Canvas canvas) {
        this.stateManager.render(canvas);
        FontFactory.DEFAULT_FONT.applyFont(canvas);
        canvas.renderString("FPS: " + GameTime.getCurrentFps(), 10, 20, Color.WHITE);
        canvas.renderString("UPS: " + GameTime.getCurrentUps(), 10, 40, Color.WHITE);
    }

    @Override
    public void conclude() {
        AudioPlayer.getInstance().shutdown();
        Database.getInstance().dispose();
        System.exit(0);
    }
}
