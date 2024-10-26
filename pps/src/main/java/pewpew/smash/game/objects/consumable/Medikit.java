package pewpew.smash.game.objects.consumable;

import java.awt.image.BufferedImage;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.objects.Consumable;

public class Medikit extends Consumable {

    public Medikit(String name, String description, BufferedImage preview) {
        super(name, description, preview);
    }

    @Override
    public void consume() {

    }

    @Override
    public void render(Canvas canvas) {

    }

    @Override
    public void preview(Canvas canvas) {

    }
}
