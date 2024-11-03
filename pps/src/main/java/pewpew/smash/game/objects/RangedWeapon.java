package pewpew.smash.game.objects;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.controls.MouseInput;
import pewpew.smash.game.entities.Bullet;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.server.ServerBulletTracker;

@ToString(callSuper = true)
@Getter
public class RangedWeapon extends Weapon {
    private RangedWeaponPropreties properties;

    private int ammoCapacity;
    @Setter
    private int currentAmmo;
    private double reloadSpeed;

    private double reloadTimer;
    @Getter
    private int bulletSpeed;

    @Getter
    private final int weaponLength;
    private final int weaponWidth;
    private final int handRadius;
    private final Color weaponColor;
    private long lastShotTime = 0;

    public RangedWeapon(String name, String description, BufferedImage preview, RangedWeaponPropreties properties) {
        super(name, description, preview);
        this.properties = properties;
        this.weaponLength = properties.getWeaponLength();
        this.weaponWidth = properties.getWeaponWidth();
        this.weaponColor = properties.getWeaponColor();
        this.handRadius = properties.getHandRadius();
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

    private void renderWeapon(Canvas canvas) {
        canvas.renderRectangle(-getWeaponLength() / 4, -getWeaponWidth() / 2, getWeaponLength(), getWeaponWidth(),
                getWeaponColor());
        renderHand(canvas, getWeaponLength() / 2 - getHandRadius(), 0);
        if (properties.isTwoHanded()) {
            renderHand(canvas, -getWeaponLength() / 4 + getHandRadius() / 2, getWeaponWidth() * 2 - getHandRadius());
        }
    }

    private void renderHand(Canvas canvas, int x, int y) {
        canvas.renderCircle(x - getHandRadius() / 2, y - getHandRadius() / 2, getHandRadius(), Color.BLACK);
        canvas.renderCircle(x - getHandRadius() / 2 + 1, y - getHandRadius() / 2 + 1, getHandRadius() - 2,
                new Color(229, 194, 152));
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
}