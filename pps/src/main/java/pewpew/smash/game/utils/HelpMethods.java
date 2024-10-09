package pewpew.smash.game.utils;

import java.awt.Rectangle;

public class HelpMethods {
    public static boolean isIn(int mouseX, int mouseY, Rectangle bounds) {
        return bounds.getBounds().contains(mouseX, mouseY);
    }
}
