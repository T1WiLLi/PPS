package pewpew.smash.game.objects;

import java.util.Optional;

import lombok.Getter;

@Getter
public enum WeaponType {

    FIST(10, 100, 1, Optional.empty(), Optional.empty()),
    AK47(35, 2500, 0.1, Optional.of(2.5), Optional.of(30)),
    HK416(38, 2700, 0.1, Optional.of(2.3), Optional.of(30)),
    M1A1(45, 3200, 0.4, Optional.of(3.0), Optional.of(20)),
    MAC10(25, 800, 0.08, Optional.of(2.1), Optional.of(32)),
    MP5(30, 1200, 0.09, Optional.of(2.2), Optional.of(25)),
    COLT45(50, 600, 0.6, Optional.of(1.8), Optional.of(7)),
    DEAGLE(75, 750, 0.7, Optional.of(2.5), Optional.of(7)),
    GLOCK(20, 800, 0.4, Optional.of(2.0), Optional.of(15));

    private final int damage;
    private final int range;
    private final double attackSpeed; // Amount of times the player must wait in second to shoot again
    private final Optional<Double> reloadSpeed;
    private final Optional<Integer> ammoCapacity;

    WeaponType(int damage, int range, double attackSpeed, Optional<Double> reloadSpeed,
            Optional<Integer> ammoCapacity) {
        this.damage = damage;
        this.range = range;
        this.attackSpeed = attackSpeed;
        this.reloadSpeed = reloadSpeed;
        this.ammoCapacity = ammoCapacity;
    }

    public boolean isRanged() {
        return reloadSpeed.isPresent() && ammoCapacity.isPresent();
    }

    public boolean isAutomatic() {
        return this.attackSpeed <= 0.1 && this.ammoCapacity.orElse(0) > 10;
    }
}
