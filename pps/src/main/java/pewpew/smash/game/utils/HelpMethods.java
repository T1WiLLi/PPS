package pewpew.smash.game.utils;

import java.awt.Rectangle;
import java.util.List;
import java.util.Random;

import pewpew.smash.engine.controls.MouseController;
import pewpew.smash.game.objects.Item;

public class HelpMethods {
    public static boolean isIn(Rectangle bounds) {
        return bounds.getBounds().contains(MouseController.getMouseX(), MouseController.getMouseY());
    }

    public static int getRandomBetween(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    public synchronized static void getIDOfItem(List<Item> items, String place) {
        System.out.println("From: " + place);
        items.forEach(item -> {
            System.out.println("id: " + item.getId() + " | for position: [" + item.getX() + "," + item.getY() + "]");
        });
    }
}
