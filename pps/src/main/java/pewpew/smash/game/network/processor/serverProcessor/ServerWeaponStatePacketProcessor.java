package pewpew.smash.game.network.processor.serverProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.WeaponStatePacket;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.processor.ServerProcessor;
import pewpew.smash.game.network.serializer.WeaponStateSerializer;
import pewpew.smash.game.network.server.ServerWrapper;

public class ServerWeaponStatePacketProcessor extends ServerProcessor implements PacketProcessor<WeaponStatePacket> {

    public ServerWeaponStatePacketProcessor(EntityManager entityManager, ServerWrapper server) {
        super(entityManager, server);
    }

    @Override
    public void handle(Connection connection, WeaponStatePacket packet) {
        Player player = getPlayer(connection);
        if (player != null) {
            WeaponStateSerializer.deserializeWeaponState(packet, player);
        }
    }
}
