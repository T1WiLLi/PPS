package pewpew.smash.game.objects;

import java.awt.Color;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class RangedWeaponProperties {
    private final int weaponLength;
    private final int weaponWidth;
    private final int handRadius;
    private final Color weaponColor;
    private final boolean isTwoHanded;
}
