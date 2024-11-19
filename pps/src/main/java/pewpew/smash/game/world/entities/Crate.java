package pewpew.smash.game.world.entities;

import java.awt.image.BufferedImage;
import java.util.List;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.network.model.WorldEntityState;
import pewpew.smash.game.objects.Item;
import pewpew.smash.game.utils.ResourcesLoader;

public class Crate extends WorldBreakableStaticEntity {
    private BufferedImage[] sprites;
    private int currentSpriteIndex;
    private List<Item> lootTable;

    public Crate(int x, int y, List<Item> lootTable) {
        super(WorldEntityType.CRATE, x, y);
        loadSprites();
        this.currentSpriteIndex = 0;
        this.lootTable = lootTable;
        this.health = 100;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderImage(sprites[getCurrentSprite()], x, y, width, height);
        renderHitbox(canvas);
    }

    public void onBreak() {

    }

    public void applyState(WorldEntityState state) {
        this.health = state.getHealth();
    }

    private void loadSprites() {
        BufferedImage spriteSheet = ResourcesLoader.getImage(ResourcesLoader.ENTITY_SPRITE,
                "obstacle-crate-spritesheet");

        final int spriteWidth = 384;
        final int spriteHeight = 384;

        int cols = spriteSheet.getWidth() / spriteWidth;
        int rows = spriteSheet.getHeight() / spriteHeight;
        sprites = new BufferedImage[cols * rows];

        int index = 0;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                sprites[index++] = spriteSheet.getSubimage(x * spriteWidth, y * spriteHeight, spriteWidth,
                        spriteHeight);
            }
        }
    }

    private int getCurrentSprite() {
        if (health <= 0) {
            return 7;
        } else if (health >= 100) {
            return 0;
        } else {
            int index = (int) ((100 - health) / 12.5);
            return Math.min(index, 7);
        }
    }
}
