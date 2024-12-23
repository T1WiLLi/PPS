package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import java.util.concurrent.CompletableFuture;

import pewpew.smash.game.entities.Bullet;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.BulletRemovePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class ClientBulletRemovePacketProcessor extends ClientProcessor implements PacketProcessor<BulletRemovePacket> {

    public ClientBulletRemovePacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, BulletRemovePacket packet) {
        Bullet bullet = getEntityManager().getBulletEntity(packet.getBulletID());
        if (bullet != null) {
            bullet.setShouldRenderEffect(true);

            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                getEntityManager().removeBulletEntity(bullet.getId());
            });
        }
    }
}