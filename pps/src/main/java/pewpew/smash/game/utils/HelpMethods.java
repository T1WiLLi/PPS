package pewpew.smash.game.utils;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import pewpew.smash.engine.controls.MouseController;
import pewpew.smash.game.objects.ConsumableType;
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

    public static Optional<ConsumableType> getConsumableType(int code) {
        return switch (code) {
            case KeyEvent.VK_3 -> Optional.of(ConsumableType.MEDIKIT);
            case KeyEvent.VK_4 -> Optional.of(ConsumableType.BANDAGE);
            case KeyEvent.VK_5 -> Optional.of(ConsumableType.PILL);
            default -> Optional.empty();
        };
    }
}
