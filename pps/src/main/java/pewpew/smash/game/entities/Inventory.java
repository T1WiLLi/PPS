package pewpew.smash.game.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pewpew.smash.game.objects.ConsumableType;
import pewpew.smash.game.objects.ItemFactory;
import pewpew.smash.game.objects.RangedWeapon;
import pewpew.smash.game.objects.SpecialType;
import pewpew.smash.game.objects.special.AmmoStack;
import pewpew.smash.game.objects.special.Scope;

@ToString
public class Inventory {
    private RangedWeapon primaryWeapon;
    @Getter
    private final Map<ConsumableType, Integer> consumables;
    @Getter
    private AmmoStack ammoStack;
    @Getter
    @Setter
    private Scope scope;

    public Inventory() {
        this.consumables = new HashMap<>();
        this.ammoStack = new AmmoStack(0, "Ammo", "Stack of ammunition");
        this.scope = (Scope) ItemFactory.createItem(SpecialType.SCOPE_X1);
    }

    public void changeWeapon(RangedWeapon weapon) {
        this.primaryWeapon = weapon;
    }

    public Optional<RangedWeapon> getPrimaryWeapon() {
        return Optional.ofNullable(primaryWeapon);
    }

    public boolean hasPrimaryWeapon() {
        return primaryWeapon != null;
    }

    public Optional<RangedWeapon> removePrimaryWeapon() {
        RangedWeapon removedWeapon = primaryWeapon;
        primaryWeapon = null;
        return Optional.ofNullable(removedWeapon);
    }

    public boolean addConsumable(ConsumableType consumableType) {
        consumables.merge(consumableType, 1, Integer::sum);
        return true;
    }

    public Optional<Integer> getConsumableQuantity(ConsumableType consumableType) {
        return Optional.ofNullable(consumables.get(consumableType));
    }

    public Optional<ConsumableType> useConsumable(ConsumableType consumableType) {
        Integer count = consumables.get(consumableType);
        if (count != null && count > 0) {
            consumables.put(consumableType, count - 1);
            if (consumables.get(consumableType) == 0) {
                consumables.remove(consumableType);
            }
            return Optional.of(consumableType);
        }
        return Optional.empty();
    }

    public boolean hasConsumable(ConsumableType consumableType) {
        return consumables.containsKey(consumableType);
    }

    public void addAmmo(int amount) {
        ammoStack.setAmmo(ammoStack.getAmmo() + amount);
    }

    public int useAmmo(int requestedAmount) {
        int currentAmmo = ammoStack.getAmmo();
        int dispensedAmount = Math.min(requestedAmount, currentAmmo);
        ammoStack.setAmmo(currentAmmo - dispensedAmount);
        return dispensedAmount;
    }

    public int getAmmoCount() {
        return ammoStack.getAmmo();
    }

    public boolean isAmmoEmpty() {
        return ammoStack.getAmmo() == 0;
    }

    public void clearInventory() {
        primaryWeapon = null;
        consumables.clear();
        ammoStack.setAmmo(0);
    }

    public boolean isEmpty() {
        return primaryWeapon == null && consumables.isEmpty() && ammoStack.getAmmo() == 0;
    }
}