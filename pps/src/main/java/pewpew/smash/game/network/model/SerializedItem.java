package pewpew.smash.game.network.model;

import java.util.Map;
import java.util.HashMap;

public class SerializedItem {
    public enum ItemType {
        CONSUMABLE,
        WEAPON,
        AMMO_STACK
    }

    public ItemType type;
    public String itemIdentifier;
    public int quantity;
    public Map<String, Integer> extraData;

    public SerializedItem(ItemType type, String itemIdentifier, int quantity) {
        this.type = type;
        this.itemIdentifier = itemIdentifier;
        this.quantity = quantity;
        this.extraData = new HashMap<>();
    }
}