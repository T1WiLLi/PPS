package pewpew.smash.game.objects.weapon;

import java.awt.Color;
import java.awt.image.BufferedImage;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.entities.Bullet;
import pewpew.smash.game.objects.RangedWeapon;

public class AK47 extends RangedWeapon {

    private long lastShotTime = 0;

    public AK47(String name, String description, BufferedImage preview) {
        super(name, description, preview);

    }

    @Override
    public void shoot() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastShotTime >= (getAttackSpeed() * 1000) && canShoot()) {
            if (getOwner() != null) {
                Bullet bullet = new Bullet(getOwner());
                // Find a way to add the bullet to the game
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
    }

    @Override
    public void render(Canvas canvas) {
        int playerX = getOwner().getX();
        int playerY = getOwner().getY();
        float rotation = getOwner().getRotation();

        int handRadius = 16;
        int weaponLength = 30;
        int weaponWidth = 6;

        double angleRad = Math.toRadians(rotation);
        int handOffsetX = (int) (weaponLength / 2 * Math.cos(angleRad));
        int handOffsetY = (int) (weaponLength / 2 * Math.sin(angleRad));

        int leftHandX = playerX - handOffsetX / 2;
        int leftHandY = playerY - handOffsetY / 2;
        int rightHandX = playerX + handOffsetX;
        int rightHandY = playerY + handOffsetY;

        canvas.setColor(new Color(139, 69, 19));
        canvas.translate(playerX, playerY);
        canvas.rotate(rotation, 0, 0);
        canvas.renderRectangle(-weaponLength / 2, -weaponWidth / 2, weaponLength, weaponWidth, new Color(139, 69, 19));
        canvas.resetScale();

        renderHand(canvas, leftHandX, leftHandY, handRadius);
        renderHand(canvas, rightHandX, rightHandY, handRadius);
    }

    @Override
    public void preview(Canvas canvas) {
        canvas.renderImage(getPreview(), getX(), getY());
    }

    private void renderHand(Canvas canvas, int x, int y, int handRadius) {
        canvas.renderCircle(x, y, handRadius, Color.BLACK);
        canvas.renderCircle(x + 1, y + 1, handRadius - 2, new Color(229, 194, 152));
    }
}
