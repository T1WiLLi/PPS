package pewpew.smash.game.world.entities;

import java.awt.image.BufferedImage;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.utils.ResourcesLoader;

public class Bush extends WorldStaticEntity {

    private final BufferedImage TRANSPARENT_IMAGE;
    private boolean isTransparent = false;

    public Bush(int x, int y) {
        super(WorldEntityType.BUSH, x, y);
        TRANSPARENT_IMAGE = ResourcesLoader.getImage(ResourcesLoader.ENTITY_SPRITE, "obstacle-bush-02");
    }

    @Override
    public void render(Canvas canvas) {
        if (isTransparent) {
            canvas.renderImage(TRANSPARENT_IMAGE, x, y, width, height);
        } else {
            super.render(canvas);
        }
    }

    public void isIn(Player player) {
        if (isColliding(player)) {
            isTransparent = true;
        } else {
            isTransparent = false;
        }
    }
}
