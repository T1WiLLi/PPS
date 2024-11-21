package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.model.SerializedWorldStaticEntity;
import pewpew.smash.game.network.packets.WorldEntityAddPacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.serializer.WorldStaticEntitySerializer;
import pewpew.smash.game.world.entities.WorldStaticEntity;

public class ClientWorldEntityAddPacketProcessor extends ClientProcessor
        implements PacketProcessor<WorldEntityAddPacket> {

    public ClientWorldEntityAddPacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, WorldEntityAddPacket packet) {
        SerializedWorldStaticEntity serializedEntity = packet.getEntity();
        WorldStaticEntity entity = WorldStaticEntitySerializer.deserialize(serializedEntity);
        getEntityManager().addStaticEntity(entity.getId(), entity);
    }
}
