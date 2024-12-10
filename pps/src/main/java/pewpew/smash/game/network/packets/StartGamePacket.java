package pewpew.smash.game.network.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StartGamePacket extends BasePacket {
    private String mode;
}
