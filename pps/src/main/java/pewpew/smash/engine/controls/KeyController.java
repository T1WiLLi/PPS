package pewpew.smash.engine.controls;

import java.util.Map;

import lombok.Getter;

import java.util.HashMap;

import java.awt.event.KeyEvent;

public abstract class KeyController {

    @Getter
    private static KeyEvent keyPressed;

    private final Map<Integer, Boolean> pressedKeys;

    public KeyController() {
        this.pressedKeys = new HashMap<>();
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

    protected boolean isKeyPressed(int key) {
        return pressedKeys.getOrDefault(key, false);
    }

    public void keyPressed(KeyEvent e) {
        pressedKeys.computeIfPresent(e.getKeyCode(), (k, v) -> true);
        keyPressed = e;
    }

    public void keyReleased(KeyEvent e) {
        pressedKeys.computeIfPresent(e.getKeyCode(), (k, v) -> false);
    }
}
