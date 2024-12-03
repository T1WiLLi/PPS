package pewpew.smash.game.event;

import lombok.Getter;
import pewpew.smash.game.world.WorldGenerator;

@Getter
public enum StormStage {
    PRE_INITIAL(10 * 1000, 0.0f, 0, WorldGenerator.getWorldWidth()), // Start at 30 secondes
    INITIAL(30 * 1000, 0.5f, 1, WorldGenerator.getWorldWidth() / 2), // Start at 1min
    STAGE_1(180 * 1000, 1.0f, 1, calculateTargetRadius(0.8f)), // Start at 3min
    STAGE_2(240 * 1000, 1.5f, 2, calculateTargetRadius(0.6f)), // Start at 4min
    STAGE_3(300 * 1000, 2.0f, 3, calculateTargetRadius(0.4f)), // Start at 5min
    FINAL(390 * 1000, 2.5f, 5, 500); // Start at 6min 30

    private final long startTime;
    private final float stormSpeed;
    private final int hitDamage;
    private final int targetRadius;

    StormStage(long startTime, float stormSpeed, int hitDamage, int targetRadius) {
        this.startTime = startTime;
        this.stormSpeed = stormSpeed;
        this.hitDamage = hitDamage;
        this.targetRadius = targetRadius;
    }

    public boolean hasNext() {
        return this.ordinal() < values().length - 1;
    }

    public StormStage next() {
        return hasNext() ? values()[ordinal() + 1] : this;
    }

    private static int calculateTargetRadius(float shrinkFactor) {
        int maxRadius = WorldGenerator.getWorldWidth() / 2;
        return (int) (maxRadius * shrinkFactor);
    }
}
