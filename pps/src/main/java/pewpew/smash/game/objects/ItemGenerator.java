package pewpew.smash.game.objects;

import java.util.Random;

import pewpew.smash.game.network.manager.ItemManager;
import pewpew.smash.game.network.model.SerializedItem;
import pewpew.smash.game.network.packets.ItemAddPacket;
import pewpew.smash.game.network.serializer.SerializationUtility;
import pewpew.smash.game.network.server.ServerWrapper;
import pewpew.smash.game.world.WorldGenerator;

public class ItemGenerator {
    private int WorldWidth = WorldGenerator.getWorldWidth();
    private int WorldHeight = WorldGenerator.getWorldHeight();
    private final Random random = new Random();

    // Using worldData make sure that the item is not generated in water...
    public void generateItems(ServerWrapper server, byte[][] worldData, int itemToBeGenerated) {
        for (int i = 0; i < itemToBeGenerated; i++) {
            int x, y;

            do {
                x = random.nextInt(WorldWidth);
                y = random.nextInt(WorldHeight);
            } while (worldData[x / 5][y / 5] == WorldGenerator.WATER);

            Item item = generateRandomItem();
            item.teleport(x, y);

            ItemManager.getInstance().addItem(item);

            // Notify clients about the new item
            SerializedItem serializedItem = SerializationUtility.serializeItem(item);
            ItemAddPacket packet = new ItemAddPacket(x, y, serializedItem);
            server.sendToAllTCP(packet);
        }
    }

    private Item generateRandomItem() {
        int itemType = random.nextInt(100);

        if (itemType < 50) {
            ConsumableType consumable = switch (random.nextInt(3)) {
                case 0 -> ConsumableType.MEDIKIT;
                case 1 -> ConsumableType.BANDAGE;
                case 2 -> ConsumableType.PILL;
                default -> throw new IllegalStateException("Unexpected consumable type");
            };
            return ItemFactory.createItem(consumable);

        } else if (itemType < 80) {
            return ItemFactory.createAmmoStack();

        } else if (itemType < 95) {
            WeaponType weapon = switch (random.nextInt(4)) {
                case 0 -> WeaponType.AK47;
                case 1 -> WeaponType.HK416;
                case 2 -> WeaponType.M1A1;
                case 3 -> WeaponType.MAC10;
                default -> throw new IllegalStateException("Unexpected weapon type");
            };
            return ItemFactory.createItem(weapon);

        } else {
            SpecialType scope = switch (random.nextInt(3)) {
                case 0 -> SpecialType.SCOPE_X2;
                case 1 -> SpecialType.SCOPE_X3;
                case 2 -> SpecialType.SCOPE_X4;
                default -> throw new IllegalStateException("Unexpected scope type");
            };
            return ItemFactory.createItem(scope);
        }
    }
}
