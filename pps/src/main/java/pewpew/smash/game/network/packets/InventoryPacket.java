package pewpew.smash.game.network.packets;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pewpew.smash.game.network.model.SerializedItem;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class InventoryPacket extends BasePacket {
    private Map<Integer, SerializedItem> items;
}
