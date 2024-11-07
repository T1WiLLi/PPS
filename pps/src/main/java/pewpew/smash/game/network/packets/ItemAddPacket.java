package pewpew.smash.game.network.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pewpew.smash.game.network.model.SerializedItem;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ItemAddPacket extends BasePacket {
    private int x, y;
    private SerializedItem serializedItem;
}
