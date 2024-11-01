package pewpew.smash.game.objects.weapon;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.controls.MouseInput;
import pewpew.smash.game.entities.Bullet;
import pewpew.smash.game.network.server.ServerBulletTracker;
import pewpew.smash.game.objects.RangedWeapon;

public class AK47 extends RangedWeapon {
    private long lastShotTime = 0;
    private static final int WEAPON_LENGTH = 40;
    private static final int WEAPON_WIDTH = 10;
    private static final int HAND_RADIUS = 8;
    private static final Color WEAPON_COLOR = new Color(139, 69, 19); // Dark brown
    private static final Color HANDLE_COLOR = new Color(101, 67, 33); // Darker brown

    public AK47(String name, String description, BufferedImage preview) {
        super(name, description, preview);
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

        // Store the player's center position
        int centerX = getOwner().getX() + getOwner().getWidth() / 2;
        int centerY = getOwner().getY() + getOwner().getHeight() / 2;
        float rotation = getOwner().getRotation();

        // Save original state by storing the current transform
        AffineTransform original = canvas.getGraphics2D().getTransform();

        // Move to player center and rotate
        canvas.translate(centerX, centerY);
        canvas.rotate(rotation, 0, 0);

        // Render main weapon body
        canvas.renderRectangle(-WEAPON_LENGTH / 4, -WEAPON_WIDTH / 2,
                WEAPON_LENGTH, WEAPON_WIDTH,
                WEAPON_COLOR);

        // Render handle (grip)
        int handleLength = WEAPON_WIDTH * 2;
        int handleWidth = WEAPON_WIDTH;
        canvas.renderRectangle(-WEAPON_LENGTH / 4, 0,
                handleWidth, handleLength,
                HANDLE_COLOR);

        // Render hands
        // Front hand (near the barrel)
        renderHand(canvas, WEAPON_LENGTH / 2 - HAND_RADIUS, 0);
        // Back hand (on the handle)
        renderHand(canvas, -WEAPON_LENGTH / 4 + HAND_RADIUS / 2, handleLength - HAND_RADIUS);

        // Reset transform to original state
        canvas.getGraphics2D().setTransform(original);
    }

    private void renderHand(Canvas canvas, int x, int y) {
        canvas.renderCircle(x - HAND_RADIUS / 2, y - HAND_RADIUS / 2,
                HAND_RADIUS, Color.BLACK);
        canvas.renderCircle(x - HAND_RADIUS / 2 + 1, y - HAND_RADIUS / 2 + 1,
                HAND_RADIUS - 2, new Color(229, 194, 152));
    }
}