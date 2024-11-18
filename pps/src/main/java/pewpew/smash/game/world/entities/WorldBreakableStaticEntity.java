package pewpew.smash.game.world.entities;

import pewpew.smash.game.network.model.WorldEntityState;

public abstract class WorldBreakableStaticEntity extends WorldStaticEntity {

    public abstract void onBreak();

    public abstract void applyState(WorldEntityState state);

    public WorldBreakableStaticEntity(WorldEntityType type, int x, int y) {
        super(type, x, y);
    }
}
