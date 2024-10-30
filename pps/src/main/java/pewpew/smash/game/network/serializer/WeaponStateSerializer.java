package pewpew.smash.game.network.serializer;

import java.util.Map;
import java.util.HashMap;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.packets.WeaponStatePacket;
import pewpew.smash.game.objects.ItemFactory;
import pewpew.smash.game.objects.MeleeWeapon;
import pewpew.smash.game.objects.RangedWeapon;
import pewpew.smash.game.objects.Weapon;
import pewpew.smash.game.objects.WeaponType;

public class WeaponStateSerializer {
    public static WeaponStatePacket serializeWeaponState(Player player) {
        Weapon weapon = player.getEquippedWeapon();
        if (weapon == null) {
            return null;
        }

        Map<String, Object> stateData = new HashMap<>();
        WeaponType weaponType = getWeaponType(weapon);

        stateData.put("damage", weapon.getDamage());
        stateData.put("range", weapon.getRange());
        stateData.put("attackSpeed", weapon.getAttackSpeed());

        if (weapon instanceof MeleeWeapon) {
            MeleeWeapon meleeWeapon = (MeleeWeapon) weapon;
            stateData.put("isAttacking", meleeWeapon.isAttacking());
            stateData.put("isReturning", meleeWeapon.isReturning());
            stateData.put("attackProgress", meleeWeapon.getAttackProgress());
        } else if (weapon instanceof RangedWeapon) {
            RangedWeapon rangedWeapon = (RangedWeapon) weapon;
            stateData.put("ammoCapacity", rangedWeapon.getAmmoCapacity());
            stateData.put("currentAmmo", rangedWeapon.getCurrentAmmo());
            stateData.put("reloadSpeed", rangedWeapon.getReloadSpeed());
        }

        return new WeaponStatePacket(weaponType, stateData);
    }

    public static void deserializeWeaponState(WeaponStatePacket weaponState, Player player) {
        Weapon weapon = player.getEquippedWeapon();
        WeaponType weaponType = weaponState.getWeaponType();

        if (weapon == null || getWeaponType(weapon) != weaponType) {
            weapon = ItemFactory.createItem(weaponState.getWeaponType());
            weapon.pickup(player);
            player.setEquippedWeapon(weapon);
        }

        Map<String, Object> stateData = weaponState.getWeaponStateData();

        if (weapon instanceof MeleeWeapon) {
            MeleeWeapon meleeWeapon = (MeleeWeapon) weapon;
            meleeWeapon.buildWeapon(
                    (int) stateData.get("damage"),
                    ((Number) stateData.get("attackSpeed")).doubleValue(),
                    (int) stateData.get("range"));

            meleeWeapon.setAttacking((boolean) stateData.get("isAttacking"));
            meleeWeapon.setReturning((boolean) stateData.get("isReturning"));
            meleeWeapon.setAttackProgress(((Number) stateData.get("attackProgress")).floatValue());
        } else if (weapon instanceof RangedWeapon) {
            RangedWeapon rangedWeapon = (RangedWeapon) weapon;
            rangedWeapon.buildWeapon(
                    (int) stateData.get("damage"),
                    ((Number) stateData.get("attackSpeed")).doubleValue(),
                    (int) stateData.get("range"),
                    ((Number) stateData.get("reloadSpeed")).doubleValue(),
                    (int) stateData.get("ammoCapacity"));
            rangedWeapon.setCurrentAmmo((int) stateData.get("currentAmmo"));
        }
    }

    private static WeaponType getWeaponType(Weapon weapon) {
        for (WeaponType weaponType : WeaponType.values()) {
            if (matchesAttributes(weapon, weaponType)) {
                return weaponType;
            }
        }
        return null;
    }

    private static boolean matchesAttributes(Weapon weapon, WeaponType weaponType) {
        return weapon.getDamage() == weaponType.getDamage() &&
                weapon.getRange() == weaponType.getRange() &&
                weapon.getAttackSpeed() == weaponType.getAttackSpeed();
    }
}
