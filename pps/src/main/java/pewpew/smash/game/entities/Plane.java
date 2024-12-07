package pewpew.smash.game.entities;

import java.awt.Color;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.controls.Direction;
import pewpew.smash.engine.entities.MovableEntity;
import pewpew.smash.game.utils.ResourcesLoader;

// Plane will be use for both Airdrop events and Player spawn at the start of the game :)
// TODO: ADD PLANE TRAIL EFFECTS
public class Plane extends MovableEntity {

    private List<int[]> planeTrail = new ArrayList<>();

    private BufferedImage sprite;
    @Getter
    @Setter
    private float rotation;

    public Plane() {
        setDimensions(1600, 2000);
        setDirection(Direction.NONE);
        setSpeed(4);
        loadSprite();
        this.rotation = 0;
    }

    @Override
    public void updateServer() {
        move();
    }

    @Override
    public void updateClient() {
        move();
        planeTrail.add(new int[] { x, y });
        if (planeTrail.size() > 10000)
            planeTrail.remove(0);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.rotate(rotation, x + getWidth() / 2, y + getHeight() / 2);
        canvas.setTransparency(0.5f);
        canvas.renderImage(this.sprite, x, y, getWidth(), getHeight());
        canvas.resetTransparency();
        canvas.resetRotation();

        for (int[] point : planeTrail) {
            canvas.renderRectangle(point[0], point[1], 4, 4, Color.red);
        }
    }

    @Override
    public Shape getHitbox() {
        return null; // No hitbox for a plane
    }

    private void loadSprite() {
        this.sprite = ResourcesLoader.getImage(ResourcesLoader.ENTITY_SPRITE, "plane-blurry");
    }
}
