package pewpew.smash.game.network.packets;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pewpew.smash.engine.controls.Direction;

@NoArgsConstructor
public class DirectionPacket extends BasePacket {
    @Getter
    private Direction direction;

    @Getter
    private float rotation;

    public DirectionPacket(Direction direction, float rotation) {
        this.direction = direction;
        this.rotation = rotation;
    }
}
