package pewpew.smash.game.network.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class WorldDataPacket extends BasePacket {
    @Getter
    private byte[][] worldData;
}
