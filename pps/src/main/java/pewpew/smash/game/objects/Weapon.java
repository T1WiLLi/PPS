package pewpew.smash.game.objects;

import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public abstract class Weapon extends Item {

    protected int damage;
    protected int range;
    protected double attackSpeed;

    public abstract void updateServer();

    public Weapon(String name, String description, BufferedImage preview) {
        super(name, description, preview);
    }

    protected void buildWeapon(int damage, double attackSpeed, int range) {
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.range = range;
    }
}
