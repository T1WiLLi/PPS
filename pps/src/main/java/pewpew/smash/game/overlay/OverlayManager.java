package pewpew.smash.game.overlay;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import pewpew.smash.engine.Canvas;

public class OverlayManager {

    private static OverlayManager instance;
    private Deque<Overlay> overlays;

    public synchronized static OverlayManager getInstance() {
        if (instance == null) {
            synchronized (OverlayManager.class) {
                if (instance == null) {
                    instance = new OverlayManager();
                }
            }
        }
        return instance;
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
        overlays.forEach(overlay -> overlay.render(canvas));
    }

    private boolean isEventHandledByOverlay(Overlay overlay, String eventType) {
        FunctionalOverlay annotation = overlay.getClass().getAnnotation(FunctionalOverlay.class);
        if (annotation != null) {
            for (String handler : annotation.value()) {
                if (handler.equals(eventType)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean handleMousePress(MouseEvent e) {
        Overlay currentOverlay = overlays.peek();
        if (currentOverlay != null && currentOverlay.isDisplaying()) {
            if (isEventHandledByOverlay(currentOverlay, "HandleMousePress")) {
                currentOverlay.handleMousePress(e);
            }
            return true;
        }
        return false;
    }

    public boolean handleMouseRelease(MouseEvent e) {
        Overlay currentOverlay = overlays.peek();
        if (currentOverlay != null && currentOverlay.isDisplaying()) {
            if (isEventHandledByOverlay(currentOverlay, "HandleMouseRelease")) {
                currentOverlay.handleMouseRelease(e);
            }
            return true;
        }
        return false;
    }

    public boolean handleMouseMove(MouseEvent e) {
        Overlay currentOverlay = overlays.peek();
        if (currentOverlay != null && currentOverlay.isDisplaying()) {
            if (isEventHandledByOverlay(currentOverlay, "HandleMouseMove")) {
                currentOverlay.handleMouseMove(e);
            }
            return true;
        }
        return false;
    }

    public boolean handleMouseDrag(MouseEvent e) {
        Overlay currentOverlay = overlays.peek();
        if (currentOverlay != null && currentOverlay.isDisplaying()) {
            if (isEventHandledByOverlay(currentOverlay, "HandleMouseDrag")) {
                currentOverlay.handleMouseDrag(e);
            }
            return true;
        }
        return false;
    }

    public boolean handleKeyPress(KeyEvent e) {
        Overlay currentOverlay = overlays.peek();
        if (currentOverlay != null && currentOverlay.isDisplaying()) {
            if (isEventHandledByOverlay(currentOverlay, "HandleKeyPress")) {
                currentOverlay.handleKeyPress(e);
            }
            return true;
        }
        return false;
    }

    public boolean handleKeyRelease(KeyEvent e) {
        Overlay currentOverlay = overlays.peek();
        if (currentOverlay != null && currentOverlay.isDisplaying()) {
            if (isEventHandledByOverlay(currentOverlay, "HandleKeyRelease")) {
                currentOverlay.handleKeyRelease(e);
            }
            return true;
        }
        return false;
    }

    private OverlayManager() {
        overlays = new ArrayDeque<>();
    }
}
