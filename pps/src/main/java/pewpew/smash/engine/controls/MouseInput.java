package pewpew.smash.engine.controls;

public enum MouseInput {
    LEFT_CLICK,
    RIGHT_CLICK,
    NONE;

    public static MouseInput getCurrentInput() {
        if (MouseController.isLeftMousePressed()) {
            return LEFT_CLICK;
        } else if (MouseController.isRightMousePressed()) {
            return RIGHT_CLICK;
        } else {
            return NONE;
        }
    }
}
