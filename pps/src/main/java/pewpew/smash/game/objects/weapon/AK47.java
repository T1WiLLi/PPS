package pewpew.smash.game.objects.weapon;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import lombok.Getter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.controls.MouseInput;
import pewpew.smash.game.entities.Bullet;
import pewpew.smash.game.network.server.ServerBulletTracker;
import pewpew.smash.game.objects.RangedWeapon;

public class AK47 extends RangedWeapon {
    private long lastShotTime = 0;
    @Getter
    private static final int WEAPON_LENGTH = 60;
    private static final int WEAPON_WIDTH = 8;
    private static final int HAND_RADIUS = 14;
    private static final Color WEAPON_COLOR = new Color(139, 69, 19);

    public AK47(String name, String description, BufferedImage preview) {
        super(name, description, preview, WEAPON_LENGTH);
    }

    @Override
    public void shoot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime >= (getAttackSpeed() * 1000) && canShoot()) {
            if (getOwner() != null) {
                Bullet bullet = new Bullet(getOwner());
                ServerBulletTracker.getInstance().addBullet(bullet);
                lastShotTime = currentTime;
                currentAmmo--;
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
    public void render(Canvas canvas) {
        if (getOwner() == null)
            return;

        int centerX = getOwner().getX() + getOwner().getWidth() / 2;
        int centerY = getOwner().getY() + getOwner().getHeight() / 2;
        float rotation = getOwner().getRotation();

        AffineTransform original = canvas.getGraphics2D().getTransform();

        centerX = centerX + (int) (weaponLength / 2 * Math.cos(Math.toRadians(rotation)));
        centerY = centerY + (int) (weaponLength / 2 * Math.sin(Math.toRadians(rotation)));

        canvas.translate(centerX, centerY);
        canvas.rotate(rotation, 0, 0);

        canvas.renderRectangle(-weaponLength / 4, -WEAPON_WIDTH / 2, weaponLength, WEAPON_WIDTH, WEAPON_COLOR);

        renderHand(canvas, weaponLength / 2 - HAND_RADIUS, 0);
        renderHand(canvas, -weaponLength / 4 + HAND_RADIUS / 2, WEAPON_WIDTH * 2 - HAND_RADIUS);

        canvas.getGraphics2D().setTransform(original);
    }

    private void renderHand(Canvas canvas, int x, int y) {
        canvas.renderCircle(x - HAND_RADIUS / 2, y - HAND_RADIUS / 2,
                HAND_RADIUS, Color.BLACK);
        canvas.renderCircle(x - HAND_RADIUS / 2 + 1, y - HAND_RADIUS / 2 + 1,
                HAND_RADIUS - 2, new Color(229, 194, 152));
    }
}