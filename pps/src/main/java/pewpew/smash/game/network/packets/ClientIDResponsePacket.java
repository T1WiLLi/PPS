package pewpew.smash.game.network.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ClientIDResponsePacket extends BasePacket {
    @Getter
    private int clientID;
}
