package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;
import pewpew.smash.game.network.client.ClientHandler;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.client.ClientLobbyManager;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.StartGamePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.utils.HelpMethods;

public class ClientStartGamePacketProcessor extends ClientProcessor implements PacketProcessor<StartGamePacket> {

    public ClientStartGamePacketProcessor(EntityManager entityManager, ClientWrapper client,
            ClientHandler clientHandler) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, StartGamePacket packet) {
        ClientLobbyManager.getInstance().onStartGame(HelpMethods.getGameModeTypeFromString(packet.getMode()));
    }
}