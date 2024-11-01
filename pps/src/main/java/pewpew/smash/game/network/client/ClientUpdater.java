package pewpew.smash.game.network.client;

import pewpew.smash.engine.controls.Direction;
import pewpew.smash.engine.controls.MouseInput;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.input.GamePad;
import pewpew.smash.game.input.MouseHandler;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.DirectionPacket;
import pewpew.smash.game.network.packets.MouseInputPacket;
import pewpew.smash.game.network.packets.WeaponStatePacket;
import pewpew.smash.game.network.packets.WeaponSwitchRequestPacket;
import pewpew.smash.game.network.serializer.WeaponStateSerializer;

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
            sendMouseInput(client);
            sendWeaponState(client);
        }
    }

    private void sendDirection(ClientWrapper client) {
        Player localPlayer = entityManager.getPlayerEntity(User.getInstance().getLocalID().get());
        if (localPlayer != null) {
            Direction direction = GamePad.getInstance().getDirection();
            double rotation = MouseHandler.getAngle(localPlayer.getX() + localPlayer.getWidth() / 2,
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

    private void sendMouseInput(ClientWrapper client) {
        MouseInput input = MouseInput.getCurrentInput();
        client.sendToUDP(new MouseInputPacket(input));
    }

    private void sendWeaponState(ClientWrapper client) {
        Player localPlayer = entityManager.getPlayerEntity(User.getInstance().getLocalID().get());
        if (localPlayer != null) {
            WeaponStatePacket packet = WeaponStateSerializer.serializeWeaponState(localPlayer);
            if (packet != null) {
                client.sendToUDP(packet);
            }
        }
    }
}
