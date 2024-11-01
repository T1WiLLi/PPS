package pewpew.smash.game.objects;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.entities.Bullet;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.server.ServerBulletTracker;

@ToString(callSuper = true)
@Getter
public abstract class RangedWeapon extends Weapon {
    protected int ammoCapacity;
    @Setter
    protected int currentAmmo;
    protected double reloadSpeed;

    private double reloadTimer;
    @Getter
    private int bulletSpeed;

    // Render gun
    @Getter
    protected final int weaponLength;
    protected final int weaponWidth;
    protected final int handRadius;
    protected final Color weaponColor;
    private long lastShotTime = 0;

    public abstract void shoot();

    public RangedWeapon(String name, String description, BufferedImage preview, int weaponLength, int weaponWidth,
            int handRadius, Color weaponColor) {
        super(name, description, preview);
        this.weaponLength = weaponLength;
        this.weaponWidth = weaponWidth;
        this.handRadius = handRadius;
        this.weaponColor = weaponColor;
    }

    public void buildWeapon(int damage, double attackSpeed, int range, double reloadSpeed, int ammoCapacity,
            int bulletSpeed) {
        super.buildWeapon(damage, attackSpeed, range);
        this.reloadSpeed = reloadSpeed;
        this.ammoCapacity = ammoCapacity;
        this.currentAmmo = ammoCapacity;
        this.bulletSpeed = bulletSpeed;
    }

    public void reload() {
        currentAmmo = ammoCapacity;
    }

    protected boolean canShoot() {
        long currentTime = System.currentTimeMillis();
        return currentAmmo > 0 && (currentTime - lastShotTime >= (getAttackSpeed() * 1000));
    }

    protected void spawnBullet(Player owner) {
        Bullet bullet = new Bullet(owner);
        ServerBulletTracker.getInstance().addBullet(bullet);
        lastShotTime = System.currentTimeMillis();
        currentAmmo--;
    }

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

        renderWeapon(canvas);

        canvas.getGraphics2D().setTransform(original);
    }

    protected abstract void renderWeapon(Canvas canvas);
}