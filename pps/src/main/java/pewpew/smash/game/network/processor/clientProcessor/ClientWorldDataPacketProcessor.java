package pewpew.smash.game.network.processor.clientProcessor;

import java.util.concurrent.CompletableFuture;
import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.WorldDataPacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.world.WorldClientIntegration;
import pewpew.smash.game.world.WorldGenerator;

public class ClientWorldDataPacketProcessor extends ClientProcessor implements PacketProcessor<WorldDataPacket> {

    public ClientWorldDataPacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, WorldDataPacket packet) {
        CompletableFuture.runAsync(() -> {
            WorldGenerator worldGenerator = new WorldGenerator(packet.getSeed());
            WorldClientIntegration.getInstance().buildImage(worldGenerator.getWorldData());
        });
    }
}
