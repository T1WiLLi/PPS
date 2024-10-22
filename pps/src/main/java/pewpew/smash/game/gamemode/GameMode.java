package pewpew.smash.game.gamemode;

import pewpew.smash.engine.Canvas;

public interface GameMode {
    public void update(double deltaTime);

    public void render(Canvas canvas);

    public void reset();

    public void start();
}
