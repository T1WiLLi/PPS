package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.engine.entities.MovableEntity;
import pewpew.smash.game.entities.Plane;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.PlaneStatePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class ClientPlaneStatePacketProcessor extends ClientProcessor implements PacketProcessor<PlaneStatePacket> {

    public ClientPlaneStatePacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, PlaneStatePacket packet) {
        Plane plane = new Plane();
        plane.teleport(packet.getX(), packet.getY());
        plane.setDirection(packet.getDir());
        plane.setRotation(packet.getR());

        getEntityManager().addMovableEntity(getEntityManager().getNextID(MovableEntity.class), plane);
    }
}
