package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Bullet;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.BulletCreatePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class ClientBulletCreatePacketProcessor extends ClientProcessor implements PacketProcessor<BulletCreatePacket> {

    public ClientBulletCreatePacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, BulletCreatePacket packet) {
        Player owner = getEntityManager().getPlayerEntity(packet.getOwnerID());
        if (owner != null) {
            Bullet bullet = new Bullet(owner);
            bullet.setId(packet.getBulletID());
            bullet.teleport(packet.getX(), packet.getY());
            getEntityManager().addBulletEntity(packet.getBulletID(), bullet);
        }
    }
}
