package pewpew.smash.game.network.processor.serverProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.PlayerJoinedPacket;
import pewpew.smash.game.network.packets.PlayerUsernamePacket;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.processor.ServerProcessor;
import pewpew.smash.game.network.server.ServerLobbyManager;
import pewpew.smash.game.network.server.ServerWrapper;

public class ServerPlayerUsernamePacketProcessor extends ServerProcessor
        implements PacketProcessor<PlayerUsernamePacket> {

    private final ServerLobbyManager lobbyManager;

    public ServerPlayerUsernamePacketProcessor(EntityManager entityManager, ServerWrapper server,
            ServerLobbyManager lobbyManager) {
        super(entityManager, server);
        this.lobbyManager = lobbyManager;
    }

    @Override
    public void handle(Connection connection, PlayerUsernamePacket packet) {
        String rawUsername = packet.getUsername().trim();
        if (rawUsername.isEmpty()) {
            rawUsername = "Guest";
        }

        if (rawUsername.equalsIgnoreCase("Guest")) {
            rawUsername = "Guest-" + connection.getID();
        }

        if (lobbyManager.isLobbyActive()) {
            lobbyManager.addPlayer(connection.getID(), rawUsername);
        } else {
            Player player = getPlayer(connection);
            if (player != null) {
                player.setUsername(rawUsername);
                sendToAllUDP(new PlayerJoinedPacket(connection.getID(), packet.getUsername()));
            }
        }
    }
}
