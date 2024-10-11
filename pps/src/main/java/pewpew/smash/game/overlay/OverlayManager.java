package pewpew.smash.game.overlay;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
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

    public boolean handleMousePress(MouseEvent e) {
        return handleEvent(overlay -> overlay.handleMousePress(e));
    }

    public boolean handleMouseRelease(MouseEvent e) {
        return handleEvent(overlay -> overlay.handleMouseRelease(e));
    }

    public boolean handleMouseMove(MouseEvent e) {
        return handleEvent(overlay -> overlay.handleMouseMove(e));
    }

    public boolean handleMouseDrag(MouseEvent e) {
        return handleEvent(overlay -> overlay.handleMouseDrag(e));
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

    private OverlayManager() {
        overlays = new ArrayDeque<>();
    }
}