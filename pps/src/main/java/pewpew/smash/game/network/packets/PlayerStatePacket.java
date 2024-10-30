package pewpew.smash.game.network.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pewpew.smash.game.network.model.PlayerState;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PlayerStatePacket extends BasePacket {
    private PlayerState state;
}
