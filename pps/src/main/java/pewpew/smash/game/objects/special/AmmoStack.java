package pewpew.smash.game.objects.special;

import java.awt.image.BufferedImage;

import lombok.Getter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.objects.Item;

public class AmmoStack extends Item {

    @Getter
    private int ammo; // Amount of ammo in the stack

    public AmmoStack(String name, String description, BufferedImage preview) {
        super(name, description, preview);
    }

    @Override
    public void render(Canvas canvas) {

    }

    @Override
    public void preview(Canvas canvas) {

    }
}
