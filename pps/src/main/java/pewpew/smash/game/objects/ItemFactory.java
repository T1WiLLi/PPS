package pewpew.smash.game.objects;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import pewpew.smash.game.objects.consumable.Bandage;
import pewpew.smash.game.objects.consumable.Medikit;
import pewpew.smash.game.objects.consumable.Pill;
import pewpew.smash.game.objects.special.AmmoStack;
import pewpew.smash.game.objects.special.Scope;
import pewpew.smash.game.utils.ResourcesLoader;

public class ItemFactory {

    private static int currentID = 0;

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

        preloadRangedWeaponProperties();
    }

    private static void preloadRangedWeaponProperties() {
        rangedWeaponPropertiesMap.put(WeaponType.AK47,
                new RangedWeaponProperties(60, 8, 14, new Color(139, 69, 19), true));
        rangedWeaponPropertiesMap.put(WeaponType.HK416,
                new RangedWeaponProperties(80, 10, 14, new Color(139, 69, 19), true));
        rangedWeaponPropertiesMap.put(WeaponType.M1A1,
                new RangedWeaponProperties(70, 10, 14, new Color(97, 74, 62), true));
        rangedWeaponPropertiesMap.put(WeaponType.MAC10,
                new RangedWeaponProperties(25, 6, 14, new Color(139, 69, 19), false));
        rangedWeaponPropertiesMap.put(WeaponType.MP5,
                new RangedWeaponProperties(30, 7, 12, new Color(105, 105, 105), false));
        rangedWeaponPropertiesMap.put(WeaponType.COLT45,
                new RangedWeaponProperties(50, 8, 5, new Color(112, 128, 144), false));
        rangedWeaponPropertiesMap.put(WeaponType.DEAGLE,
                new RangedWeaponProperties(75, 9, 7, new Color(128, 128, 128), false));
        rangedWeaponPropertiesMap.put(WeaponType.GLOCK,
                new RangedWeaponProperties(20, 5, 12, new Color(105, 105, 105), false));
    }

    public static Weapon createItem(WeaponType type) {
        BufferedImage preview = weaponsPreviews.get(type);
        RangedWeaponProperties properties = rangedWeaponPropertiesMap.get(type);

        int itemId = currentID++;

        Weapon weapon = switch (type) {
            case FIST -> createMeleeWeapon(new Fist(itemId, type.name(), "Good'ol fist", preview), type);
            case AK47, HK416, M1A1, MAC10, MP5, COLT45, DEAGLE, GLOCK ->
                new RangedWeapon(itemId, type.name(), "Description", preview, properties, type);
            default -> throw new IllegalArgumentException("Unknown weapon type");
        };

        return weapon;
    }

    public static Consumable createItem(ConsumableType type) {
        BufferedImage preview = consumablesPreviews.get(type);

        Consumable createdItem = switch (type) {
            case MEDIKIT -> createConsumable(type, preview);
            case BANDAGE -> createConsumable(type, preview);
            case PILL -> createConsumable(type, preview);
            default -> throw new IllegalArgumentException("Unknown consumable type");
        };

        return createdItem;
    }

    public static Item createItem(SpecialType type) {
        BufferedImage preview = specialItems.get(type);

        int itemId = currentID++;

        Item createdItem = switch (type) {
            case SCOPE_X1 -> new Scope(itemId, type.getName(), type.getDescription(), type.getValue(), preview);
            case SCOPE_X2 -> new Scope(itemId, type.getName(), type.getDescription(), type.getValue(), preview);
            case SCOPE_X3 -> new Scope(itemId, type.getName(), type.getDescription(), type.getValue(), preview);
            case SCOPE_X4 -> new Scope(itemId, type.getName(), type.getDescription(), type.getValue(), preview);
        };

        return createdItem;
    }

    public static AmmoStack createAmmoStack() {
        int itemId = currentID++;
        int ammoAmount = 15 + new Random().nextInt(26);
        AmmoStack ammoStack = new AmmoStack(itemId, "Ammo Stack", "A Stack of ammo");
        ammoStack.setAmmo(ammoAmount);
        return ammoStack;
    }

    private static MeleeWeapon createMeleeWeapon(MeleeWeapon weapon, WeaponType type) {
        weapon.buildWeapon(
                type.getDamage(),
                type.getAttackSpeed(),
                type.getRange());
        return weapon;
    }

    private static Consumable createConsumable(ConsumableType type, BufferedImage preview) {
        int itemId = currentID++;
        return switch (type) {
            case MEDIKIT -> new Medikit(itemId, type.name(), "Heals you", preview);
            case BANDAGE -> new Bandage(itemId, type.name(), "Heals you", preview);
            case PILL -> new Pill(itemId, type.name(), "Heals you", preview);
            default -> throw new IllegalArgumentException("Unknown consumable type");
        };
    }

    private static BufferedImage loadPreview(String path, Enum<?> type) {
        return ResourcesLoader.getImage(path, type.name().toLowerCase());
    }
}
