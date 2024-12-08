package pewpew.smash.game.network.packets;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LobbyStatePacket extends BasePacket {
    private List<String> playerNames;
    private int countdownRemaining; // 0 if not started;
}
