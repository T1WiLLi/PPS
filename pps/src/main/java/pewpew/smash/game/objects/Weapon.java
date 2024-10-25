package pewpew.smash.game.objects;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public abstract class Weapon extends Item {

    protected int damage;
    protected int range;
    protected double attackSpeed;

    public abstract void updateServer();

    public abstract void attack();

    public Weapon(String name, String description) {
        super(name, description);
    }

    protected void buildWeapon(int damage, double attackSpeed, int range) {
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.range = range;
    }
}
