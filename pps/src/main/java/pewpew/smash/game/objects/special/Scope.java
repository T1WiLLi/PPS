package pewpew.smash.game.objects.special;

import java.awt.image.BufferedImage;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.Camera;
import pewpew.smash.game.objects.Item;

public class Scope extends Item {

    private float zoomValue;

    public Scope(String name, String description, float zoomValue, BufferedImage preview) {
        super(name, description, preview);
    }

    @Override
    public void render(Canvas canvas) {

    }

    @Override
    public void preview(Canvas canvas) {

    }

    public void applyZoom(Camera camera) {
        camera.setZoom(this.zoomValue);
    }
}
