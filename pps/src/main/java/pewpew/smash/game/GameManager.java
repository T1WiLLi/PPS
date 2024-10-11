package pewpew.smash.game;

import lombok.Setter;

public class GameManager {

    private volatile static GameManager instance;

    @Setter
    private volatile PewPewSmash game;

    public synchronized static GameManager getInstance() {
        if (instance == null) {
            synchronized (GameManager.class) {
                if (instance == null) {
                    instance = new GameManager();
                }
            }
        }
        return instance;
    }

    public void conclude() {
        this.game.conclude();
    }
}
