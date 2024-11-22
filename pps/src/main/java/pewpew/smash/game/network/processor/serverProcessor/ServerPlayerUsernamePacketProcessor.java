package pewpew.smash.game.network.processor.serverProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.PlayerJoinedPacket;
import pewpew.smash.game.network.packets.PlayerUsernamePacket;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.processor.ServerProcessor;
import pewpew.smash.game.network.server.ServerWrapper;

public class ServerPlayerUsernamePacketProcessor extends ServerProcessor
        implements PacketProcessor<PlayerUsernamePacket> {

    public ServerPlayerUsernamePacketProcessor(EntityManager entityManager, ServerWrapper server) {
        super(entityManager, server);
    }

    @Override
    public void handle(Connection connection, PlayerUsernamePacket packet) {
        Player player = getPlayer(connection);
        if (player != null) {
            player.setUsername(packet.getUsername()
                    + ((packet.getUsername().equals("Guest")) ? "-" + player.getId() : ""));
            sendToAllUDP(new PlayerJoinedPacket(connection.getID(), packet.getUsername()));
        }
    }
}
