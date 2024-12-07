package pewpew.smash.game.network.processor.clientProcessor;

import java.util.Timer;
import java.util.TimerTask;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.engine.entities.StaticEntity;
import pewpew.smash.game.entities.Parachute;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.CrateDropPacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.serializer.WorldStaticEntitySerializer;
import pewpew.smash.game.world.entities.WorldStaticEntity;

public class ClientCrateDropPacketProcessor extends ClientProcessor implements PacketProcessor<CrateDropPacket> {

    public ClientCrateDropPacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, CrateDropPacket packet) {
        Parachute chute = new Parachute();
        chute.teleport(packet.getEntity().getX() - (40 + 46), packet.getEntity().getY() - (40 + 46));

        int id = getEntityManager().getNextID(StaticEntity.class);
        getEntityManager().addStaticEntity(id, chute);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getEntityManager().removeStaticEntity(id);
                WorldStaticEntity entity = WorldStaticEntitySerializer.deserialize(packet.getEntity());
                getEntityManager().addStaticEntity(entity.getId(), entity);
            }
        }, 2000);
    }
}
