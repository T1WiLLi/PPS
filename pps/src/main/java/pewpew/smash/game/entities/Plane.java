package pewpew.smash.game.entities;

import java.awt.Shape;
import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.Setter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.controls.Direction;
import pewpew.smash.engine.entities.MovableEntity;
import pewpew.smash.game.utils.ResourcesLoader;

// Plane will be use for both Airdrop events and Player spawn at the start of the game :)
public class Plane extends MovableEntity {

    private BufferedImage sprite;
    @Getter
    @Setter
    private float rotation;

    public Plane() {
        setDimensions(1600, 2000);
        setDirection(Direction.NONE);
        setSpeed(5);
        loadSprite();
        this.rotation = 0;
    }

    @Override
    public void updateClient() {
        move();
        System.out.println("COMING: [X=" + x + ", Y=" + y + "]");
    }

    @Override
    public void updateServer() {

    }

    @Override
    public void render(Canvas canvas) {
        canvas.rotate(rotation, getX() + getWidth() / 2, getY() + getHeight() / 2);
        canvas.setTransparency(0.5f);
        canvas.renderImage(this.sprite, getX(), getY(), getWidth(), getHeight());
        canvas.resetTransparency();
        canvas.resetRotation();
    }

    @Override
    public Shape getHitbox() {
        return null; // No hitbox for a plane
    }

    private void loadSprite() {
        this.sprite = ResourcesLoader.getImage(ResourcesLoader.ENTITY_SPRITE, "plane-blurry");
    }
}
