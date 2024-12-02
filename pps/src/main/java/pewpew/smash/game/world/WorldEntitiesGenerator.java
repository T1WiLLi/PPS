package pewpew.smash.game.world;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pewpew.smash.game.network.manager.ItemManager;
import pewpew.smash.game.objects.Consumable;
import pewpew.smash.game.objects.ConsumableType;
import pewpew.smash.game.objects.Item;
import pewpew.smash.game.objects.ItemFactory;
import pewpew.smash.game.objects.SpecialType;
import pewpew.smash.game.objects.Weapon;
import pewpew.smash.game.objects.WeaponType;
import pewpew.smash.game.objects.special.AmmoStack;
import pewpew.smash.game.world.entities.Bush;
import pewpew.smash.game.world.entities.Crate;
import pewpew.smash.game.world.entities.WorldEntityType;
import pewpew.smash.game.world.entities.WorldStaticEntity;

// Generate entities for the game world.
// TODO: Make the normal case generate lesser good items. While the Soviet case generates better items.
public class WorldEntitiesGenerator {

    private static final int ITEM_SIZE = 48;

    private final List<WorldStaticEntity> worldEntities;
    private final List<Item> items;
    private int entityID = 0;

    public WorldEntitiesGenerator() {
        this.worldEntities = new ArrayList<>();
        this.items = new ArrayList<>();
    }

    public List<WorldStaticEntity> generateWorldEntities(long seed, byte[][] data, int numEntities) {
        Random random = new Random(seed);

        while (worldEntities.size() < numEntities) {
            int tileX = random.nextInt(data.length);
            int tileY = random.nextInt(data[0].length);

            int x = tileX * WorldGenerator.TILE_SIZE;
            int y = tileY * WorldGenerator.TILE_SIZE;

            if (data[tileX][tileY] == WorldGenerator.GRASS) {
                WorldEntityType type = WorldEntityType.values()[random.nextInt(WorldEntityType.values().length)];
                WorldStaticEntity entity = createEntityInstance(type, x, y);

                if (entity != null && isValidPlacement(entity)) {
                    this.worldEntities.add(entity);
                } else {
                    System.out.println("PLACEMENT IS NOT VALID!");
                }
            }
        }
        return this.worldEntities;
    }

    public List<Item> generateItems(byte[][] data, int numItems) {
        Random random = new Random();
        while (items.size() < numItems) {
            int tileX = random.nextInt(data.length);
            int tileY = random.nextInt(data[0].length);

            int x = tileX * WorldGenerator.TILE_SIZE;
            int y = tileY * WorldGenerator.TILE_SIZE;

            if (isValidPosition(x, y)) {
                Item item = generateRandomItem();
                item.teleport(x, y);
                ItemManager.getInstance(true).addItem(item);
                this.items.add(item);
            }
        }
        return this.items;
    }

    private WorldStaticEntity createEntityInstance(WorldEntityType type, int x, int y) {
        WorldStaticEntity entity = switch (type) {
            case BUSH -> new Bush(x, y);
            case CRATE, SOVIET_CRATE -> new Crate(type, x, y, generateLootTable(x, y));
            default -> new WorldStaticEntity(type, x, y);
        };
        entity.setId(entityID++);
        return entity;
    }

    private List<Item> generateLootTable(int x, int y) {
        List<Item> items = new ArrayList<>();
        Random random = new Random();

        boolean containsGun = random.nextBoolean();

        if (containsGun) {
            WeaponType weaponType = switch (random.nextInt(100)) {
                case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 -> WeaponType.GLOCK;
                case 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 -> WeaponType.MP5;
                case 20, 21, 22, 23, 24, 25, 26, 27, 28, 29 -> WeaponType.MAC10;
                case 30, 31, 32, 33, 34 -> WeaponType.DEAGLE;
                case 35, 36, 37, 38, 39, 40, 41 -> WeaponType.COLT45;
                case 42, 43, 44, 45, 46, 47, 48, 49 -> WeaponType.AK47;
                case 50, 51, 52, 53, 54, 55, 56, 57, 58, 59 -> WeaponType.M1A1;
                default -> WeaponType.HK416;
            };
            Weapon weapon = ItemFactory.createItem(weaponType);
            weapon.teleport(x, y);
            items.add(weapon);
        } else {
            ConsumableType healType = switch (random.nextInt(3)) {
                case 0 -> ConsumableType.MEDIKIT;
                case 1 -> ConsumableType.PILL;
                default -> ConsumableType.BANDAGE;
            };
            Consumable consumable = ItemFactory.createItem(healType);
            consumable.teleport(x, y);
            items.add(consumable);
        }
        AmmoStack ammoStack = ItemFactory.createAmmoStack();
        ammoStack.teleport(x, y);
        items.add(ammoStack);
        return items;
    }

    private boolean isValidPlacement(WorldStaticEntity entity) {
        for (WorldStaticEntity other : this.worldEntities) {
            if (entity.isColliding(other)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidPosition(int x, int y) {
        Rectangle bounds = new Rectangle(x, y, ITEM_SIZE, ITEM_SIZE);

        for (WorldStaticEntity entity : this.worldEntities) {
            Shape hitbox = entity.getHitbox();

            if (hitbox != null && hitbox.intersects(bounds)) {
                return false;
            }
        }

        for (Item item : this.items) {
            Rectangle itemHitbox = new Rectangle(item.getX(), item.getY(), item.getWidth(), item.getHeight());
            if (bounds.intersects(itemHitbox)) {
                return false;
            }
        }

        return true;
    }

    private Item generateRandomItem() {
        Random random = new Random();
        int itemType = random.nextInt(1000);

        if (itemType < 300) {
            return ItemFactory.createAmmoStack();

        } else if (itemType < 700) {
            WeaponType weapon = switch (random.nextInt(60)) {
                case 0, 1, 2, 3, 4 -> WeaponType.HK416;
                case 5, 6, 7, 8, 9, 10, 11, 12, 13 -> WeaponType.M1A1;
                case 14, 15, 16, 17 -> WeaponType.DEAGLE;
                case 18, 19, 20, 21, 22, 23, 24 -> WeaponType.AK47;
                case 25, 26, 27, 28, 29 -> WeaponType.MAC10;
                case 30, 31, 32, 33, 34, 35, 36 -> WeaponType.MP5;
                case 37, 38, 39, 40, 41 -> WeaponType.COLT45;
                default -> WeaponType.GLOCK;
            };
            return ItemFactory.createItem(weapon);

        } else if (itemType < 950) { // 25% chance for a consumable
            ConsumableType consumable = switch (random.nextInt(35)) {
                case 0, 1, 2, 3, 4 -> ConsumableType.MEDIKIT;
                case 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 -> ConsumableType.PILL;
                default -> ConsumableType.BANDAGE;
            };
            return ItemFactory.createItem(consumable);

        } else { // 5% chance for a scope
            SpecialType scope = switch (random.nextInt(60)) {
                case 0, 1, 2, 3, 4 -> SpecialType.SCOPE_X4;
                case 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 -> SpecialType.SCOPE_X3;
                default -> SpecialType.SCOPE_X2;
            };
            return ItemFactory.createItem(scope);
        }
    }
}
