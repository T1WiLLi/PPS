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
    private final RangedWeaponProperties properties;
    private final int weaponLength;
    private final int weaponWidth;
    private final int handRadius;
    private final Color weaponColor;

    private WeaponType type;

    private int ammoCapacity;
    @Setter
    private int currentAmmo;
    private double reloadSpeed;
    private int bulletSpeed;
    private long lastShotTime = 0;

    public RangedWeapon(int id, String name, String description, BufferedImage preview,
            RangedWeaponProperties properties, WeaponType type) {
        super(id, name, description, preview);
        setDimensions(48, 48);
        this.properties = properties;
        this.type = type;
        this.weaponLength = properties.getWeaponLength();
        this.weaponWidth = properties.getWeaponWidth();
        this.weaponColor = properties.getWeaponColor();
        this.handRadius = properties.getHandRadius();
        buildWeapon();
    }

    private void buildWeapon() {
        super.buildWeapon(this.type.getDamage(), this.type.getAttackSpeed(), this.type.getRange());
        this.reloadSpeed = this.type.getReloadSpeed().get();
        this.ammoCapacity = this.type.getAmmoCapacity().get();
        this.currentAmmo = this.ammoCapacity;
        this.bulletSpeed = this.type.getBulletSpeed().get();
    }

    public void reload() {
        int neededAmmo = this.ammoCapacity - this.currentAmmo;
        int ammotAvailable = getOwner().getInventory().useAmmo(neededAmmo);
        currentAmmo += ammotAvailable;
    }

    public void shoot() {
        if (canShoot() && getOwner() != null) {
            spawnBullet((Player) getOwner());
        }
    }

    @Override
    public void updateClient() {
    }

    @Override
    public void updateServer() {
        if (getOwner() != null) {
            if (getOwner().getMouseInput() == MouseInput.LEFT_CLICK && canShoot()) {
                shoot();
            }
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

        centerX += (int) (weaponLength / 2 * Math.cos(Math.toRadians(rotation)));
        centerY += (int) (weaponLength / 2 * Math.sin(Math.toRadians(rotation)));

        canvas.translate(centerX, centerY);
        canvas.rotate(rotation, 0, 0);
        renderWeapon(canvas);

        canvas.getGraphics2D().setTransform(original);
    }

    private void renderWeapon(Canvas canvas) {
        canvas.renderRectangle(-weaponLength / 4, -weaponWidth / 2, weaponLength, weaponWidth, weaponColor);
        renderHand(canvas, weaponLength / 2 - handRadius, 0);

        if (properties.isTwoHanded()) {
            renderHand(canvas, -weaponLength / 4 + handRadius / 2, weaponWidth * 2 - handRadius);
        }
    }

    private void renderHand(Canvas canvas, int x, int y) {
        canvas.renderCircle(x - handRadius / 2, y - handRadius / 2, handRadius, Color.BLACK);
        canvas.renderCircle(x - handRadius / 2 + 1, y - handRadius / 2 + 1, handRadius - 2, new Color(229, 194, 152));
    }

    private boolean canShoot() {
        long currentTime = System.currentTimeMillis();
        return currentAmmo > 0 && (currentTime - lastShotTime >= (getAttackSpeed() * 1000));
    }

    private void spawnBullet(Player owner) {
        lastShotTime = System.currentTimeMillis();
        currentAmmo--;
        Bullet bullet = new Bullet(owner);
        ServerBulletTracker.getInstance().addBullet(bullet, this);
    }
}