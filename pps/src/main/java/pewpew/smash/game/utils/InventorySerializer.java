package pewpew.smash.game.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import pewpew.smash.game.entities.Inventory;
import pewpew.smash.game.network.model.SerializedItem;
import pewpew.smash.game.objects.Consumable;
import pewpew.smash.game.objects.ConsumableType;
import pewpew.smash.game.objects.Item;
import pewpew.smash.game.objects.ItemFactory;
import pewpew.smash.game.objects.RangedWeapon;
import pewpew.smash.game.objects.Weapon;
import pewpew.smash.game.objects.WeaponType;
import pewpew.smash.game.objects.special.AmmoStack;

public class InventorySerializer {

    public static Map<Integer, SerializedItem> serialize(Inventory<?, ?> inventory) {
        Map<Integer, SerializedItem> serialized = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            Optional<Item> itemOpt = inventory.getItem(i);
            if (itemOpt.isPresent()) {
                Item item = itemOpt.get();
                int quantity = inventory.getQuantity(i);
                SerializedItem serializedItem = serializeItem(item, quantity);
                if (serializedItem != null) {
                    serialized.put(i, serializedItem);
                }
            }
        }
        return serialized;
    }

    public static Inventory<?, ?> deserialize(Map<Integer, SerializedItem> serializedData) {
        Inventory<?, ?> inventory = new Inventory<>();

        for (Map.Entry<Integer, SerializedItem> entry : serializedData.entrySet()) {
            Item item = deserializeItem(entry.getValue());
            if (item != null) {
                inventory.addItem(item);
            }
        }
        return inventory;
    }

    private static SerializedItem serializeItem(Item item, int quantity) {
        if (item instanceof Weapon) {
            Weapon weapon = (Weapon) item;
            SerializedItem serializedItem = new SerializedItem(
                    SerializedItem.ItemType.WEAPON,
                    getWeaponType(weapon).name(),
                    1);

            if (weapon instanceof RangedWeapon) {
                RangedWeapon rangedWeapon = (RangedWeapon) weapon;
                serializedItem.extraData.put("currentAmmo", rangedWeapon.getCurrentAmmo());
            }
            return serializedItem;
        } else if (item instanceof Consumable) {
            return new SerializedItem(
                    SerializedItem.ItemType.CONSUMABLE,
                    getConsumableType((Consumable) item).name(),
                    quantity);
        } else if (item instanceof AmmoStack) {
            AmmoStack ammoStack = (AmmoStack) item;
            SerializedItem serializedItem = new SerializedItem(
                    SerializedItem.ItemType.AMMO_STACK,
                    "AMMO",
                    1);
            serializedItem.extraData.put("ammoCount", ammoStack.getAmmo());
            return serializedItem;
        }

        return null;
    }

    private static Item deserializeItem(SerializedItem serializedItem) {
        switch (serializedItem.type) {
            case WEAPON:
                WeaponType weaponType = WeaponType.valueOf(serializedItem.itemIdentifier);
                Weapon weapon = createWeapon(weaponType);
                if (weapon instanceof RangedWeapon && serializedItem.extraData.containsKey("currentAmmo")) {
                    ((RangedWeapon) weapon).setCurrentAmmo(serializedItem.extraData.get("currentAmmo"));
                }
                return weapon;

            case CONSUMABLE:
                ConsumableType consumableType = ConsumableType.valueOf(serializedItem.itemIdentifier);
                return createConsumable(consumableType);

            case AMMO_STACK:
                AmmoStack ammoStack = new AmmoStack("Ammo", "Stack of ammunition");
                if (serializedItem.extraData.containsKey("ammoCount")) {
                    ammoStack.setAmmo(serializedItem.extraData.get("ammoCount"));
                }
                return ammoStack;

            default:
                return null;
        }
    }

    private static WeaponType getWeaponType(Weapon weapon) {
        for (WeaponType type : WeaponType.values()) {
            if (weapon.getDamage() == type.getDamage() &&
                    weapon.getRange() == type.getRange() &&
                    weapon.getAttackSpeed() == type.getAttackSpeed()) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown weapon type");
    }

    private static ConsumableType getConsumableType(Consumable consumable) {
        for (ConsumableType type : ConsumableType.values()) {
            if (consumable.getName().equals(type.getName()) &&
                    consumable.getHealingAmount() == type.getHealAmount()) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown consumable type");
    }

    private static Weapon createWeapon(WeaponType type) {
        return ItemFactory.createItem(type);
    }

    private static Consumable createConsumable(ConsumableType type) {
        return ItemFactory.createItem(type);
    }
}