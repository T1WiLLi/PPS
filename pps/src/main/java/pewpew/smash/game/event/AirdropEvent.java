package pewpew.smash.game.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.Getter;
import pewpew.smash.game.entities.Plane;
import pewpew.smash.game.objects.ConsumableType;
import pewpew.smash.game.objects.Item;
import pewpew.smash.game.objects.ItemFactory;
import pewpew.smash.game.objects.SpecialType;
import pewpew.smash.game.objects.WeaponType;
import pewpew.smash.game.utils.HelpMethods;
import pewpew.smash.game.world.WorldGenerator;
import pewpew.smash.game.world.entities.Crate;
import pewpew.smash.game.world.entities.WorldEntityType;

public class AirdropEvent {

    @Getter
    private final Plane plane;
    private Crate crate;
    @Getter
    private boolean crateDropped;

    @Getter
    private int dropX;
    @Getter
    private int dropY;

    public AirdropEvent() {
        this.plane = HelpMethods.generatePlane();
        determineDropLocation();
    }

    public Crate createCrate(int x, int y) {
        this.crate = new Crate(WorldEntityType.AIR_DROP_CRATE, x, y, createLoot(x, y));
        this.crateDropped = true;
        return crate;
    }

    private void determineDropLocation() {
        Random random = new Random();

        float planeCenterX = plane.getX() + plane.getWidth() / 2f;
        float planeCenterY = plane.getY() + plane.getHeight() / 2f;

        float endX = planeCenterX;
        float endY = planeCenterY;

        int worldWidth = WorldGenerator.getWorldWidth();
        int worldHeight = WorldGenerator.getWorldHeight();

        switch (plane.getDirection()) {
            case UP -> endY = 0;
            case DOWN -> endY = worldHeight;
            case LEFT -> endX = 0;
            case RIGHT -> endX = worldWidth;
            case NONE -> throw new UnsupportedOperationException("Unimplemented case: " + plane.getDirection());
            default -> throw new IllegalArgumentException("Unexpected value: " + plane.getDirection());
        }

        double mapPos = 0.3 + (0.4 * random.nextDouble());
        this.dropX = (int) (planeCenterX + (endX - planeCenterX) * mapPos);
        this.dropY = (int) (planeCenterY + (endY - planeCenterY) * mapPos);

        System.out.println("DropX: " + dropX + ", DropY: " + dropY);
        System.out.println("Plane Center Start: (" + planeCenterX + ", " + planeCenterY + ")");
        System.out.println("Plane End: (" + endX + ", " + endY + ")");
    }

    private List<Item> createLoot(int x, int y) {
        List<Item> loot = new ArrayList<>();
        Random random = new Random();

        Item weapon = switch (random.nextInt(3)) {
            case 0 -> ItemFactory.createItem(WeaponType.HK416);
            case 1 -> ItemFactory.createItem(WeaponType.M1A1);
            case 2 -> ItemFactory.createItem(WeaponType.AK47);
            default -> ItemFactory.createItem(WeaponType.AK47);
        };
        weapon.teleport(x, y);
        loot.add(weapon);

        loot.add(ItemFactory.createItem(ConsumableType.MEDIKIT));

        Item healingItem = switch (random.nextInt(2)) {
            case 0 -> ItemFactory.createItem(ConsumableType.BANDAGE);
            case 1 -> ItemFactory.createItem(ConsumableType.PILL);
            default -> ItemFactory.createItem(ConsumableType.BANDAGE);
        };
        healingItem.teleport(x, y);
        loot.add(healingItem);

        Item ammoStack = ItemFactory.createAmmoStack();
        ammoStack.teleport(x, y);
        loot.add(ammoStack);

        Item scope = getRandomScope(random);
        scope.teleport(x, y);
        loot.add(scope);

        return loot;
    }

    private Item getRandomScope(Random random) {
        int chance = random.nextInt(100);
        if (chance < 10) {
            return ItemFactory.createItem(SpecialType.SCOPE_X4);
        } else if (chance < 40) { // 30% chance for 3x scope
            return ItemFactory.createItem(SpecialType.SCOPE_X3);
        } else { // 60% chance for 2x scope
            return ItemFactory.createItem(SpecialType.SCOPE_X2);
        }
    }
}