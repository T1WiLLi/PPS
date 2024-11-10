package pewpew.smash.game.network.packets;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pewpew.smash.game.network.model.SerializedItem;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class InventoryPacket extends BasePacket {
    private int playerID;
    private Map<String, SerializedItem> items;
}