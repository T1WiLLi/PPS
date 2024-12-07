package pewpew.smash.game.entities;

import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.entities.StaticEntity;
import pewpew.smash.game.utils.ResourcesLoader;

public class Parachute extends StaticEntity {

    private BufferedImage sprite;
    private Timer shrink;

    public Parachute() {
        setDimensions(256, 256);
        sprite = ResourcesLoader.getImage(ResourcesLoader.ENTITY_SPRITE, "obstacle-chute-01");
        startShrinking();
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderImage(this.sprite, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public Shape getHitbox() {
        return null;
    }

    private void startShrinking() {
        this.shrink = new Timer();
        this.shrink.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (getWidth() > 0 && getHeight() > 0) {
                    int shrinkFactor = 2;

                    teleport(getX() + shrinkFactor / 2, getY() + shrinkFactor / 2);
                    setDimensions(getWidth() - shrinkFactor, getHeight() - shrinkFactor);
                } else {
                    cancel();
                    shrink = null;
                }
            }
        }, 0, 100);
    }
}
