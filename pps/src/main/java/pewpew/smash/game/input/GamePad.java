package pewpew.smash.game.input;

import pewpew.smash.engine.controls.MovementController;

public class GamePad extends MovementController {

    private static final GamePad INSTANCE = new GamePad();

    public final synchronized static GamePad getInstance() {
        return INSTANCE;
    }

    public boolean isUpKeyPressed() {
        return isKeyPressed("up");
    }

    public boolean isDownKeyPressed() {
        return isKeyPressed("down");
    }

    public boolean isLeftKeyPressed() {
        return isKeyPressed("left");
    }

    public boolean isRightKeyPressed() {
        return isKeyPressed("right");
    }

    public boolean isUseKeyPressed() {
        return isKeyPressed("use");
    }

    public boolean isReloadKeyPressed() {
        return isKeyPressed("reload");
    }

    public boolean isMapKeyPressed() {
        return isKeyPressed("map");
    }

    public boolean isInventoryKeyPressed() {
        return isKeyPressed("inventory");
    }

    public boolean isPauseKeyPressed() {
        return isKeyPressed("pause");
    }
}
