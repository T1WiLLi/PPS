package pewpew.smash.game.event;

import lombok.Getter;
import pewpew.smash.game.entities.Plane;
import pewpew.smash.game.utils.HelpMethods;
import pewpew.smash.game.world.entities.Crate;
import pewpew.smash.game.world.entities.WorldEntityType;

public class AirdropEvent {

    @Getter
    private final Plane plane;
    private Crate crate;
    @Getter
    private boolean crateDropped;

    public AirdropEvent() {
        this.plane = HelpMethods.generatePlane();
    }

    public Crate createCrate(int x, int y) {
        this.crate = new Crate(WorldEntityType.AIR_DROP_CRATE, x, y, null); // TODO: ADD LOOT !
        this.crateDropped = true;
        return crate;
    }
}
