package pewpew.smash.game;

import pewpew.smash.database.Database;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.Game;
import pewpew.smash.engine.GameTime;
import pewpew.smash.engine.controls.MouseController;
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
        GameManager.getInstance().setGame(this);

        this.stateManager = StateManager.getInstance();
        this.stateManager.setState(GameStateType.MENU);

        new MouseHandler();
        new KeyHandler(this.stateManager);

        SettingsManager.getInstance().updateGameSettings();
    }

    @Override
    public void update(double deltaTime) {
        this.stateManager.update(deltaTime);
        System.out.println("Mouse Angle is: " + MouseController.getMouseAngleFrom(300, 400));
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
