package pewpew.smash.game.entities.world;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.entities.StaticEntity;
import pewpew.smash.game.utils.ResourcesLoader;

public class Tree extends StaticEntity {

    private BufferedImage sprite;

    public Tree() {
        setDimensions(164, 164);
        loadSprite();
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderImage(sprite, getX(), getY(), getWidth(), getHeight());
        renderHitbox(canvas);
    }

    @Override
    public Shape getHitbox() {
        int logWidth = (int) (getWidth() * 0.30);
        int logHeight = (int) (getHeight() * 0.30);

        int centerX = getX() + (getWidth() - logWidth) / 2;
        int centerY = getY() + (getHeight() - logHeight) / 2;

        return new Ellipse2D.Float(centerX, centerY, logWidth, logHeight);
    }

    private void loadSprite() {
        sprite = ResourcesLoader.getImage(ResourcesLoader.ENTITY_SPRITE, "obstacle-tree-03sv");
    }
}
