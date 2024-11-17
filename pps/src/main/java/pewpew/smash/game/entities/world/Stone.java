package pewpew.smash.game.entities.world;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.entities.StaticEntity;
import pewpew.smash.game.utils.ResourcesLoader;

public class Stone extends StaticEntity {

    private BufferedImage sprite;

    public Stone() {
        setDimensions(92, 92);
        loadSprite();
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderImage(sprite, getX(), getY(), getWidth(), getHeight());
        renderHitbox(canvas);
    }

    @Override
    public Shape getHitbox() {
        return new Ellipse2D.Float(getX(), getY(), getWidth(), getHeight());
    }

    private void loadSprite() {
        sprite = ResourcesLoader.getImage(ResourcesLoader.ENTITY_SPRITE, "obstacle-stone-01");
    }
}
