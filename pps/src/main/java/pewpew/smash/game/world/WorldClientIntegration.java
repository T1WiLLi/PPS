package pewpew.smash.game.world;

import java.awt.image.BufferedImage;

import lombok.Getter;

public class WorldClientIntegration {
    private static WorldClientIntegration instance;

    @Getter
    private BufferedImage worldImage;

    public static WorldClientIntegration getInstance() {
        if (instance == null) {
            instance = new WorldClientIntegration();
        }
        return instance;
    }

    public boolean isWorldLoaded() {
        return worldImage != null;
    }

    public void buildImage(byte[][] worldData) {
        this.worldImage = WorldGenerator.getWorldImage(worldData);
    }

    public void reset() {
        this.worldImage = null;
    }
}
