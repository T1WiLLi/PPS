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
                new RangedWeaponProperties(60, 8, 16, new Color(160, 82, 45), true));
        rangedWeaponPropertiesMap.put(WeaponType.HK416,
                new RangedWeaponProperties(80, 10, 16, new Color(105, 105, 105), true));
        rangedWeaponPropertiesMap.put(WeaponType.M1A1,
                new RangedWeaponProperties(70, 9, 15, new Color(128, 0, 0), true));
        rangedWeaponPropertiesMap.put(WeaponType.MAC10,
                new RangedWeaponProperties(25, 6, 10, new Color(169, 169, 169), false));
        rangedWeaponPropertiesMap.put(WeaponType.MP5,
                new RangedWeaponProperties(30, 7, 13, new Color(47, 79, 79), true));
        rangedWeaponPropertiesMap.put(WeaponType.COLT45,
                new RangedWeaponProperties(25, 8, 7, new Color(70, 130, 180), false));
        rangedWeaponPropertiesMap.put(WeaponType.DEAGLE,
                new RangedWeaponProperties(30, 9, 8, new Color(192, 192, 192), false));
        rangedWeaponPropertiesMap.put(WeaponType.GLOCK,
                new RangedWeaponProperties(20, 5, 10, new Color(119, 136, 153), false));
        rangedWeaponPropertiesMap.put(WeaponType.M4A1,
                new RangedWeaponProperties(85, 12, 18, new Color(34, 139, 34), true));
        rangedWeaponPropertiesMap.put(WeaponType.SCORPION,
                new RangedWeaponProperties(65, 9, 14, new Color(139, 69, 19), true));
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

        createdItem.buildConsumable(type.getHealAmount(), type.getUseTime());
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
        Consumable consumable = switch (type) {
            case MEDIKIT -> new Medikit(itemId, type.name(), type.getDescription(), preview);
            case BANDAGE -> new Bandage(itemId, type.name(), type.getDescription(), preview);
            case PILL -> new Pill(itemId, type.name(), type.getDescription(), preview);
            default -> throw new IllegalArgumentException("Unknown consumable type");
        };
        consumable.setType(type);
        return consumable;
    }

    private static BufferedImage loadPreview(String path, Enum<?> type) {
        return ResourcesLoader.getImage(path, type.name().toLowerCase());
    }
}
