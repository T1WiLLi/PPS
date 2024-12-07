package pewpew.smash.game.objects;

import java.util.Optional;
import lombok.Getter;

@Getter
public enum WeaponType {
    // Pistols
    GLOCK(8, 750, 0.4, Optional.of(1), Optional.of(1.5), Optional.of(15)),
    COLT45(15, 600, 0.6, Optional.of(2), Optional.of(1.8), Optional.of(7)),
    DEAGLE(20, 1000, 0.7, Optional.of(2), Optional.of(2.2), Optional.of(7)),

    // SMG's
    MAC10(10, 1200, 0.08, Optional.of(3), Optional.of(2.0), Optional.of(32)),
    MP5(12, 1400, 0.09, Optional.of(3), Optional.of(2.1), Optional.of(25)),

    // Assault Rifles
    AK47(14, 2500, 0.125, Optional.of(3), Optional.of(2.5), Optional.of(28)),
    HK416(16, 2700, 0.1, Optional.of(4), Optional.of(2.3), Optional.of(32)),
    M4A1(18, 2800, 0.09, Optional.of(4), Optional.of(2.0), Optional.of(30)),

    // Precision Rifles
    M1A1(25, 3400, 0.5, Optional.of(5), Optional.of(3.0), Optional.of(20)),

    // Mid-tier Assault Rifles
    SCORP(15, 2600, 0.12, Optional.of(3), Optional.of(2.4), Optional.of(25)),

    // Fists by default.
    FIST(5, 25, 0.020, Optional.empty(), Optional.empty(), Optional.empty());

    private final int damage;
    private final int range;
    private final double attackSpeed;
    private final Optional<Integer> bulletSpeed;
    private final Optional<Double> reloadSpeed;
    private final Optional<Integer> ammoCapacity;

    WeaponType(int damage, int range, double attackSpeed, Optional<Integer> bulletSpeed, Optional<Double> reloadSpeed,
            Optional<Integer> ammoCapacity) {
        this.damage = damage;
        this.range = range;
        this.attackSpeed = attackSpeed;
        this.bulletSpeed = bulletSpeed;
        this.reloadSpeed = reloadSpeed;
        this.ammoCapacity = ammoCapacity;
    }

    public boolean isRanged() {
        return reloadSpeed.isPresent() && ammoCapacity.isPresent() && bulletSpeed.isPresent();
    }

    public boolean isAutomatic() {
        return this.attackSpeed <= 0.1 && this.ammoCapacity.orElse(0) > 10;
    }
}
