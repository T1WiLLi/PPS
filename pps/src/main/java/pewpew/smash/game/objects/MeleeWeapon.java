package pewpew.smash.game.objects;

import java.awt.Shape;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public abstract class MeleeWeapon extends Weapon {

    protected boolean isAttacking;
    protected float attackProgress = 0.0f;

    public abstract Shape getHitbox();

    public MeleeWeapon(String name, String description) {
        super(name, description);
    }

    public void buildWeapon(int damage, double attackSpeed, int range) {
        super.buildWeapon(damage, attackSpeed, range);
    }
}
