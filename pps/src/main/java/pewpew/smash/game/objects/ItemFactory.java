package pewpew.smash.game.objects;

import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

import pewpew.smash.game.objects.consumable.Bandage;
import pewpew.smash.game.objects.consumable.Medikit;
import pewpew.smash.game.objects.consumable.Pill;
import pewpew.smash.game.objects.weapon.AK47;
import pewpew.smash.game.objects.weapon.Fist;
import pewpew.smash.game.utils.ResourcesLoader;

// TODO: Don't forget to change the name of the picture to match the type in the ENUM
public class ItemFactory {
    private static final Map<WeaponType, BufferedImage> weaponsPreviews = new EnumMap<>(WeaponType.class);
    private static final Map<ConsumableType, BufferedImage> consumablesPreviews = new EnumMap<>(ConsumableType.class);

    public static void preloadItemPreviews() {
        for (WeaponType type : WeaponType.values()) {
            if (type.name().toLowerCase().equals("fist")) {
                continue; // Fist has no preview
            }
            weaponsPreviews.put(type, loadPreview(ResourcesLoader.PREVIEW_PATH, type));
        }

        for (ConsumableType type : ConsumableType.values()) {
            consumablesPreviews.put(type, loadPreview(ResourcesLoader.PREVIEW_PATH, type));
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Item> T createItem(WeaponType type) {
        BufferedImage preview = weaponsPreviews.get(type);

        Item createdItem = switch (type) {
            case FIST -> createMeleeWeapon(new Fist(type.name(), "Good'ol fist", preview), type);
            case AK47 -> createRangedWeapon(new AK47(type.name(), "AK-47", preview), type);
            // Add more weapon cases here as needed
            default -> throw new IllegalArgumentException("Unknown weapon type");
        };

        if (createdItem instanceof Item) {
            return (T) createdItem;
        } else {
            throw new ClassCastException("Failed to create item of type: " + type);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Item> T createItem(ConsumableType type) {
        BufferedImage preview = consumablesPreviews.get(type);

        Item createdItem = switch (type) {
            case MEDIKIT -> createConsumable(type, preview);
            case BANDAGE -> createConsumable(type, preview);
            case PILL -> createConsumable(type, preview);
            default -> throw new IllegalArgumentException("Unknown consumable type");
        };

        if (createdItem instanceof Item) {
            return (T) createdItem;
        } else {
            throw new ClassCastException("Failed to create item of type: " + type);
        }
    }

    private static RangedWeapon createRangedWeapon(RangedWeapon weapon, WeaponType type) {
        weapon.buildWeapon(
                type.getDamage(),
                type.getAttackSpeed(),
                type.getRange(),
                type.getReloadSpeed().get(),
                type.getAmmoCapacity().get());
        return weapon;
    }

    private static MeleeWeapon createMeleeWeapon(MeleeWeapon weapon, WeaponType type) {
        weapon.buildWeapon(
                type.getDamage(),
                type.getAttackSpeed(),
                type.getRange());
        return weapon;
    }

    private static Consumable createConsumable(ConsumableType type, BufferedImage preview) {
        return switch (type) {
            case MEDIKIT -> new Medikit(type.name(), "Heals you", preview);
            case BANDAGE -> new Bandage(type.name(), "Heals you", preview);
            case PILL -> new Pill(type.name(), "Heals you", preview);
            default -> throw new IllegalArgumentException("Unknown consumable type");
        };
    }

    private static BufferedImage loadPreview(String path, Enum<?> type) {
        return ResourcesLoader.getImage(path, type.name().toLowerCase());
    }
}
