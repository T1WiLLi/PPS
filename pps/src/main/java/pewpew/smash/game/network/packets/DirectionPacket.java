package pewpew.smash.game.network.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pewpew.smash.engine.controls.Direction;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DirectionPacket extends BasePacket {
    private Direction direction;
    private float rotation;
}
