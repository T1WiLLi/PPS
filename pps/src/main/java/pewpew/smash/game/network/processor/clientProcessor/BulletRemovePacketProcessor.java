package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.BulletRemovePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class BulletRemovePacketProcessor extends ClientProcessor implements PacketProcessor {

    public BulletRemovePacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, Object packet) {
        if (packet instanceof BulletRemovePacket bulletRemovePacket) {
            getEntityManager().removeBulletEntity(bulletRemovePacket.getBulletID());
        }
    }
}
