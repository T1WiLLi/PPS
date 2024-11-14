package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Bullet;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.BulletCreatePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class BulletCreatePacketProcessor extends ClientProcessor implements PacketProcessor {

    public BulletCreatePacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, Object packet) {
        if (packet instanceof BulletCreatePacket bulletCreatePacket) {
            Player owner = getEntityManager().getPlayerEntity(bulletCreatePacket.getOwnerID());
            if (owner != null) {
                Bullet bullet = new Bullet(owner);
                bullet.setId(bulletCreatePacket.getBulletID());
                bullet.teleport(bulletCreatePacket.getX(), bulletCreatePacket.getY());
                getEntityManager().addBulletEntity(bulletCreatePacket.getBulletID(), bullet);
            }
        }
    }
}
