package pewpew.smash.game.objects.consumable;

import java.awt.image.BufferedImage;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.objects.Consumable;

public class Bandage extends Consumable {

    public Bandage(int id, String name, String description, BufferedImage preview) {
        super(id, name, description, preview);
    }

    @Override
    public void consume() {

    }

    @Override
    public void render(Canvas canvas) {

    }
}
