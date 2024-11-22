package pewpew.smash.game.world.entities;

import java.awt.Shape;
import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.ToString;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.entities.StaticEntity;
import pewpew.smash.game.utils.ResourcesLoader;

@ToString(callSuper = true)
public class WorldStaticEntity extends StaticEntity {
    @Getter
    private final WorldEntityType type;
    private BufferedImage defaultSprite;
    private Shape hitbox;

    public WorldStaticEntity(WorldEntityType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = type.getWidth();
        this.height = type.getHeight();
        loadSprite();
        computeHitbox();
    }

    private void loadSprite() {
        defaultSprite = ResourcesLoader.getImage(ResourcesLoader.ENTITY_SPRITE, type.getTextureName());
    }

    private void computeHitbox() {
        this.hitbox = type.getHitbox().apply(this);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderImage(defaultSprite, x, y, width, height);
        renderHitbox(canvas);
    }

    @Override
    public Shape getHitbox() {
        return hitbox;
    }
}
