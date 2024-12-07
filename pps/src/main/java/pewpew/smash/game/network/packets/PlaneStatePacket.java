package pewpew.smash.game.network.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pewpew.smash.engine.controls.Direction;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PlaneStatePacket extends BasePacket {
    private int x, y;
    private Direction dir;
    private float r;
}
