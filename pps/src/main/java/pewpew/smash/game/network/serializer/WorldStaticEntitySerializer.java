package pewpew.smash.game.network.serializer;

import pewpew.smash.game.network.model.SerializedWorldStaticEntity;
import pewpew.smash.game.world.entities.Bush;
import pewpew.smash.game.world.entities.Crate;
import pewpew.smash.game.world.entities.WorldStaticEntity;

public class WorldStaticEntitySerializer {
    public static SerializedWorldStaticEntity serialize(WorldStaticEntity entity) {
        return new SerializedWorldStaticEntity(entity.getId(), entity.getType(), entity.getX(), entity.getY());
    }

    public static WorldStaticEntity deserialize(SerializedWorldStaticEntity serialized) {
        int x = serialized.getX();
        int y = serialized.getY();
        WorldStaticEntity entity = switch (serialized.getType()) {
            case BUSH -> new Bush(x, y);
            case CRATE -> new Crate(x, y, null);
            default -> new WorldStaticEntity(serialized.getType(), x, y);
        };
        entity.setId(serialized.getId());
        return entity;
    }
}
