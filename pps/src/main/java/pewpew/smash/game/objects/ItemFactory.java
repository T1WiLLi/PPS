package pewpew.smash.game.objects;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

import pewpew.smash.game.objects.consumable.Bandage;
import pewpew.smash.game.objects.consumable.Medikit;
import pewpew.smash.game.objects.consumable.Pill;
import pewpew.smash.game.objects.special.Scope;
import pewpew.smash.game.utils.ResourcesLoader;

public class ItemFactory {
    private static final Map<WeaponType, BufferedImage> weaponsPreviews = new EnumMap<>(WeaponType.class);
    private static final Map<ConsumableType, BufferedImage> consumablesPreviews = new EnumMap<>(ConsumableType.class);
    private static final Map<SpecialType, BufferedImage> specialItems = new EnumMap<>(SpecialType.class);

    private static final Map<WeaponType, RangedWeaponProperties> rangedWeaponPropertiesMap = new EnumMap<>(
            WeaponType.class);

    public static void preloadItemPreviews() {
        for (WeaponType type : WeaponType.values()) {
            weaponsPreviews.put(type, loadPreview(ResourcesLoader.PREVIEW_PATH, type));
        }

        for (ConsumableType type : ConsumableType.values()) {
            consumablesPreviews.put(type, loadPreview(ResourcesLoader.PREVIEW_PATH, type));
        }

        for (SpecialType type : SpecialType.values()) {
            specialItems.put(type, loadPreview(ResourcesLoader.HUD_PATH, type));
        }

        preloadRangedWeaponPropreties();
    }

    private static void preloadRangedWeaponPropreties() {
        rangedWeaponPropertiesMap.put(WeaponType.AK47,
                new RangedWeaponProperties(60, 8, 14, new Color(139, 69, 19), true));
        rangedWeaponPropertiesMap.put(WeaponType.HK416,
                new RangedWeaponProperties(80, 10, 14, new Color(139, 69, 19), true));
        rangedWeaponPropertiesMap.put(WeaponType.M1A1,
                new RangedWeaponProperties(70, 10, 14, new Color(97, 74, 62), true));
        rangedWeaponPropertiesMap.put(WeaponType.MAC10,
                new RangedWeaponProperties(25, 6, 14, new Color(139, 69, 19), false));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Weapon> T createItem(WeaponType type) {
        BufferedImage preview = weaponsPreviews.get(type);
        RangedWeaponProperties properties = rangedWeaponPropertiesMap.get(type);

        Item weapon = switch (type) {
            case FIST -> createMeleeWeapon(new Fist(type.name(), "Good'ol fist", preview), type);
            case AK47 -> createRangedWeapon(new RangedWeapon(type.name(), "AK-47", preview, properties), type);
            case HK416 ->
                createRangedWeapon(new RangedWeapon(type.name(), "For heavy duty combat", preview, properties), type);
            case M1A1 -> createRangedWeapon(
                    new RangedWeapon(type.name(), "You won't see it t'ill it's too late", preview, properties), type);
            case MAC10 -> createRangedWeapon(
                    new RangedWeapon(type.name(), "Samll-rate firing machine", preview, properties), type);
            default -> throw new IllegalArgumentException("Unknown weapon type");
        };

        if (weapon instanceof Item) {
            return (T) weapon;
        } else {
            throw new ClassCastException("Failed to create item of type: " + type);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Consumable> T createItem(ConsumableType type) {
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

    @SuppressWarnings("unchecked")
    public static <T extends Item> T createItem(SpecialType type) {
        BufferedImage preview = specialItems.get(type);

        Item createdItem = switch (type) {
            case SCOPE_X1 -> new Scope(type.getName(), type.getDescription(), type.getValue(), preview);
            case SCOPE_X2 -> new Scope(type.getName(), type.getDescription(), type.getValue(), preview);
            case SCOPE_X3 -> new Scope(type.getName(), type.getDescription(), type.getValue(), preview);
            case SCOPE_X4 -> new Scope(type.getName(), type.getDescription(), type.getValue(), preview);
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
                type.getAmmoCapacity().get(),
                type.getBulletSpeed().get());
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
