package pewpew.smash.game.network.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StormEventCreationPacket extends BasePacket {
    private int centerX, centerY;
    private float initialRadius;
}
