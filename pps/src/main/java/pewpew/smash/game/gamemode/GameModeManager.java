package pewpew.smash.game.gamemode;

import lombok.Getter;
import pewpew.smash.engine.Canvas;

public class GameModeManager {

    private static GameModeManager instance;

    @Getter
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

    public void update() {
        if (currentGameMode != null) {
            currentGameMode.update();
        } else {
            System.out.println("Gamemode null");
        }
    }

    public void render(Canvas canvas) {
        if (currentGameMode != null) {
            currentGameMode.render(canvas);
        }
    }
}
