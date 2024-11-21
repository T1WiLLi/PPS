package pewpew.smash.game.network.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pewpew.smash.game.world.entities.WorldEntityType;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SerializedWorldStaticEntity {
    private int id;
    private WorldEntityType type;
    private int x, y;
}
