package pewpew.smash.game.objects;

import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public abstract class RangedWeapon extends Weapon {

    protected int ammoCapacity;
    @Setter
    protected int currentAmmo;
    protected double reloadSpeed;

    private double reloadTimer;
    private int bulletSpeed;

    public abstract void shoot();

    public RangedWeapon(String name, String description, BufferedImage preview) {
        super(name, description, preview);
    }

    public void buildWeapon(int damage, double attackSpeed, int range, double reloadSpeed, int ammoCapacity,
            int bulletSpeed) {
        super.buildWeapon(damage, attackSpeed, range);
        this.reloadSpeed = reloadSpeed;
        this.ammoCapacity = ammoCapacity;
        this.currentAmmo = ammoCapacity;
        this.bulletSpeed = bulletSpeed;
    }

    // This function should be called in update()
    protected void reload() {
        reloadTimer -= 0.01;
        if (reloadSpeed <= 0) {
            reloadTimer = reloadSpeed;
            currentAmmo = ammoCapacity;
        }
    }

    protected boolean canShoot() {
        return true;
        // return currentAmmo > 0;
    }
}
