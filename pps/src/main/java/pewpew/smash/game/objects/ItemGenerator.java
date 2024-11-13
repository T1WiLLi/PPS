package pewpew.smash.game.objects;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pewpew.smash.game.network.manager.ItemManager;
import pewpew.smash.game.network.model.SerializedItem;
import pewpew.smash.game.network.packets.ItemAddPacket;
import pewpew.smash.game.network.serializer.SerializationUtility;
import pewpew.smash.game.network.server.ServerWrapper;
import pewpew.smash.game.world.WorldGenerator;

public class ItemGenerator {

    private static final int ITEM_SIZE = 48;

    private int WorldWidth = WorldGenerator.getWorldWidth();
    private int WorldHeight = WorldGenerator.getWorldHeight();
    private final Random random = new Random();

    private final List<Rectangle> existingsBounds = new ArrayList<>();

    // Using worldData make sure that the item is not generated in water...
    public void generateItems(ServerWrapper server, byte[][] worldData, int itemToBeGenerated) {
        for (int i = 0; i < itemToBeGenerated; i++) {
            int x, y;

            do {
                x = random.nextInt(WorldWidth);
                y = random.nextInt(WorldHeight);
            } while (!isValidPosition(x, y, worldData));

            Item item = generateRandomItem();
            item.teleport(x, y);

            existingsBounds.add(new Rectangle(x, y, ITEM_SIZE, ITEM_SIZE));

            ItemManager.getInstance(true).addItem(item);

            SerializedItem serializedItem = SerializationUtility.serializeItem(item);
            ItemAddPacket packet = new ItemAddPacket(x, y, serializedItem);
            server.sendToAllTCP(packet);
        }
    }

    private Item generateRandomItem() {
        int itemType = random.nextInt(1000);

        if (itemType < 300) {
            return ItemFactory.createAmmoStack();

        } else if (itemType < 700) {
            WeaponType weapon = switch (random.nextInt(60)) {
                case 0, 1, 2, 3, 4 -> WeaponType.HK416;
                case 5, 6, 7, 8, 9, 10, 11, 12, 13 -> WeaponType.M1A1;
                case 14, 15, 16, 17 -> WeaponType.DEAGLE;
                case 18, 19, 20, 21, 22, 23, 24 -> WeaponType.AK47;
                case 25, 26, 27, 28, 29 -> WeaponType.MAC10;
                case 30, 31, 32, 33, 34, 35, 36 -> WeaponType.MP5;
                case 37, 38, 39, 40, 41 -> WeaponType.COLT45;
                default -> WeaponType.GLOCK;
            };
            return ItemFactory.createItem(weapon);

        } else if (itemType < 950) { // 25% chance for a consumable
            ConsumableType consumable = switch (random.nextInt(35)) {
                case 0, 1, 2, 3, 4 -> ConsumableType.MEDIKIT;
                case 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 -> ConsumableType.PILL;
                default -> ConsumableType.BANDAGE;
            };
            return ItemFactory.createItem(consumable);

        } else { // 5% chance for a scope
            SpecialType scope = switch (random.nextInt(60)) {
                case 0, 1, 2, 3, 4 -> SpecialType.SCOPE_X4;
                case 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 -> SpecialType.SCOPE_X3;
                default -> SpecialType.SCOPE_X2;
            };
            return ItemFactory.createItem(scope);
        }
    }

    private boolean isValidPosition(int x, int y, byte[][] worldData) {
        if (worldData[x / 5][y / 5] == WorldGenerator.WATER) {
            return false;
        }

        Rectangle newItemBounds = new Rectangle(x, y, ITEM_SIZE, ITEM_SIZE);
        for (Rectangle bounds : existingsBounds) {
            if (bounds.intersects(newItemBounds)) {
                return false;
            }
        }

        return true;
    }
}
