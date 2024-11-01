package pewpew.smash.game.network.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BulletCreatePacket extends BasePacket {
    private int bulletID;
    private float x;
    private float y;
    private int ownerID;
}
