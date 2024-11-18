package pewpew.smash.game.network.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pewpew.smash.game.network.model.WorldEntityState;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class WorldEntityStatePacket extends BasePacket {
    private WorldEntityState state;
}
