package pewpew.smash.game.utils;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import pewpew.smash.engine.controls.Direction;
import pewpew.smash.engine.controls.MouseController;
import pewpew.smash.engine.controls.MouseInput;
import pewpew.smash.game.Animation.PlayerAnimation;
import pewpew.smash.game.Animation.PlayerAnimationState;
import pewpew.smash.game.entities.Inventory;
import pewpew.smash.game.entities.Plane;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.gamemode.GameModeType;
import pewpew.smash.game.input.GamePad;
import pewpew.smash.game.network.packets.ItemAddPacket;
import pewpew.smash.game.network.serializer.SerializationUtility;
import pewpew.smash.game.network.server.ServerWrapper;
import pewpew.smash.game.objects.Consumable;
import pewpew.smash.game.objects.ConsumableType;
import pewpew.smash.game.objects.Item;
import pewpew.smash.game.objects.ItemFactory;
import pewpew.smash.game.objects.MeleeWeapon;
import pewpew.smash.game.objects.RangedWeapon;
import pewpew.smash.game.objects.WeaponType;
import pewpew.smash.game.objects.special.AmmoStack;
import pewpew.smash.game.objects.special.Scope;
import pewpew.smash.game.world.WorldGenerator;

public class HelpMethods {

    private static final Map<Player, Long> reloadTimers = new HashMap<>();

    public static boolean isIn(Rectangle bounds) {
        return bounds.getBounds().contains(MouseController.getMouseX(), MouseController.getMouseY());
    }

