package pewpew.smash.game.objects.special;

import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.Setter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.objects.Item;
import pewpew.smash.game.utils.ResourcesLoader;

public class AmmoStack extends Item {

    private final static BufferedImage preview = ResourcesLoader.getImage(ResourcesLoader.PREVIEW_PATH, "ammo");

    @Getter
    @Setter
    private int ammo; // Amount of ammo in the stack

    public AmmoStack(int id, String name, String description) {
        super(id, name, description, preview);
        setDimensions(24, 24);
        this.ammo = 0;
    }

    @Override
    public void render(Canvas canvas) {

    }
}
