package pewpew.smash.game.utils;

import java.awt.Rectangle;

import pewpew.smash.engine.controls.MouseController;

public class HelpMethods {
    public static boolean isIn(Rectangle bounds) {
        return bounds.getBounds().contains(MouseController.getMouseX(), MouseController.getMouseY());
    }
}
