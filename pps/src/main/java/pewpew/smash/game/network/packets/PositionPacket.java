package pewpew.smash.game.network.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PositionPacket extends BasePacket {
    private int id;
    private int x;
    private int y;
}
