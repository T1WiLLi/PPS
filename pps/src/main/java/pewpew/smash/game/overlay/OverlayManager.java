package pewpew.smash.game.overlay;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.controls.MouseController;
import pewpew.smash.game.utils.HelpMethods;

public class OverlayManager {
    private Deque<Overlay> overlays;

    public OverlayManager() {
        overlays = new ArrayDeque<>();
    }

    public void push(Overlay overlay) {
        if (!overlays.contains(overlay)) {
            overlay.activate();
            overlays.push(overlay);
        }
    }

    public void pop() {
        if (!overlays.isEmpty()) {
            Overlay overlay = overlays.pop();
            overlay.deactivate();
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
        return handleEvent(overlay -> handleMouseEvent(e, overlay));
    }

    public boolean handleMouseRelease(MouseEvent e) {
        return handleEvent(overlay -> handleMouseEvent(e, overlay));
    }

    public boolean handleMouseMove(MouseEvent e) {
        return handleEvent(overlay -> handleMouseEvent(e, overlay));
    }

    public boolean handleMouseDrag(MouseEvent e) {
        return handleEvent(overlay -> handleMouseEvent(e, overlay));
    }

    public boolean handleKeyPress(KeyEvent e) {
        return handleEvent(overlay -> overlay.handleKeyPress(e));
    }

    public boolean handleKeyRelease(KeyEvent e) {
        return handleEvent(overlay -> overlay.handleKeyRelease(e));
    }

    private boolean handleEvent(Consumer<Overlay> eventHandler) {
        if (!overlays.isEmpty()) {
            eventHandler.accept(overlays.peek());
        }
        return !overlays.isEmpty();
    }

    private void handleMouseEvent(MouseEvent e, Overlay overlay) {
        int mouseX = MouseController.getMouseX();
        int mouseY = MouseController.getMouseY();
        Rectangle bounds = new Rectangle(overlay.x, overlay.y, overlay.width, overlay.height);

        if (HelpMethods.isIn(mouseX, mouseY, bounds)) {
            if (e.getID() == MouseEvent.MOUSE_PRESSED) {
                overlay.handleMousePress(e);
            } else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                overlay.handleMouseRelease(e);
            } else if (e.getID() == MouseEvent.MOUSE_MOVED) {
                overlay.handleMouseMove(e);
            } else if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
                overlay.handleMouseDrag(e);
            }
        }
    }
}