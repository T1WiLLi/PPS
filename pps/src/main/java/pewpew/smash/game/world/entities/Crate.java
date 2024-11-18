package pewpew.smash.game.world.entities;

import java.awt.image.BufferedImage;
import java.util.List;

import pewpew.smash.game.objects.Item;
import pewpew.smash.game.utils.ResourcesLoader;

public class Crate extends WorldStaticEntity {
    private int health;
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

    private void loadSprites() {
        String[] spriteNames = new String[] { getType().getTextureName() }; // TODO: Add more sprite names
        sprites = new BufferedImage[spriteNames.length];
        for (int i = 0; i < spriteNames.length; i++) {
            sprites[i] = ResourcesLoader.getImage(ResourcesLoader.ENTITY_SPRITE, spriteNames[i]);
        }
    }
}
