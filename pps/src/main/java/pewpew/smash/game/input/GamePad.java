package pewpew.smash.game.input;

import lombok.Setter;
import pewpew.smash.engine.controls.KeyController;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

@Setter
public class GamePad extends KeyController {

    private static final GamePad INSTANCE = new GamePad();
    private final Map<String, Integer> actionKeyMap = new HashMap<>();

    public final synchronized static GamePad getInstance() {
        return INSTANCE;
    }

    public void updateBindings(Map<String, String> movement, Map<String, String> misc) {
        clearKeys();
        actionKeyMap.clear();
        bindKeysFromMap(movement);
        bindKeysFromMap(misc);
    }

    public boolean isKeyPressed(String action) {
        Integer keyCode = actionKeyMap.get(action);
        return keyCode != null && isKeyPressed(keyCode);
    }

    public boolean isKeyPressed(int keyCode) {
        return super.isKeyPressed(keyCode);
    }

    public boolean isMoving() {
        return isKeyPressed("left") || isKeyPressed("right")
                || isKeyPressed("up") || isKeyPressed("down");
    }

    private void bindKeysFromMap(Map<String, String> keyMap) {
        keyMap.forEach((action, keyName) -> {
            int keyCode = parseKeyNameToKeyCode(keyName);
            if (keyCode != -1) {
                bindKey(keyCode);
                actionKeyMap.put(action, keyCode);
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
