package pewpew.smash.game.network.serializer;

import pewpew.smash.game.objects.*;
import pewpew.smash.game.objects.special.*;
import pewpew.smash.game.network.model.SerializedItem;
import pewpew.smash.game.network.packets.WeaponStatePacket;
import java.util.HashMap;
import java.util.Map;

public class SerializationUtility {

    public static SerializedItem serializeItem(Item item) {
        SerializedItem.ItemType type;
        String itemIdentifier = item.getClass().getSimpleName();
        int quantity = 1;
        Map<String, Integer> extraData = new HashMap<>();

        if (item instanceof Consumable) {
            type = SerializedItem.ItemType.CONSUMABLE;
            quantity = 1;
        } else if (item instanceof RangedWeapon || item instanceof MeleeWeapon) {
            type = SerializedItem.ItemType.WEAPON;
            Weapon weapon = (Weapon) item;

            WeaponStatePacket weaponStatePacket = WeaponStateSerializer.serializeWeaponState(weapon);
            WeaponType weaponType = weaponStatePacket.getWeaponType();

            itemIdentifier = (weaponType != null) ? weaponType.name() : "UNKNOWN_WEAPON";

            weaponStatePacket.getWeaponStateData().forEach((key, value) -> {
                if (value instanceof Integer) {
                    extraData.put(key, (Integer) value);
                }
            });
        } else if (item instanceof AmmoStack) {
            type = SerializedItem.ItemType.AMMO_STACK;
            quantity = ((AmmoStack) item).getAmmo();
        } else if (item instanceof Scope) {
            type = SerializedItem.ItemType.SCOPE;
            Scope scope = (Scope) item;
            itemIdentifier = scope.getName();
            extraData.put("zoomValue", (int) (scope.getZoomValue() * 100));
        } else {
            System.err.println("Unknown item type: " + item.getClass().getName());
            throw new IllegalArgumentException("Unknown item type");
        }

        SerializedItem serializedItem = new SerializedItem(type, itemIdentifier, quantity);
        serializedItem.extraData.putAll(extraData);
        return serializedItem;
    }

    public static Item deserializeItem(SerializedItem serializedItem) {
        Item item;
        switch (serializedItem.type) {
            case CONSUMABLE:
                item = ItemFactory.createItem(ConsumableType.valueOf(serializedItem.itemIdentifier.toUpperCase()));
                break;
            case WEAPON:
                WeaponType weaponType = WeaponType.valueOf(serializedItem.itemIdentifier.toUpperCase());
                item = ItemFactory.createItem(weaponType);
                if (item instanceof RangedWeapon rangedWeapon) {
                    rangedWeapon.setCurrentAmmo(serializedItem.extraData.getOrDefault("currentAmmo", 0));
                }
                break;
            case AMMO_STACK:
                item = ItemFactory.createAmmoStack();
                ((AmmoStack) item).setAmmo(serializedItem.quantity);
                break;
            case SCOPE:
                SpecialType specialType = SpecialType.getScopeFromIdentifier(serializedItem.itemIdentifier);
                item = ItemFactory.createItem(specialType);
                break;
            default:
                throw new IllegalArgumentException("Unknown SerializedItem type");
        }
        return item;
    }
}
