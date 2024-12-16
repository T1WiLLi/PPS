package pewpew.smash.game;

import pewpew.smash.database.Database;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.Game;
import pewpew.smash.game.audio.AudioPlayer;
import pewpew.smash.game.input.KeyHandler;
import pewpew.smash.game.input.MouseHandler;
import pewpew.smash.game.network.upnp.UPnPPortManager;
import pewpew.smash.game.post_processing.PostProcessingManager;
import pewpew.smash.game.settings.SettingsManager;
import pewpew.smash.game.states.GameStateType;
import pewpew.smash.game.states.StateManager;
import pewpew.smash.game.utils.FontFactory;

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
        PostProcessingManager.getInstance();
    }

    @Override
    public void update() {
        this.stateManager.update();
    }

    @Override
    public void render(Canvas canvas) {
        this.stateManager.render(canvas);
        FontFactory.DEFAULT_FONT.applyFont(canvas);
    }

    @Override
    public void conclude() {
        UPnPPortManager.getInstance().closeAllPorts();
        AudioPlayer.getInstance().shutdown();
        Database.getInstance().dispose();
        System.exit(0);
    }
}
