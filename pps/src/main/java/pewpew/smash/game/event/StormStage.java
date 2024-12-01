package pewpew.smash.game.event;

import lombok.Getter;
import pewpew.smash.game.world.WorldGenerator;

@Getter
public enum StormStage {
    INITIAL(0.0f, 0, WorldGenerator.getWorldWidth() / 2),
    STAGE_1(0.5f, 2, calculateTargetRadius(0.8f)),
    STAGE_2(1.0f, 3, calculateTargetRadius(0.6f)),
    STAGE_3(1.5f, 4, calculateTargetRadius(0.4f)),
    STAGE_4(2.0f, 5, 500);

    private final float stormSpeed;
    private final int hitDamage;
    private final int targetRadius;

    private StormStage(float stormSpeed, int hitDamage, int targetRadius) {
        this.stormSpeed = stormSpeed;
        this.hitDamage = hitDamage;
        this.targetRadius = targetRadius;
    }

    public boolean hasNext() {
        return this.ordinal() < values().length - 1;
    }

    public StormStage next() {
        if (hasNext()) {
            return values()[this.ordinal() + 1];
        }
        return this;
    }

    private static int calculateTargetRadius(float shrinkFactor) {
        int maxRadius = WorldGenerator.getWorldWidth() / 2;
        return (int) (maxRadius * shrinkFactor);
    }
}