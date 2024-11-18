package pewpew.smash.game.network.server;

import java.util.HashMap;
import java.util.Map;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.ItemManager;
import pewpew.smash.game.network.packets.InventoryPacket;
import pewpew.smash.game.network.packets.ItemAddPacket;
import pewpew.smash.game.network.packets.ItemRemovePacket;
import pewpew.smash.game.network.serializer.InventorySerializer;
import pewpew.smash.game.network.serializer.SerializationUtility;
import pewpew.smash.game.network.serializer.WeaponStateSerializer;
import pewpew.smash.game.objects.Consumable;
import pewpew.smash.game.objects.Item;
import pewpew.smash.game.objects.RangedWeapon;
import pewpew.smash.game.objects.SpecialType;
import pewpew.smash.game.objects.Weapon;
import pewpew.smash.game.objects.special.AmmoStack;
import pewpew.smash.game.objects.special.Scope;

public class ServerItemUpdater {
    private static final int PICKUP_RADIUS = 100;
    private static final long PICKUP_COOLDOWN = 500; // ms

    private final Map<Integer, Boolean> pickupInProgress = new HashMap<>();
    private final Map<Integer, Long> lastPickupTime = new HashMap<>();

    public void tryPickupItem(Player player, ServerWrapper server) {
        int playerID = player.getId();
        if (pickupInProgress.getOrDefault(playerID, false) || isOnCooldown(playerID)) {
            return;
        }

        ItemManager.getInstance(true).getItems().stream()
                .filter(item -> isPlayerNearItem(player, item))
                .findFirst()
                .ifPresent(item -> {
                    pickupInProgress.put(player.getId(), true);
                    handleItemPickup(player, item, server);
                    pickupInProgress.put(player.getId(), false);
                    lastPickupTime.put(playerID, System.currentTimeMillis());
                });
    }

    private boolean isPlayerNearItem(Player player, Item item) {
        double distance = calculateDistance(player.getX(), player.getY(), item.getX(), item.getY());
        return distance <= PICKUP_RADIUS;
    }

    private double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private void handleItemPickup(Player player, Item item, ServerWrapper server) {
        if (!ItemManager.getInstance(true).removeItem(item)) {
            return;
        }

        server.sendToAllTCP(new ItemRemovePacket(item.getId()));

        if (item instanceof AmmoStack) {
            AmmoStack ammoStack = (AmmoStack) item;
            player.getInventory().addAmmo(ammoStack.getAmmo());
        } else if (item instanceof Scope) {
            Scope currentscope = player.getScope();
            if (currentscope.getZoomValue() != SpecialType.SCOPE_X1.getValue()) {
                currentscope.drop();
                ItemManager.getInstance(true).addItem(currentscope);
                server.sendToAllTCP(
                        new ItemAddPacket(player.getX(), player.getY(),
                                SerializationUtility.serializeItem(currentscope)));
            }
            item.pickup(player);
            player.getInventory().setScope((Scope) item);
        } else if (item instanceof Consumable) {
            Consumable consumable = (Consumable) item;
            player.getInventory().addConsumable(consumable.getType());
        } else if (item instanceof RangedWeapon) {
            player.getInventory().getPrimaryWeapon().ifPresent(currentWeapon -> {
                System.out.println("The weapon added has ammo: " + currentWeapon.getCurrentAmmo());
                currentWeapon.drop();
                ItemManager.getInstance(true).addItem(currentWeapon);
                server.sendToAllTCP(new ItemAddPacket(player.getX(), player.getY(),
                        SerializationUtility.serializeItem(currentWeapon)));
            });
            System.out.println("ID of pickup item: " + item.getId());
            player.changeWeapon((RangedWeapon) item);
            server.sendToAllTCP(WeaponStateSerializer.serializeWeaponState((Weapon) item));
        }

        InventoryPacket inventoryPacket = new InventoryPacket(player.getId(),
                InventorySerializer.serializeInventory(player.getInventory()));
        server.sendToTCP(player.getId(), inventoryPacket);
    }

    private boolean isOnCooldown(int playerID) {
        long currentTime = System.currentTimeMillis();
        return lastPickupTime.containsKey(playerID) && (currentTime - lastPickupTime.get(playerID)) < PICKUP_COOLDOWN;
    }
}