package pewpew.smash.game.event;

import java.util.Random;

import lombok.Getter;
import pewpew.smash.game.world.WorldGenerator;

@Getter
public enum StormStage {
    STAGE_1(0.5f, 2, generateTargetRadius(1.0f)),
    STAGE_2(1.0f, 3, generateTargetRadius(0.8f)),
    STAGE_3(2.0f, 4, generateTargetRadius(0.6f)),
    STAGE_4(3.0f, 5, generateTargetRadius(0.4f));

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

    public static int generateTargetRadius(float shrinkFactor) {
        Random random = new Random();
        int worldWidth = WorldGenerator.getWorldWidth();
        int worldHeight = WorldGenerator.getWorldHeight();

        int maxRadius = Math.min(worldWidth, worldHeight) / 2;
        int minRadius = (int) (maxRadius * shrinkFactor);

        return random.nextInt(maxRadius - minRadius + 1) + minRadius;
    }
}
