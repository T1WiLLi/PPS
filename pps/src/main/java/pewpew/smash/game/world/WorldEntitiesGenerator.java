package pewpew.smash.game.world;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pewpew.smash.game.network.manager.ItemManager;
import pewpew.smash.game.objects.*;
import pewpew.smash.game.objects.special.AmmoStack;
import pewpew.smash.game.world.entities.*;

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
                WorldEntityType type = getRandomEntityType(random);
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
                Item item = generateRandomWorldItem();
                item.teleport(x, y);
                ItemManager.getInstance(true).addItem(item);
                this.items.add(item);
            }
        }
        return this.items;
    }

    private WorldEntityType getRandomEntityType(Random random) {
        WorldEntityType[] entityTypes = WorldEntityType.values();
        int[] weights = new int[entityTypes.length];

        for (int i = 0; i < entityTypes.length; i++) {
            switch (entityTypes[i]) {
                case AIR_DROP_CRATE -> weights[i] = 0; // Exclude air drop crates
                case TREE, TREE_DEAD -> weights[i] = 30;
                case STONE, STONE_GRASS -> weights[i] = 20;
                case CRATE -> weights[i] = 15;
                case SOVIET_CRATE -> weights[i] = 10;
                case AMMO_CRATE -> weights[i] = 5;
                case BUSH -> weights[i] = 20;
            }
        }

        int totalWeight = 0;
        for (int weight : weights) {
            totalWeight += weight;
        }

        int randomValue = random.nextInt(totalWeight);
        for (int i = 0; i < weights.length; i++) {
            randomValue -= weights[i];
            if (randomValue < 0) {
                return entityTypes[i];
            }
        }

        return entityTypes[0];
    }

    private WorldStaticEntity createEntityInstance(WorldEntityType type, int x, int y) {
        WorldStaticEntity entity = switch (type) {
            case BUSH -> new Bush(x, y);
            case CRATE, SOVIET_CRATE, AMMO_CRATE -> new Crate(type, x, y, generateLootTable(type, x, y));
            default -> new WorldStaticEntity(type, x, y);
        };
        entity.setId(entityID++);
        return entity;
    }

    private List<Item> generateLootTable(WorldEntityType type, int x, int y) {
        List<Item> items = new ArrayList<>();
        Random random = new Random();

        switch (type) {
            case CRATE -> {
                items.add(generateWeapon(random, x, y, false));
                items.add(generateAmmo(x, y));
                items.add(generateHealingOrScope(random, x, y));
            }
            case SOVIET_CRATE -> {
                items.add(generateWeapon(random, x, y, true));
                items.add(generateAmmo(x, y));
                items.add(generateHealingOrScope(random, x, y));
            }
            case AMMO_CRATE -> {
                for (int i = 0; i < random.nextInt(3) + 1; i++) {
                    items.add(generateAmmo(x, y));
                }
            }
            default -> {
            } // Do nothing
        }
        return items;
    }

    private Item generateWeapon(Random random, int x, int y, boolean highTier) {
        WeaponType[] weaponPool = highTier
                ? new WeaponType[] { WeaponType.AK47, WeaponType.HK416, WeaponType.DEAGLE, WeaponType.M1A1 }
                : new WeaponType[] { WeaponType.GLOCK, WeaponType.MAC10, WeaponType.MP5, WeaponType.COLT45 };
        WeaponType weaponType = weaponPool[random.nextInt(weaponPool.length)];
        Weapon weapon = ItemFactory.createItem(weaponType);
        weapon.teleport(x, y);
        return weapon;
    }

    private Item generateAmmo(int x, int y) {
        AmmoStack ammoStack = ItemFactory.createAmmoStack();
        ammoStack.teleport(x, y);
        return ammoStack;
    }

    private Item generateHealingOrScope(Random random, int x, int y) {
        int chance = random.nextInt(100);
        if (chance < 70) {
            return generateHealingItem(random, x, y, true);
        } else {
            return generateRandomScope(random, x, y);
        }
    }

    private Item generateHealingItem(Random random, int x, int y, boolean allowPillsAndMedikits) {
        ConsumableType healType = allowPillsAndMedikits
                ? ConsumableType.values()[random.nextInt(ConsumableType.values().length)]
                : ConsumableType.BANDAGE;
        Consumable consumable = ItemFactory.createItem(healType);
        consumable.teleport(x, y);
        return consumable;
    }

    private Item generateRandomScope(Random random, int x, int y) {
        int scopeChance = random.nextInt(100);
        SpecialType scopeType = switch (scopeChance) {
            case 0, 1, 2 -> SpecialType.SCOPE_X4;
            case 3, 4, 5, 6, 7 -> SpecialType.SCOPE_X3;
            default -> SpecialType.SCOPE_X2;
        };
        Item scope = ItemFactory.createItem(scopeType);
        scope.teleport(x, y);
        return scope;
    }

    private Item generateRandomWorldItem() {
        Random random = new Random();
        int itemType = random.nextInt(1000);

        if (itemType < 400) {
            return ItemFactory.createAmmoStack();
        } else if (itemType < 900) {
            WeaponType weapon = switch (random.nextInt(40)) {
                case 0, 1, 2 -> WeaponType.GLOCK;
                case 3, 4 -> WeaponType.MAC10;
                case 5, 6, 7 -> WeaponType.MP5;
                default -> WeaponType.COLT45;
            };
            return ItemFactory.createItem(weapon);
        } else {
            return ItemFactory.createItem(ConsumableType.BANDAGE);
        }
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
}
