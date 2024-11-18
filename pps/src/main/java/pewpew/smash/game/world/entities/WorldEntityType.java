package pewpew.smash.game.world.entities;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.function.Function;

import lombok.Getter;
import pewpew.smash.engine.entities.StaticEntity;

@Getter
public enum WorldEntityType {
    TREE(
            "obstacle-tree-03sv",
            false,
            164,
            164,
            (entity) -> {
                int logWidth = (int) (entity.getWidth() * 0.30);
                int logHeight = (int) (entity.getHeight() * 0.30);
                int centerX = entity.getX() + (entity.getWidth() - logWidth) / 2;
                int centerY = entity.getY() + (entity.getHeight() - logHeight) / 2;
                return new Ellipse2D.Float(centerX, centerY, logWidth, logHeight);
            }),

    STONE(
            "obstacle-stone-01",
            false,
            92,
            92,
            (entity) -> new Ellipse2D.Float(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight())),
    CRATE(
            "obstacle-crate-01",
            true,
            100,
            100,
            (entity) -> new Rectangle(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight()));

    private final String textureName;
    private final boolean isBreakable;
    private final int width, height;
    private final Function<StaticEntity, Shape> Hitbox;

    WorldEntityType(String textureName, boolean isBreakable, int width, int height,
            Function<StaticEntity, Shape> hitboxFunction) {
        this.textureName = textureName;
        this.isBreakable = isBreakable;
        this.width = width;
        this.height = height;
        this.Hitbox = hitboxFunction;
    }
}
