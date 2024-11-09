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

            ItemManager.getInstance().addItem(item);

            SerializedItem serializedItem = SerializationUtility.serializeItem(item);
            ItemAddPacket packet = new ItemAddPacket(x, y, serializedItem);
            server.sendToAllTCP(packet);
        }
    }

    private Item generateRandomItem() {
        int itemType = random.nextInt(100);

        if (itemType < 20) {
            ConsumableType consumable = switch (random.nextInt(3)) {
                case 0 -> ConsumableType.MEDIKIT;
                case 1 -> ConsumableType.BANDAGE;
                case 2 -> ConsumableType.PILL;
                default -> throw new IllegalStateException("Unexpected consumable type");
            };
            return ItemFactory.createItem(consumable);

        } else if (itemType < 50) {
            return ItemFactory.createAmmoStack();

        } else if (itemType < 85) {
            WeaponType weapon = switch (random.nextInt(7)) {
                case 0 -> WeaponType.AK47;
                case 1 -> WeaponType.HK416;
                case 2 -> WeaponType.M1A1;
                case 3 -> WeaponType.MAC10;
                case 4 -> WeaponType.MP5;
                case 5 -> WeaponType.COLT45;
                case 6 -> WeaponType.DEAGLE;
                case 7 -> WeaponType.GLOCK;
                default -> throw new IllegalStateException("Unexpected weapon type");
            };
            return ItemFactory.createItem(weapon);

        } else {
            int scopeRarity = random.nextInt(100);
            SpecialType scope = switch (scopeRarity) {
                case 0 -> SpecialType.SCOPE_X4;
                case 1, 2, 3, 4 -> SpecialType.SCOPE_X3;
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
