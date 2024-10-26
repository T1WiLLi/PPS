package pewpew.smash.game.network.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pewpew.smash.engine.controls.MouseInput;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MouseInputPacket extends BasePacket {
    private MouseInput input;
}
