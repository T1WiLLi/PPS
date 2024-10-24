package pewpew.smash.game.network.packets;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pewpew.smash.engine.controls.Direction;

@NoArgsConstructor
@Getter
public class DirectionPacket extends BasePacket {
    private Direction direction;
    private float rotation;

    public DirectionPacket(Direction direction, float rotation) {
        this.direction = direction;
        this.rotation = rotation;
    }
}
