package pewpew.smash.game.objects;

import lombok.Getter;

/**
 * ConsumableType
 */
@Getter
public enum ConsumableType {

    MEDIKIT("Medikit", "Restores 100 Health", 100, 5),
    PILL("Medical Pill", "Restores 25 Health", 25, 2),
    BANDAGE("Bandage", "Restores 15 Health", 15, 1.5); // In Seconds

    private final String name;
    private final String description;
    private final int healAmount;
    private final double useTime;

    ConsumableType(String name, String description, int healAmount, double useTime) {
        this.name = name;
        this.description = description;
        this.healAmount = healAmount;
        this.useTime = useTime;
    }
}