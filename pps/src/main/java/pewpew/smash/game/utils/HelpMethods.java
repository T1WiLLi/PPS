package pewpew.smash.game.utils;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import pewpew.smash.engine.controls.Direction;
import pewpew.smash.engine.controls.MouseController;
import pewpew.smash.game.entities.Inventory;
import pewpew.smash.game.entities.Plane;
import pewpew.smash.game.gamemode.GameModeType;
import pewpew.smash.game.network.packets.ItemAddPacket;
import pewpew.smash.game.network.serializer.SerializationUtility;
import pewpew.smash.game.network.server.ServerWrapper;
import pewpew.smash.game.objects.Consumable;
import pewpew.smash.game.objects.ConsumableType;
import pewpew.smash.game.objects.Item;
import pewpew.smash.game.objects.ItemFactory;
import pewpew.smash.game.objects.special.AmmoStack;
import pewpew.smash.game.objects.special.Scope;
import pewpew.smash.game.world.WorldGenerator;

public class HelpMethods {
    public static boolean isIn(Rectangle bounds) {
        return bounds.getBounds().contains(MouseController.getMouseX(), MouseController.getMouseY());
    }

    public static int getRandomBetween(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    public static Optional<ConsumableType> getConsumableType(int code) {
        return switch (code) {
            case KeyEvent.VK_3 -> Optional.of(ConsumableType.MEDIKIT);
            case KeyEvent.VK_4 -> Optional.of(ConsumableType.BANDAGE);
            case KeyEvent.VK_5 -> Optional.of(ConsumableType.PILL);
            default -> Optional.empty();
        };
    }

    public static GameModeType getGameModeTypeFromString(String str) {
        return Stream.of(GameModeType.values())
                .filter(g -> g.name().equalsIgnoreCase(str))
                .findFirst()
                .orElse(GameModeType.SANDBOX); // default
    }

    public static void sendDroppedItem(Item item, ServerWrapper server) {
        spreadItem(item);
        server.sendToAllTCP(new ItemAddPacket(
                item.getX(),
                item.getY(),
                SerializationUtility.serializeItem(item)));
    }

    public static void dropInventoryOfDeadPlayer(Inventory inventory, ServerWrapper server) {
        inventory.getPrimaryWeapon().ifPresent(weapon -> {
            weapon.drop();
            HelpMethods.spreadItem(weapon);
            server.sendToAllTCP(new ItemAddPacket(
                    weapon.getX(),
                    weapon.getY(),
                    SerializationUtility.serializeItem(weapon)));
        });

        inventory.getConsumables().forEach((consumableType, quantity) -> {
            IntStream.range(0, quantity)
                    .mapToObj(i -> {
                        Consumable consumable = ItemFactory.createItem(consumableType);
                        consumable.pickup(inventory.getOwner());
                        consumable.drop();
                        HelpMethods.spreadItem(consumable);
                        server.sendToAllTCP(new ItemAddPacket(
                                consumable.getX(),
                                consumable.getY(),
                                SerializationUtility.serializeItem(consumable)));
                        return consumable;
                    });
        });

        if (inventory.getAmmoCount() > 0) {
            AmmoStack ammoStack = inventory.getAmmoStack();
            ammoStack.drop();
            HelpMethods.spreadItem(ammoStack);
            server.sendToAllTCP(new ItemAddPacket(
                    ammoStack.getX(),
                    ammoStack.getY(),
                    SerializationUtility.serializeItem(ammoStack)));
        }

        Scope scope = inventory.getScope();
        scope.drop();
        HelpMethods.spreadItem(scope);
        server.sendToAllTCP(new ItemAddPacket(
                scope.getX(),
                scope.getY(),
                SerializationUtility.serializeItem(scope)));
    }

    public static Plane generatePlane() {
        Plane plane = new Plane();
        Random random = new Random();

        int worldWidth = WorldGenerator.getWorldWidth();
        int worldHeight = WorldGenerator.getWorldHeight();

        int centerX = worldWidth / 2;
        int centerY = worldHeight / 2;

        int x = 0, y = 0;
        Direction direction = null;

        int path = random.nextInt(8);
        path = 6;

        switch (path) {
            case 0:
                x = -plane.getWidth();
                y = centerY - plane.getHeight() / 2;
                direction = Direction.RIGHT;
                break;
            case 1:
                x = worldWidth;
                y = centerY - plane.getHeight() / 2;
                direction = Direction.LEFT;
                break;
            case 2:
                x = centerX - plane.getWidth() / 2;
                y = -plane.getHeight();
                direction = Direction.DOWN;
                break;
            case 3:
                x = centerX - plane.getWidth() / 2;
                y = worldHeight;
                direction = Direction.UP;
                break;
            case 4:
                x = -plane.getWidth();
                y = -plane.getHeight();
                direction = Direction.DOWN_RIGHT;
                break;
            case 5:
                x = worldWidth;
                y = worldHeight;
                direction = Direction.UP_LEFT;
                break;
            case 6:
                x = worldWidth;
                y = -plane.getHeight();
                direction = Direction.DOWN_LEFT;
                break;
            case 7:
                x = -plane.getWidth();
                y = worldHeight;
                direction = Direction.UP_RIGHT;
                break;
        }

        float rotation = getRotationFromDirection(direction);

        plane.teleport(x, y);
        plane.setRotation(rotation);
        plane.setDirection(direction);

        return plane;
    }

    private static float getRotationFromDirection(Direction direction) {
        return switch (direction) {
            case UP -> 0f;
            case DOWN -> 180f;
            case LEFT -> 270f;
            case RIGHT -> 90f;
            case UP_RIGHT -> 45f;
            case UP_LEFT -> 315f;
            case DOWN_RIGHT -> 135f;
            case DOWN_LEFT -> 225f;
            default -> 0f;
        };
    }

    private static void spreadItem(Item item) {
        int offsetX = (int) (Math.random() * 80 - 40);
        int offsetY = (int) (Math.random() * 80 - 40);
        item.teleport(item.getX() + offsetX, item.getY() + offsetY);
    }
}
