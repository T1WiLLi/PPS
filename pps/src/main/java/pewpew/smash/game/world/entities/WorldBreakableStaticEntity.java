package pewpew.smash.game.world.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pewpew.smash.game.network.model.WorldEntityState;

@Getter
@ToString(callSuper = true)
public abstract class WorldBreakableStaticEntity extends WorldStaticEntity {

    @Setter
    protected int health;

    public abstract void onBreak();

    public abstract void applyState(WorldEntityState state);

    public WorldBreakableStaticEntity(WorldEntityType type, int x, int y) {
        super(type, x, y);
    }
}
