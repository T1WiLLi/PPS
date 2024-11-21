package pewpew.smash.game.network.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pewpew.smash.game.network.model.SerializedWorldStaticEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class WorldEntityAddPacket extends BasePacket {
    private SerializedWorldStaticEntity entity;
}
