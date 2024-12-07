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
import pewpew.smash.game.world.entities.Crate;
import pewpew.smash.game.world.entities.WorldEntityType;

public class AirdropEvent {

    @Getter
    private final Plane plane;
    private Crate crate;
    @Getter
    private boolean crateDropped;

    public AirdropEvent() {
        this.plane = HelpMethods.generatePlane();
    }

    public Crate createCrate(int x, int y) {
        this.crate = new Crate(WorldEntityType.AIR_DROP_CRATE, x, y, createLoot());
        this.crateDropped = true;
        return crate;
    }

    private List<Item> createLoot() {
        List<Item> loot = new ArrayList<>();
        Random random = new Random();

        Item weapon = switch (random.nextInt(3)) {
            case 0 -> ItemFactory.createItem(WeaponType.HK416);
            case 1 -> ItemFactory.createItem(WeaponType.M1A1);
            case 2 -> ItemFactory.createItem(WeaponType.AK47);
            default -> ItemFactory.createItem(WeaponType.AK47);
        };
        loot.add(weapon);

        loot.add(ItemFactory.createItem(ConsumableType.MEDIKIT));

        Item healingItem = switch (random.nextInt(2)) {
            case 0 -> ItemFactory.createItem(ConsumableType.BANDAGE);
            case 1 -> ItemFactory.createItem(ConsumableType.PILL);
            default -> ItemFactory.createItem(ConsumableType.BANDAGE);
        };
        loot.add(healingItem);

        loot.add(ItemFactory.createAmmoStack());

        Item scope = getRandomScope(random);
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
