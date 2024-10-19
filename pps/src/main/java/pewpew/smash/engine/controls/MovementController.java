package pewpew.smash.engine.controls;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class MovementController extends KeyController {

    protected Map<String, Integer> movementKeyMap;

    public MovementController() {
        super();
        movementKeyMap = new HashMap<>(Map.of(
                "up", KeyEvent.VK_W,
                "down", KeyEvent.VK_S,
                "left", KeyEvent.VK_A,
                "right", KeyEvent.VK_D));
        bindMovementKeys();
    }

    public void updateBindings(Map<String, String> movement, Map<String, String> misc) {
        clearKeys();
        movementKeyMap.clear();
        bindKeysFromMap(movement);
        bindKeysFromMap(misc);
    }

    public boolean isKeyPressed(String action) {
        Integer keyCode = movementKeyMap.get(action);
        return keyCode != null && isKeyPressed(keyCode);
    }

    public Direction getDirection() {
        boolean up = isKeyPressed("up");
        boolean down = isKeyPressed("down");
        boolean left = isKeyPressed("left");
        boolean right = isKeyPressed("right");

        if (up && left) {
            return Direction.UP_LEFT;
        } else if (up && right) {
            return Direction.UP_RIGHT;
        } else if (down && left) {
            return Direction.DOWN_LEFT;
        } else if (down && right) {
            return Direction.DOWN_RIGHT;
        } else if (up) {
            return Direction.UP;
        } else if (down) {
            return Direction.DOWN;
        } else if (left) {
            return Direction.LEFT;
        } else if (right) {
            return Direction.RIGHT;
        }

        return Direction.NONE;
    }

    private void bindMovementKeys() {
        movementKeyMap.values().forEach(this::bindKey);
    }

    private void bindKeysFromMap(Map<String, String> keyMap) {
        keyMap.forEach((action, keyName) -> {
            int keyCode = parseKeyNameToKeyCode(keyName);
            if (keyCode != -1) {
                bindKey(keyCode);
                movementKeyMap.put(action, keyCode);
            }
        });
    }

    private int parseKeyNameToKeyCode(String keyName) {
        try {
            return (int) KeyEvent.class.getField("VK_" + keyName.toUpperCase()).get(null);
        } catch (Exception e) {
            System.err.println("Invalid key name: " + keyName);
            return -1;
        }
    }
}
