package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.WorldEntityRemovePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class ClientWorldEntityRemovePacketProcessor extends ClientProcessor
        implements PacketProcessor<WorldEntityRemovePacket> {

    public ClientWorldEntityRemovePacketProcessor(EntityManager entityManager, ClientWrapper clientWrapper) {
        super(entityManager, clientWrapper);
    }

    @Override
    public void handle(Connection connection, WorldEntityRemovePacket packet) {
        getEntityManager().removeStaticEntity(packet.getEntityId());
    }
}
