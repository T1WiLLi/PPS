package pewpew.smash.game;

import pewpew.smash.database.Database;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.Game;
import pewpew.smash.game.audio.AudioPlayer;
import pewpew.smash.game.input.KeyHandler;
import pewpew.smash.game.input.MouseHandler;
import pewpew.smash.game.network.upnp.NetworkUtils;
import pewpew.smash.game.network.upnp.UPnPPortManager;
import pewpew.smash.game.settings.SettingsManager;
import pewpew.smash.game.states.GameStateType;
import pewpew.smash.game.states.StateManager;
import pewpew.smash.game.utils.FontFactory;

public class PewPewSmash extends Game {

    private StateManager stateManager;

    @Override
    public void init() {
        System.out.println("IP: " + NetworkUtils.getExternalIP());
        try {
            UPnPPortManager.getInstance().openPort(12345, 12345);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    }

    @Override
    public void render(Canvas canvas) {
        this.stateManager.render(canvas);
        FontFactory.DEFAULT_FONT.applyFont(canvas);
    }

    @Override
    public void conclude() {
        AudioPlayer.getInstance().shutdown();
        Database.getInstance().dispose();
        System.exit(0);
    }
}
