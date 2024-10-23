package pewpew.smash.engine.controls;

import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;

import java.awt.event.KeyEvent;

public abstract class KeyController {

    private final Map<Integer, Boolean> pressedKeys;

    public KeyController() {
        this.pressedKeys = new ConcurrentHashMap<>();
    }

    protected void bindKeys(int[] keys) {
        for (int key : keys) {
            pressedKeys.put(key, false);
        }
    }

    protected void bindKey(int key) {
        pressedKeys.put(key, false);
    }

    protected void clearKeys() {
        pressedKeys.clear();
    }

    protected void unbindKey(int key) {
        pressedKeys.remove(key);
    }

    public synchronized boolean isKeyPressed(int key) {
        return pressedKeys.getOrDefault(key, false);
    }

    public synchronized void keyPressed(KeyEvent e) {
        pressedKeys.computeIfPresent(e.getKeyCode(), (k, v) -> true);
    }

    public synchronized void keyReleased(KeyEvent e) {
        pressedKeys.computeIfPresent(e.getKeyCode(), (k, v) -> false);
    }
}
