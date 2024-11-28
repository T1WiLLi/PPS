package pewpew.smash.game.gamemode;

import pewpew.smash.engine.Canvas;

public interface GameMode {
    public void update();

    public void render(Canvas canvas);

    public void reset();

    public void build(String[] args);
}
