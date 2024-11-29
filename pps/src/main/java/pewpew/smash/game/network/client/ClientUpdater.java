package pewpew.smash.game.network.client;

import pewpew.smash.engine.controls.Direction;
import pewpew.smash.engine.controls.MouseInput;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.hud.HudManager;
import pewpew.smash.game.input.GamePad;
import pewpew.smash.game.input.MouseHandler;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.DirectionPacket;
import pewpew.smash.game.network.packets.MouseInputPacket;
import pewpew.smash.game.network.packets.PickupItemRequestPacket;
import pewpew.smash.game.network.packets.PreventActionForPlayerPacket;
import pewpew.smash.game.network.packets.ReloadWeaponRequestPacket;
import pewpew.smash.game.network.packets.UseConsumableRequestPacket;
import pewpew.smash.game.network.packets.WeaponStatePacket;
import pewpew.smash.game.network.packets.WeaponSwitchRequestPacket;
import pewpew.smash.game.network.serializer.WeaponStateSerializer;
import pewpew.smash.game.objects.RangedWeapon;
import pewpew.smash.game.utils.HelpMethods;

public class ClientUpdater {
    private final EntityManager entityManager;

    public ClientUpdater(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void update(ClientWrapper client) {
        this.entityManager.getPlayerEntities().forEach(Player::updateClient);
        if (!User.getInstance().isDead()) {
            sendDirection(client);
            sendWeaponSwitch(client);
            sendGamePadInput(client);
            sendMouseInput(client);
            sendWeaponState(client);
        }
    }

    private void sendDirection(ClientWrapper client) {
        Player localPlayer = entityManager.getPlayerEntity(User.getInstance().getLocalID().get());
        if (localPlayer != null) {
            Direction direction = GamePad.getInstance().getDirection();
            double rotation = MouseHandler.getSmoothedAngle(localPlayer.getX() + localPlayer.getWidth() / 2,
                    localPlayer.getY() + localPlayer.getHeight() / 2);
            client.sendToUDP(new DirectionPacket(direction, (float) rotation));
        }
    }

    private void sendWeaponSwitch(ClientWrapper client) {
        if (GamePad.getInstance().isSwitchWeaponOneKeyPressed()) {
            WeaponSwitchRequestPacket packet = new WeaponSwitchRequestPacket(1);
            client.sendToTCP(packet);
        } else if (GamePad.getInstance().isSwitchWeaponTwoKeyPressed()) {
            WeaponSwitchRequestPacket packet = new WeaponSwitchRequestPacket(2);
            client.sendToTCP(packet);
        }
    }

    private void sendGamePadInput(ClientWrapper client) {
        Player player = this.entityManager.getPlayerEntity(User.getInstance().getLocalID().get());

        if (player != null) {
            if (GamePad.getInstance().isReloadKeyPressed()
                    && player.getEquippedWeapon() instanceof RangedWeapon weapon) {
                boolean canReload = weapon.getCurrentAmmo() < weapon.getAmmoCapacity() && !player.hasAmmo();
                if (canReload) {
                    client.sendToTCP(new PreventActionForPlayerPacket(player.getId()));
                    HudManager.getInstance().startLoader((long) weapon.getReloadSpeed(), () -> {
                        client.sendToTCP(new ReloadWeaponRequestPacket());
                    }, player);
                }
            } else if (GamePad.getInstance().isUseKeyPressed()) {
                client.sendToTCP(new PickupItemRequestPacket());
            } else {
                Object[] consumableKeyResult = GamePad.getInstance().isConsumableKeysPressed();
                boolean isConsumableKeyPressed = (boolean) consumableKeyResult[0];

                if (isConsumableKeyPressed) {
                    int keyCode = (int) consumableKeyResult[1];
                    HelpMethods.getConsumableType(keyCode).ifPresent(consumableType -> {
                        boolean canUseConsumable = player.getHealth() < 100
                                && player.getInventory().hasConsumable(consumableType);
                        if (canUseConsumable) {
                            client.sendToTCP(new PreventActionForPlayerPacket(player.getId()));
                            HudManager.getInstance().startLoader(
                                    (long) consumableType.getUseTime(),
                                    () -> client.sendToTCP(new UseConsumableRequestPacket(keyCode)),
                                    player);
                        }
                    });
                }
            }
        }
    }

    private void sendMouseInput(ClientWrapper client) {
        MouseInput input = MouseInput.getCurrentInput();
        client.sendToUDP(new MouseInputPacket(input));
    }

    private void sendWeaponState(ClientWrapper client) {
        Player localPlayer = entityManager.getPlayerEntity(User.getInstance().getLocalID().get());
        if (localPlayer != null) {
            WeaponStatePacket packet = WeaponStateSerializer.serializeWeaponState(localPlayer.getEquippedWeapon());
            if (packet != null) {
                client.sendToUDP(packet);
            }
        }
    }
}
