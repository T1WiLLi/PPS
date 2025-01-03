package pewpew.smash.game.network.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PlayerDeathPacket extends BasePacket {
    private int deadPlayerID;
    private int killerPlayerID;
}