    public static int getRandomBetween(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    public static Optional<ConsumableType> getConsumableType(int code) {
        return switch (code) {
            case KeyEvent.VK_3 -> Optional.of(ConsumableType.MEDIKIT);
            case KeyEvent.VK_4 -> Optional.of(ConsumableType.BANDAGE);
            case KeyEvent.VK_5 -> Optional.of(ConsumableType.PILL);
            default -> Optional.empty();
        };
    }

    public static GameModeType getGameModeTypeFromString(String str) {
        return Stream.of(GameModeType.values())
                .filter(g -> g.name().equalsIgnoreCase(str.trim().replace(" ", "_")))
                .findFirst()
                .orElse(GameModeType.SANDBOX); // default
    }

    public static void sendDroppedItem(Item item, ServerWrapper server) {
        spreadItem(item);
        server.sendToAllTCP(new ItemAddPacket(
                item.getX(),
                item.getY(),
                SerializationUtility.serializeItem(item)));
    }

    public static void dropInventoryOfDeadPlayer(Inventory inventory, ServerWrapper server) {
        inventory.getPrimaryWeapon().ifPresent(weapon -> {
            weapon.drop();
            HelpMethods.spreadItem(weapon);
            server.sendToAllTCP(new ItemAddPacket(
                    weapon.getX(),
                    weapon.getY(),
                    SerializationUtility.serializeItem(weapon)));
        });

        inventory.getConsumables().forEach((consumableType, quantity) -> {
            IntStream.range(0, quantity)
                    .mapToObj(i -> {
                        Consumable consumable = ItemFactory.createItem(consumableType);
                        consumable.pickup(inventory.getOwner());
                        consumable.drop();
                        HelpMethods.spreadItem(consumable);
                        server.sendToAllTCP(new ItemAddPacket(
                                consumable.getX(),
                                consumable.getY(),
                                SerializationUtility.serializeItem(consumable)));
                        return consumable;
                    });
        });

        if (inventory.getAmmoCount() > 0) {
            AmmoStack ammoStack = inventory.getAmmoStack();
            ammoStack.drop();
            HelpMethods.spreadItem(ammoStack);
            server.sendToAllTCP(new ItemAddPacket(
                    ammoStack.getX(),
                    ammoStack.getY(),
                    SerializationUtility.serializeItem(ammoStack)));
        }

        Scope scope = inventory.getScope();
        scope.drop();
        HelpMethods.spreadItem(scope);
        server.sendToAllTCP(new ItemAddPacket(
                scope.getX(),
                scope.getY(),
                SerializationUtility.serializeItem(scope)));
    }

    public static boolean isBigGun(RangedWeapon weapon) {
        if (weapon.getType() == WeaponType.AK47 || weapon.getType() == WeaponType.M1A1
                || weapon.getType() == WeaponType.HK416 || weapon.getType() == WeaponType.M4A1
                || weapon.getType() == WeaponType.DEAGLE) {
            return true;
        }
        return false;
    }

    public static Plane generatePlane() {
        Plane plane = new Plane();
        Random random = new Random();

        int worldWidth = WorldGenerator.getWorldWidth();
        int worldHeight = WorldGenerator.getWorldHeight();

        int centerX = worldWidth / 2;
        int centerY = worldHeight / 2;

        int x = 0, y = 0;
        Direction direction = null;

        int path = random.nextInt(4);
        path = 5;

        switch (path) {
            case 0:
                x = -plane.getWidth();
                y = centerY - plane.getHeight() / 2;
                direction = Direction.RIGHT;
                break;
            case 1:
                x = worldWidth;
                y = centerY - plane.getHeight() / 2;
                direction = Direction.LEFT;
                break;
            case 2:
                x = centerX - plane.getWidth() / 2;
                y = -plane.getHeight();
                direction = Direction.DOWN;
                break;
            case 3:
                x = centerX - plane.getWidth() / 2;
                y = worldHeight;
                direction = Direction.UP;
                break;
        }

        float rotation = getRotationFromDirection(direction);

        plane.teleport(x, y);
        plane.setRotation(rotation);
        plane.setDirection(direction);

        return plane;
    }

    public static byte getDirectionToByte(Direction direction) {
        return (byte) Arrays.asList(Direction.values()).indexOf(direction);
    }

    public static Direction getDirectionFromByte(byte d) {
        return Direction.values()[(int) d];
    }

    public static PlayerAnimation getCurrentAnimation(Player player) {
        if (player.getEquippedWeapon() instanceof RangedWeapon rangedWeapon) {
            double reloadTime = rangedWeapon.getType().getReloadSpeed().orElse(1.5);
            long currentTime = System.currentTimeMillis();

            if (GamePad.getInstance().isReloadKeyPressed() && rangedWeapon.canReload()
                    && !player.getInventory().isAmmoEmpty()) {
                reloadTimers.put(player, currentTime);
                return PlayerAnimation.RELOAD;
            }

            if (reloadTimers.containsKey(player)) {
                long startTime = reloadTimers.get(player);
                if (currentTime - startTime < reloadTime * 1000) {
                    return PlayerAnimation.RELOAD;
                } else {
                    reloadTimers.remove(player);
                }
            }
        }

        if (isShooting(player)) {
            if (player.getEquippedWeapon() instanceof MeleeWeapon) {
                return PlayerAnimation.MELEEATTACK;
            } else {
                return PlayerAnimation.SHOOT;
            }
        }

        if (player.getDirection() != Direction.NONE) {
            return PlayerAnimation.MOVE;
        }
        return PlayerAnimation.IDLE;
    }

    public static PlayerAnimationState getCurrentState(Player player) {
        if (player.getEquippedWeapon() instanceof MeleeWeapon) {
            return PlayerAnimationState.KNIFE;
        } else {
            if (((RangedWeapon) player.getEquippedWeapon()).getProperties().isTwoHanded()) {
                if (((RangedWeapon) player.getEquippedWeapon()).getType() == WeaponType.M4A1) {
                    return PlayerAnimationState.SHOTGUN;
                } else {
                    return PlayerAnimationState.RIFLE;
                }
            } else {
                return PlayerAnimationState.HANDGUN;
            }
        }
    }

    private static boolean isShooting(Player player) {
        if (player.getEquippedWeapon() instanceof MeleeWeapon meleeWeapon) {
            return meleeWeapon.isAttacking();
        } else if (player.getEquippedWeapon() instanceof RangedWeapon rangedWeapon) {
            return player.getMouseInput() == MouseInput.LEFT_CLICK
                    && rangedWeapon.canShoot();
        }
        return false;
    }

    private static float getRotationFromDirection(Direction direction) {
        return switch (direction) {
            case UP -> 0f;
            case DOWN -> 180f;
            case LEFT -> 270f;
            case RIGHT -> 90f;
            case UP_RIGHT -> 45f;
            case UP_LEFT -> 315f;
            case DOWN_RIGHT -> 135f;
            case DOWN_LEFT -> 225f;
            default -> 0f;
        };
    }

    private static void spreadItem(Item item) {
        int offsetX = (int) (Math.random() * 80 - 40);
        int offsetY = (int) (Math.random() * 80 - 40);
        item.teleport(item.getX() + offsetX, item.getY() + offsetY);
    }
}
