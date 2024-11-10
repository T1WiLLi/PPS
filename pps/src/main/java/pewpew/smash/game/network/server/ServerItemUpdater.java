package pewpew.smash.game.network.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.ItemManager;
import pewpew.smash.game.network.packets.InventoryPacket;
import pewpew.smash.game.network.packets.ItemAddPacket;
import pewpew.smash.game.network.packets.ItemRemovePacket;
import pewpew.smash.game.network.serializer.InventorySerializer;
import pewpew.smash.game.network.serializer.SerializationUtility;
import pewpew.smash.game.objects.Consumable;
import pewpew.smash.game.objects.Item;
import pewpew.smash.game.objects.RangedWeapon;
import pewpew.smash.game.objects.special.AmmoStack;

public class ServerItemUpdater {
    private static final int PICKUP_RADIUS = 100;

    private final Set<Integer> processingItems = Collections.synchronizedSet(new HashSet<>());

    public void tryPickupItem(Player player, ServerWrapper server) {
        ItemManager.getInstance().getItems().stream()
                .filter(item -> isPlayerNearItem(player, item))
                .filter(item -> !processingItems.contains(item.getId()))
                .findFirst()
                .ifPresent(item -> handleItemPickup(player, item, server));
    }

    private boolean isPlayerNearItem(Player player, Item item) {
        double distance = calculateDistance(player.getX(), player.getY(), item.getX(), item.getY());
        return distance <= PICKUP_RADIUS;
    }

    private double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private void handleItemPickup(Player player, Item item, ServerWrapper server) {
        try {
            if (!ItemManager.getInstance().removeItem(item)) {
                return;
            }

            server.sendToAllTCP(new ItemRemovePacket(item.getId()));

            if (item instanceof AmmoStack) {
                AmmoStack ammoStack = (AmmoStack) item;
                player.getInventory().addAmmo(ammoStack.getAmmo());
            } else if (item instanceof Consumable) {
                Consumable consumable = (Consumable) item;
                player.getInventory().addConsumable(consumable.getType());
            } else if (item instanceof RangedWeapon) {
                player.getInventory().getPrimaryWeapon().ifPresent(currentWeapon -> {
                    currentWeapon.drop(player.getX(), player.getY());
                    ItemManager.getInstance().addItem(currentWeapon);
                    server.sendToAllTCP(new ItemAddPacket(player.getX(), player.getY(),
                            SerializationUtility.serializeItem(currentWeapon)));
                });

                player.changeWeapon((RangedWeapon) item);
            }

            InventoryPacket inventoryPacket = new InventoryPacket(player.getId(),
                    InventorySerializer.serializeInventory(player.getInventory()));
            server.sendToTCP(player.getId(), inventoryPacket);

        } finally {
            processingItems.remove(item.getId());
        }
    }
}