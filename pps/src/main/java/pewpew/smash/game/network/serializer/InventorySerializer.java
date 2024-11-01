package pewpew.smash.game.network.serializer;

import java.util.HashMap;
import java.util.Map;

import pewpew.smash.game.entities.Inventory;
import pewpew.smash.game.network.model.SerializedItem;
import pewpew.smash.game.objects.ConsumableType;
import pewpew.smash.game.objects.RangedWeapon;
import pewpew.smash.game.objects.ItemFactory;
import pewpew.smash.game.objects.WeaponType;

public class InventorySerializer {

    public static Map<String, SerializedItem> serialize(Inventory inventory) {
        Map<String, SerializedItem> serializedInventory = new HashMap<>();

        inventory.getPrimaryWeapon().ifPresent(weapon -> {
            SerializedItem weaponItem = new SerializedItem(
                    SerializedItem.ItemType.WEAPON,
                    weapon.getClass().getSimpleName(),
                    1);
            weaponItem.extraData.put("currentAmmo", weapon.getCurrentAmmo());
            serializedInventory.put("primaryWeapon", weaponItem);
        });

        for (Map.Entry<ConsumableType, Integer> entry : inventory.getConsumables().entrySet()) {
            SerializedItem consumableItem = new SerializedItem(
                    SerializedItem.ItemType.CONSUMABLE,
                    entry.getKey().name(),
                    entry.getValue());
            serializedInventory.put("consumable_" + entry.getKey().name(), consumableItem);
        }

        SerializedItem ammoItem = new SerializedItem(
                SerializedItem.ItemType.AMMO_STACK,
                "AMMO",
                inventory.getAmmoCount());
        serializedInventory.put("ammoStack", ammoItem);

        return serializedInventory;
    }

    public static Inventory deserialize(Map<String, SerializedItem> serializedData) {
        Inventory inventory = new Inventory();

        if (serializedData.containsKey("primaryWeapon")) {
            SerializedItem weaponItem = serializedData.get("primaryWeapon");
            RangedWeapon weapon = (RangedWeapon) ItemFactory.createItem(WeaponType.valueOf(weaponItem.itemIdentifier));
            weapon.setCurrentAmmo(weaponItem.extraData.getOrDefault("currentAmmo", 0));
            inventory.changeWeapon(weapon);
        }

        for (Map.Entry<String, SerializedItem> entry : serializedData.entrySet()) {
            if (entry.getKey().startsWith("consumable_")) {
                ConsumableType consumableType = ConsumableType.valueOf(entry.getValue().itemIdentifier);
                int quantity = entry.getValue().quantity;
                for (int i = 0; i < quantity; i++) {
                    inventory.addConsumable(consumableType);
                }
            }
        }

        if (serializedData.containsKey("ammoStack")) {
            SerializedItem ammoItem = serializedData.get("ammoStack");
            inventory.addAmmo(ammoItem.quantity);
        }

        return inventory;
    }
}
