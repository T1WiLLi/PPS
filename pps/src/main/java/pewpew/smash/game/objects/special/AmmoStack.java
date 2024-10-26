package pewpew.smash.game.objects.special;

import lombok.Getter;
import lombok.Setter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.objects.Item;
import pewpew.smash.game.utils.ResourcesLoader;

public class AmmoStack extends Item {

    @Getter
    @Setter
    private int ammo; // Amount of ammo in the stack

    public AmmoStack(String name, String description) {
        super(name, description, ResourcesLoader.getImage(ResourcesLoader.PREVIEW_PATH, "ammo"));
    }

    @Override
    public void render(Canvas canvas) {

    }

    @Override
    public void preview(Canvas canvas) {

    }
}
