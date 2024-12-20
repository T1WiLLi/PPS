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
    private float r; // Rotation degree (0-360)
    private byte d; // Direction (0-8) 0 being Direction.NONE
}
