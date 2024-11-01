package pewpew.smash.game.objects.weapon;

import java.awt.Color;
import java.awt.image.BufferedImage;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.controls.MouseInput;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.objects.RangedWeapon;

public class AK47 extends RangedWeapon {

    public AK47(String name, String description, BufferedImage preview) {
        super(name, description, preview, 60, 8, 14, new Color(139, 69, 19));
    }

    @Override
    public void shoot() {
        if (canShoot()) {
            if (getOwner() != null) {
                spawnBullet((Player) getOwner());
            }
        }
    }

    @Override
    public void updateClient() {
    }

    @Override
    public void updateServer() {
        if (getOwner().getMouseInput() == MouseInput.LEFT_CLICK && canShoot()) {
            shoot();
        }
    }

    @Override
    protected void renderWeapon(Canvas canvas) {
        canvas.renderRectangle(-getWeaponLength() / 4, -getWeaponWidth() / 2, getWeaponLength(), getWeaponWidth(),
                getWeaponColor());
        renderHand(canvas, getWeaponLength() / 2 - getHandRadius(), 0);
        renderHand(canvas, -getWeaponLength() / 4 + getHandRadius() / 2, getWeaponWidth() * 2 - getHandRadius());
    }

    private void renderHand(Canvas canvas, int x, int y) {
        canvas.renderCircle(x - getHandRadius() / 2, y - getHandRadius() / 2, getHandRadius(), Color.BLACK);
        canvas.renderCircle(x - getHandRadius() / 2 + 1, y - getHandRadius() / 2 + 1, getHandRadius() - 2,
                new Color(229, 194, 152));
    }
}