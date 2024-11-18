package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.model.WorldEntityState;
import pewpew.smash.game.network.packets.WorldEntityStatePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.world.entities.WorldBreakableStaticEntity;

public class ClientWorldEntityStatePacketProcessor extends ClientProcessor
        implements PacketProcessor<WorldEntityStatePacket> {

    public ClientWorldEntityStatePacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, WorldEntityStatePacket packet) {
        WorldEntityState newState = packet.getState();
        WorldBreakableStaticEntity entity = (WorldBreakableStaticEntity) getEntityManager()
                .getStaticEntity(newState.getId());
        if (entity != null) {
            entity.applyState(newState);
        }
    }
}
