package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.network.client.ClientHandler;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.BroadcastMessagePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class BroadcastMessagePacketProcessor extends ClientProcessor implements PacketProcessor {

    private final ClientHandler clientHandler;

    public BroadcastMessagePacketProcessor(EntityManager entityManager, ClientWrapper client,
            ClientHandler clientHandler) {
        super(entityManager, client);
        this.clientHandler = clientHandler;
    }

    @Override
    public void handle(Connection connection, Object packet) {
        if (packet instanceof BroadcastMessagePacket broadcastMessagePacket) {
            clientHandler.setCurrentBroadcastedMessage(broadcastMessagePacket.getMessage());
        }
    }
}