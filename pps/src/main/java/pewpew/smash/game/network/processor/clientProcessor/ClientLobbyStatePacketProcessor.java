package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;
import pewpew.smash.game.network.client.ClientHandler;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.client.ClientLobbyManager;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.LobbyStatePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class ClientLobbyStatePacketProcessor extends ClientProcessor implements PacketProcessor<LobbyStatePacket> {

    public ClientLobbyStatePacketProcessor(EntityManager entityManager, ClientWrapper client,
            ClientHandler clientHandler) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, LobbyStatePacket packet) {
        ClientLobbyManager.getInstance().updateLobbyState(packet.getPlayerNames(), packet.getCountdownRemaining());
    }
}
