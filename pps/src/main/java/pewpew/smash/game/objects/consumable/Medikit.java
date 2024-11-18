package pewpew.smash.game.objects.consumable;

import java.awt.image.BufferedImage;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.objects.Consumable;

public class Medikit extends Consumable {

    public Medikit(int id, String name, String description, BufferedImage preview) {
        super(id, name, description, preview);
    }

    @Override
    public void consume() {
        if (getOwner() != null) {
            int newHealth = Math.min(getOwner().getHealth() + healingAmount, 100);
            getOwner().setHealth(newHealth);
        }
    }

    @Override
    public void render(Canvas canvas) {

    }
}
