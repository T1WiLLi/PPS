package pewpew.smash.game.objects;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
@Setter
public abstract class MeleeWeapon extends Weapon {

    protected boolean isAttacking;
    protected boolean isReturning;
    protected float attackProgress = 0.0f;

    public abstract void attack();

    public abstract Rectangle getHitbox();

    public MeleeWeapon(int id, String name, String description, BufferedImage preview) {
        super(id, name, description, preview);
    }

    public void buildWeapon(int damage, double attackSpeed, int range) {
        super.buildWeapon(damage, attackSpeed, range);
    }
}
