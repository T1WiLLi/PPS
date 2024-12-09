package pewpew.smash.game.network.packets;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostGamePacket extends BasePacket {
    private String winnerName;
    private List<String> allPlayers;
}
