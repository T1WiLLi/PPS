package pewpew.smash.game.network.processor.serverProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.PlayerJoinedPacket;
import pewpew.smash.game.network.packets.PlayerUsernamePacket;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.processor.ServerProcessor;
import pewpew.smash.game.network.server.ServerWrapper;

public class UsernamePacketProcessor extends ServerProcessor implements PacketProcessor {

    public UsernamePacketProcessor(EntityManager entityManager, ServerWrapper server) {
        super(entityManager, server);
    }

    @Override
    public void handle(Connection connection, Object packet) {
        if (packet instanceof PlayerUsernamePacket usernamePacket) {
            Player player = getPlayer(connection);
            if (player != null) {
                player.setUsername(usernamePacket.getUsername());
                sendToAllUDP(new PlayerJoinedPacket(connection.getID(), usernamePacket.getUsername()));
            }
        }
    }
}
