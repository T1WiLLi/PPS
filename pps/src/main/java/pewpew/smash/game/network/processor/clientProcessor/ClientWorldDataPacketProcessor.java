package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.network.client.ClientHandler;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.WorldDataPacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.world.WorldGenerator;

public class ClientWorldDataPacketProcessor extends ClientProcessor implements PacketProcessor<WorldDataPacket> {

    private final ClientHandler clientHandler;

    public ClientWorldDataPacketProcessor(EntityManager entityManager, ClientWrapper client,
            ClientHandler clientHandler) {
        super(entityManager, client);
        this.clientHandler = clientHandler;
    }

    @Override
    public void handle(Connection connection, WorldDataPacket packet) {
        WorldGenerator worldGenerator = new WorldGenerator(packet.getSeed());
        clientHandler.setWorldData(worldGenerator.getWorldData());
        clientHandler.setWorldDataReceived(true);
    }
}
