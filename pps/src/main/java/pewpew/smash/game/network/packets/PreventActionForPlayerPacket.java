package pewpew.smash.game.network.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PreventActionForPlayerPacket extends BasePacket {
    private int playerID;
    private boolean isHealing;
    private char c;
}
