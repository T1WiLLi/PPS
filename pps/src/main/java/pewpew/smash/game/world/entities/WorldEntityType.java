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
                        0,
                        (entity) -> {
                                int logWidth = (int) (entity.getWidth() * 0.30);
                                int logHeight = (int) (entity.getHeight() * 0.30);
                                int centerX = entity.getX() + (entity.getWidth() - logWidth) / 2;
                                int centerY = entity.getY() + (entity.getHeight() - logHeight) / 2;
                                return new Ellipse2D.Float(centerX, centerY, logWidth, logHeight);
                        }),

        TREE_DEAD(
                        "obstacle-tree-05c",
                        false,
                        164,
                        164,
                        0,
                        (entity) -> {
                                int logWidth = (int) (entity.getWidth() * 0.25);
                                int logHeight = (int) (entity.getHeight() * 0.25);
                                int centerX = entity.getX() + (entity.getWidth() - logWidth) / 2;
                                int centerY = entity.getY() + (entity.getHeight() - logHeight) / 2;
                                return new Ellipse2D.Float(centerX, centerY, logWidth, logHeight);
                        }),
        STONE(
                        "obstacle-stone-01",
                        false,
                        92,
                        92,
                        0,
                        (entity) -> new Ellipse2D.Float(entity.getX(), entity.getY(), entity.getWidth(),
                                        entity.getHeight())),
        STONE_GRASS(
                        "obstacle-stone-07",
                        false,
                        92,
                        92,
                        0,
                        (entity) -> new Ellipse2D.Float(entity.getX(), entity.getY(), entity.getWidth(),
                                        entity.getHeight())),
        CRATE(
                        "obstacle-crate-01-spritesheet",
                        true,
                        100,
                        100,
                        100,
                        (entity) -> new Rectangle(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight())),
        SOVIET_CRATE(
                        "obstacle-crate-02-spritesheet",
                        true,
                        76,
                        76,
                        100,
                        (entity) -> new Rectangle(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight())),
        AIR_DROP_CRATE(
                        "obstacle-airdrop-spritesheet-01",
                        true,
                        92,
                        92,
                        150,
                        (entity) -> new Ellipse2D.Float(entity.getX(), entity.getY(), entity.getWidth(),
                                        entity.getHeight())),
        AMMO_CRATE(
                        "obstacle-crate-23-spritesheet",
                        true,
                        48,
                        48,
                        50,
                        (entity) -> new Rectangle(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight())),
        BUSH(
                        "obstacle-bush-01",
                        false,
                        142,
                        142,
                        0,
                        (entity) -> {
                                int logWidth = (int) (entity.getWidth() * 0.85);
                                int logHeight = (int) (entity.getHeight() * 0.85);
                                int centerX = entity.getX() + (entity.getWidth() - logWidth) / 2;
                                int centerY = entity.getY() + (entity.getHeight() - logHeight) / 2;
                                return new Ellipse2D.Float(centerX, centerY, logWidth, logHeight);
                        });

        private final String textureName;
        private final boolean isBreakable;
        private final int width, height;
        private final int maxHealth;
        private final Function<StaticEntity, Shape> Hitbox;

        WorldEntityType(String textureName, boolean isBreakable, int width, int height, int maxHealth,
                        Function<StaticEntity, Shape> hitboxFunction) {
                this.textureName = textureName;
                this.isBreakable = isBreakable;
                this.width = width;
                this.height = height;
                this.maxHealth = maxHealth;
                this.Hitbox = hitboxFunction;
        }
}
