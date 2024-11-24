package pewpew.smash.game.input;

import java.awt.event.KeyEvent;

import pewpew.smash.engine.controls.MovementController;

public class GamePad extends MovementController {

    private static final GamePad INSTANCE = new GamePad();

    public final synchronized static GamePad getInstance() {
        return INSTANCE;
    }

    public boolean isUpKeyPressed() {
        return isKeyPressed("upKey");
    }

    public boolean isDownKeyPressed() {
        return isKeyPressed("downKey");
    }

    public boolean isLeftKeyPressed() {
        return isKeyPressed("leftKey");
    }

    public boolean isRightKeyPressed() {
        return isKeyPressed("rightKey");
    }

    public boolean isUseKeyPressed() {
        return isKeyPressed("use");
    }

    public boolean isReloadKeyPressed() {
        return isKeyPressed("reload");
    }

    public boolean isPauseKeyPressed() {
        return isKeyPressed("pause");
    }

    public boolean isSwitchWeaponOneKeyPressed() {
        return isKeyPressed(KeyEvent.VK_1);
    }

    public boolean isSwitchWeaponTwoKeyPressed() {
        return isKeyPressed(KeyEvent.VK_2);
    }

    public Object[] isConsumableKeysPressed() {
        if (isKeyPressed(KeyEvent.VK_3)) {
            return new Object[] { true, KeyEvent.VK_3 };
        } else if (isKeyPressed(KeyEvent.VK_4)) {
            return new Object[] { true, KeyEvent.VK_4 };
        } else if (isKeyPressed(KeyEvent.VK_5)) {
            return new Object[] { true, KeyEvent.VK_5 };
        } else {
            return new Object[] { false };
        }
    }

    private GamePad() {
        bindKeys(new int[] { KeyEvent.VK_F1, KeyEvent.VK_F2, KeyEvent.VK_F3, KeyEvent.VK_F4, KeyEvent.VK_RIGHT,
                KeyEvent.VK_LEFT });
        bindKeys(new int[] { KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5 });
    }
}
