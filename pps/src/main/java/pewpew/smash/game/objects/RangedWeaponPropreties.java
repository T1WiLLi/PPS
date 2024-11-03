package pewpew.smash.game.objects;

import java.awt.Color;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RangedWeaponPropreties {
    private final int weaponLength;
    private final int weaponWidth;
    private final int handRadius;
    private final Color weaponColor;
    private final boolean isTwoHanded;
}
