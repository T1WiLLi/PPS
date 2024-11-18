package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import pewpew.smash.game.entities.Bullet;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.BulletRemovePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class BulletRemovePacketProcessor extends ClientProcessor implements PacketProcessor {

    private final ScheduledExecutorService scheduler;

    public BulletRemovePacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void handle(Connection connection, Object packet) {
        if (packet instanceof BulletRemovePacket bulletRemovePacket) {
            Bullet bullet = getEntityManager().getBulletEntity(bulletRemovePacket.getBulletID());
            if (bullet != null) {
                bullet.setShouldRenderEffect(true);

                scheduler.schedule(() -> {
                    getEntityManager().removeBulletEntity(bullet.getId());
                }, 500, TimeUnit.MILLISECONDS);
            }
        }
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
