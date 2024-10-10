package pewpew.smash.game.overlay;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import pewpew.smash.engine.Canvas;

public abstract class Overlay {

    protected final OverlayManager overlayManager;

    protected boolean isDisplaying;
    protected int x, y, width, height;
    protected BufferedImage background;

    public abstract void update();

    public abstract void render(Canvas canvas);

    public abstract void handleMousePress(MouseEvent e);

    public abstract void handleMouseRelease(MouseEvent e);

    public abstract void handleMouseMove(MouseEvent e);

    public abstract void handleMouseDrag(MouseEvent e);

    public abstract void handleKeyPress(KeyEvent e);

    public abstract void handleKeyRelease(KeyEvent e);

    public Overlay(OverlayManager overlayManager, int x, int y, int width, int height) {
        this.overlayManager = overlayManager;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        isDisplaying = false;
    }

    public void activate() {
        isDisplaying = true;
    }

    public void deactivate() {
        isDisplaying = false;
    }

    public boolean isDisplaying() {
        return isDisplaying;
    }

    protected final void close() {
        overlayManager.pop();
    }
}
