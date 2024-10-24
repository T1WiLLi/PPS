package pewpew.smash.game.network.packets;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PlayerLeftPacket extends BasePacket {
    private int id;

    public PlayerLeftPacket(int id) {
        if (id <= 0 || id >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Invalid player ID");
        }
        this.id = id;
    }
}
