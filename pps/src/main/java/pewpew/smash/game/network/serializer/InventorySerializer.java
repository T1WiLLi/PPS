package pewpew.smash.game.network.serializer;

import java.util.HashMap;
import java.util.Map;

import pewpew.smash.game.entities.Inventory;
import pewpew.smash.game.network.model.SerializedItem;
import pewpew.smash.game.objects.ConsumableType;
import pewpew.smash.game.objects.Item;
import pewpew.smash.game.objects.ItemFactory;
import pewpew.smash.game.objects.RangedWeapon;
import pewpew.smash.game.objects.special.Scope;

public class InventorySerializer {

    public static Map<String, SerializedItem> serializeInventory(Inventory inventory) {
        Map<String, SerializedItem> serializedItems = new HashMap<>();

        inventory.getConsumables().forEach((type, quantity) -> {
            Item consumableItem = ItemFactory.createItem(type);
            SerializedItem serializedItem = SerializationUtility.serializeItem(consumableItem);
            serializedItem.setQuantity(quantity);
            serializedItems.put(type.name(), serializedItem);
        });

        SerializedItem ammoSerialized = SerializationUtility.serializeItem(inventory.getAmmoStack());
        serializedItems.put("ammo_stack", ammoSerialized);

        inventory.getPrimaryWeapon().ifPresent(weapon -> {
            SerializedItem weaponSerialized = SerializationUtility.serializeItem(weapon);
            serializedItems.put(weapon.getType().name(), weaponSerialized);
        });

        SerializedItem scopeSerialized = SerializationUtility.serializeItem(inventory.getScope());
        serializedItems.put("scope", scopeSerialized);

        return serializedItems;
    }

    public static void deserializeInventory(Map<String, SerializedItem> serializedItems, Inventory inventory) {
        inventory.clearInventory();

        serializedItems.forEach((key, serializedItem) -> {
            Item item = SerializationUtility.deserializeItem(serializedItem);

            switch (serializedItem.getType()) {
                case CONSUMABLE -> {
                    ConsumableType consumableType = ConsumableType
                            .valueOf(serializedItem.getItemIdentifier().toUpperCase());
                    int quantity = serializedItem.getQuantity();
                    inventory.getConsumables().put(consumableType, quantity);
                }
                case AMMO_STACK -> {
                    inventory.addAmmo(serializedItem.getQuantity());
                }
                case WEAPON -> {
                    inventory.changeWeapon((RangedWeapon) item);
                }
                case SCOPE -> {
                    inventory.setScope((Scope) item);
                }
                default -> throw new IllegalArgumentException("Unknown item type: " + key);
            }
        });
    }
}