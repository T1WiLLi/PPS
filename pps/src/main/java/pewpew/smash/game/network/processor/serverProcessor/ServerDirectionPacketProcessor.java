package pewpew.smash.game.network.processor.serverProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.DirectionPacket;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.processor.ServerProcessor;
import pewpew.smash.game.network.server.ServerWrapper;

public class ServerDirectionPacketProcessor extends ServerProcessor implements PacketProcessor<DirectionPacket> {

    public ServerDirectionPacketProcessor(EntityManager entityManager, ServerWrapper server) {
        super(entityManager, server);
    }

    @Override
    public void handle(Connection connection, DirectionPacket packet) {
        Player player = getPlayer(connection);
        if (player != null) {
            player.setDirection(packet.getDirection());
            player.setRotation(packet.getRotation());
        }
    }
}
