package pewpew.smash.game.overlay;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import pewpew.smash.engine.Canvas;

public abstract class Overlay {
    protected boolean isDisplaying;
    protected int x, y, width, height;
    protected BufferedImage background;

    public Overlay(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isDisplaying = false;
    }

    public abstract void update();

    public abstract void render(Canvas canvas);

    public void handleMousePress(MouseEvent e) {
    }

    public void handleMouseRelease(MouseEvent e) {
    }

    public void handleMouseMove(MouseEvent e) {
    }

    public void handleMouseDrag(MouseEvent e) {
    }

    public void handleKeyPress(KeyEvent e) {
    }

    public void handleKeyRelease(KeyEvent e) {
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
        OverlayManager.getInstance().pop();
    }
}
