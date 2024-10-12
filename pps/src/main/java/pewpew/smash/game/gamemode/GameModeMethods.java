package pewpew.smash.game.gamemode;

import pewpew.smash.engine.Canvas;

public interface GameModeMethods {
    public abstract void update(double deltaTime);

    public abstract void render(Canvas canvas);

    public abstract void reset();

    public abstract void start();

}
