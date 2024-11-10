package pewpew.smash.game.network.model;

import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@NoArgsConstructor
@Getter
public class SerializedItem {
    public enum ItemType {
        CONSUMABLE,
        WEAPON,
        AMMO_STACK,
        SCOPE;
    }

    private ItemType type;
    private String itemIdentifier;
    @Setter
    private int quantity;
    private Map<String, Integer> extraData;

    public SerializedItem(ItemType type, String itemIdentifier, int quantity) {
        this.type = type;
        this.itemIdentifier = itemIdentifier;
        this.quantity = quantity;
        this.extraData = new HashMap<>();
    }
}