package pewpew.smash.game.overlay;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Stack;

import pewpew.smash.engine.Canvas;

public class OverlayManager {
    private Stack<Overlay> overlays;

    public OverlayManager() {
        overlays = new Stack<Overlay>();
    }

    public void push(Overlay overlay) {
        if (!overlays.contains(overlay)) {
            overlay.toggleDisplay();
            overlays.push(overlay);
        }
    }

    public void pop() {
        if (!overlays.isEmpty()) {
            Overlay overlay = overlays.pop();
            overlay.toggleDisplay();
        }
    }

    public boolean hasActiveOverlays() {
        return !overlays.isEmpty();
    }

    public void update() {
        if (!overlays.isEmpty()) {
            overlays.peek().update();
        }
    }

    public void render(Canvas canvas) {
        for (Overlay overlay : overlays) {
            overlay.render(canvas);
        }
    }

    public boolean handleMousePress(MouseEvent e) {
        if (!overlays.isEmpty()) {
            overlays.peek().handleMousePress(e);
        }
        return !overlays.isEmpty();
    }

    public boolean handleMouseRelease(MouseEvent e) {
        if (!overlays.isEmpty()) {
            overlays.peek().handleMouseRelease(e);
        }
        return !overlays.isEmpty();
    }

    public boolean handleMouseMove(MouseEvent e) {
        if (!overlays.isEmpty()) {
            overlays.peek().handleMouseMove(e);
        }
        return !overlays.isEmpty();
    }

    public boolean handleMouseDrag(MouseEvent e) {
        if (!overlays.isEmpty()) {
            overlays.peek().handleMouseDrag(e);
        }
        return !overlays.isEmpty();
    }

    public boolean handleKeyPress(KeyEvent e) {
        if (!overlays.isEmpty()) {
            overlays.peek().handleKeyPress(e);
        }
        return !overlays.isEmpty();
    }

    public boolean handleKeyRelease(KeyEvent e) {
        if (!overlays.isEmpty()) {
            overlays.peek().handleKeyRelease(e);
        }
        return !overlays.isEmpty();
    }
}
