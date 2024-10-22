package pewpew.smash.game.gamemode;

import pewpew.smash.engine.Canvas;

public class GameModeManager {

    private static GameModeManager instance;

    private GameMode currentGameMode;

    public synchronized static GameModeManager getInstance() {
        if (instance == null) {
            synchronized (GameModeManager.class) {
                if (instance == null) {
                    instance = new GameModeManager();
                }
            }
        }
        return instance;
    }

    public void setGameMode(GameModeType type) {
        this.currentGameMode = GameModeFactory.getGameMode(type);
    }

    public void update(double deltaTime) {
        if (currentGameMode != null) {
            currentGameMode.update(deltaTime);
        }
    }

    public void render(Canvas canvas) {
        if (currentGameMode != null) {
            currentGameMode.render(canvas);
        }
    }
}
