package pewpew.smash.game.objects.weapon;

import java.awt.Shape;
import java.awt.image.BufferedImage;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.objects.MeleeWeapon;

public class Fist extends MeleeWeapon {

    public Fist(String name, String description, BufferedImage preview) {
        super(name, description, preview);
    }

    @Override
    public void updateServer() {

    }

    @Override
    public void render(Canvas canvas) {

    }

    @Override
    public void preview(Canvas canvas) {

    }

    @Override
    public void attack() {

    }

    @Override
    public Shape getHitbox() {
        return null;
    }
}
