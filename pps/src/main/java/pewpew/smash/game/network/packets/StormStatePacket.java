package pewpew.smash.game.network.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pewpew.smash.game.network.model.StormState;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StormStatePacket extends BasePacket {
    private StormState state;
}
