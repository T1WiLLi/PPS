package pewpew.smash.game.gamemode;

import pewpew.smash.engine.Canvas;

import java.awt.event.MouseEvent;

public interface GameMode {
    public void update(double deltaTime);

    public void render(Canvas canvas);

    public void reset();

    public void start();

    public void handleMousePress(MouseEvent e);

    public void handleMouseRelease(MouseEvent e);

    public void handleMouseMove(MouseEvent e);

    public void handleMouseDrag(MouseEvent e);
}
