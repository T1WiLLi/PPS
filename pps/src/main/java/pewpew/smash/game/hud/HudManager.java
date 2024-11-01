package pewpew.smash.game.hud;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.entities.Player;

// Only 1 per client
public class HudManager {
    private static HudManager instance;

    private Player local;

    private BarDisplayer healthBar;

    public static HudManager getInstance() {
        if (instance == null) {
            instance = new HudManager();
        }
        return instance;
    }

    public void setPlayer(Player player) {
        this.local = player;
    }

    public void update() {
        this.healthBar.setValue(this.local.getHealth());
    }

    public void render(Canvas canvas) {
        this.healthBar.render(canvas);
    }

    private HudManager() {
        this.healthBar = new BarDisplayer(10, 10, 200, 25);
    }
}
