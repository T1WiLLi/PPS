package pewpew.smash.game.network.processor.serverProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.DirectionPacket;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.processor.ServerProcessor;
import pewpew.smash.game.network.server.ServerWrapper;

public class DirectionPacketProcessor extends ServerProcessor implements PacketProcessor {

    public DirectionPacketProcessor(EntityManager entityManager, ServerWrapper server) {
        super(entityManager, server);
    }

    @Override
    public void handle(Connection connection, Object packet) {
        if (packet instanceof DirectionPacket directionPacket) {
            Player player = getPlayer(connection);
            if (player != null) {
                player.setDirection(directionPacket.getDirection());
                player.setRotation(directionPacket.getRotation());
            }
        }
    }
}
