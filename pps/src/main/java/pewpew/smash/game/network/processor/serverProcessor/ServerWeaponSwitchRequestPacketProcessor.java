package pewpew.smash.game.network.processor.serverProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.WeaponStatePacket;
import pewpew.smash.game.network.packets.WeaponSwitchRequestPacket;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.processor.ServerProcessor;
import pewpew.smash.game.network.serializer.WeaponStateSerializer;
import pewpew.smash.game.network.server.ServerWrapper;

public class ServerWeaponSwitchRequestPacketProcessor extends ServerProcessor
        implements PacketProcessor<WeaponSwitchRequestPacket> {

    public ServerWeaponSwitchRequestPacketProcessor(EntityManager entityManager, ServerWrapper server) {
        super(entityManager, server);
    }

    @Override
    public void handle(Connection connection, WeaponSwitchRequestPacket packet) {
        Player player = getPlayer(connection);
        if (player != null && player.getInventory().getPrimaryWeapon().isPresent()) {
            switch (packet.getKeyCode()) {
                case 1 -> player.setEquippedWeapon(player.getFists());
                case 2 -> player.getInventory().getPrimaryWeapon().ifPresent(player::setEquippedWeapon);
            }

            WeaponStatePacket newWeaponState = WeaponStateSerializer
                    .serializeWeaponState(player.getEquippedWeapon());
            sendToAllTCP(newWeaponState);
        }
    }
}
