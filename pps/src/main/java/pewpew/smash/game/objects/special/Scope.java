package pewpew.smash.game.objects.special;

import java.awt.image.BufferedImage;

import lombok.Getter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.objects.Item;

public class Scope extends Item {

    @Getter
    private float zoomValue;

    public Scope(int id, String name, String description, float zoomValue, BufferedImage preview) {
        super(id, name, description, preview);
        this.zoomValue = zoomValue;
    }

    @Override
    public void render(Canvas canvas) {

    }
}
