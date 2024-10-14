package pewpew.smash.game.utils;

import java.awt.Rectangle;
import java.util.Random;

import pewpew.smash.engine.controls.MouseController;

public class HelpMethods {
    public static boolean isIn(Rectangle bounds) {
        return bounds.getBounds().contains(MouseController.getMouseX(), MouseController.getMouseY());
    }

    public static int getRandomBetween(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
}
