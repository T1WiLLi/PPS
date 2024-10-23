package pewpew.smash.game.states;

import java.awt.event.KeyEvent;

import pewpew.smash.engine.Canvas;

public interface State {

    public abstract void update(double deltaTime);

    public abstract void render(Canvas canvas);

    public abstract void handleKeyPress(KeyEvent e);

    public abstract void handleKeyRelease(KeyEvent e);
}
