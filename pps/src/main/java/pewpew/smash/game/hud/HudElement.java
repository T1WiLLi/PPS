package pewpew.smash.game.hud;

import pewpew.smash.engine.Canvas;

public abstract class HudElement {
    protected final int x, y;
    protected final int width, height;

    protected abstract void render(Canvas canvas);

    public HudElement(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
