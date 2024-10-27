package pewpew.smash.game.network.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pewpew.smash.engine.controls.MouseInput;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MouseActionPacket extends BasePacket {
    private int playerID;
    private MouseInput mouseInput;
}
