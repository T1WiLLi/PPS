package pewpew.smash.game.world.entities;

import java.awt.image.BufferedImage;
import java.util.List;

import lombok.Getter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.network.model.WorldEntityState;
import pewpew.smash.game.objects.Item;
import pewpew.smash.game.utils.ResourcesLoader;

public class Crate extends WorldBreakableStaticEntity {
    private BufferedImage[] sprites;
    @Getter
    private List<Item> lootTable;

    public Crate(WorldEntityType type, int x, int y, List<Item> lootTable) {
        super(type, x, y);
        loadSprites(type);
        this.lootTable = lootTable;
        this.health = type.getMaxHealth();
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderImage(sprites[getCurrentSprite()], x, y, width, height);
    }

    @Override
    public void applyState(WorldEntityState state) {
        this.health = state.getHealth();
    }

    @Override
    public boolean isDestroyed() {
        return health <= 0;
    }

    private void loadSprites(WorldEntityType type) {
        BufferedImage spriteSheet = ResourcesLoader.getImage(ResourcesLoader.ENTITY_SPRITE, type.getTextureName());
        final int spriteWidth = 384;
        final int spriteHeight = 384;
        int cols = spriteSheet.getWidth() / spriteWidth;
        sprites = new BufferedImage[cols];
        for (int x = 0; x < cols; x++) {
            sprites[x] = spriteSheet.getSubimage(x * spriteWidth, 0, spriteWidth, spriteHeight);
        }
    }

    private int getCurrentSprite() {
        if (health <= 0) {
            return sprites.length - 1;
        } else if (health >= getType().getMaxHealth()) {
            return 0;
        } else {
            int index = (int) ((getType().getMaxHealth() - health) / (getType().getMaxHealth() / (sprites.length - 1)));
            return Math.min(index, sprites.length - 1);
        }
    }
}
