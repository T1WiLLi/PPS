package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.client.ClientHandler;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.PositionPacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class PositionPacketProcessor extends ClientProcessor implements PacketProcessor {

    private final ClientHandler clientHandler;

    public PositionPacketProcessor(EntityManager entityManager, ClientWrapper client, ClientHandler clientHandler) {
        super(entityManager, client);
        this.clientHandler = clientHandler;
    }

    @Override
    public void handle(Connection connection, Object packet) {
        if (packet instanceof PositionPacket positionPacket) {
            Player player = getEntityManager().getPlayerEntity(positionPacket.getId());
            if (player != null) {
                player.teleport(positionPacket.getX(), positionPacket.getY());
                player.setRotation(positionPacket.getR());
            } else {
                System.out.println("Queuing position update for player: " + positionPacket.getId());
                clientHandler.queuePositionPacket(positionPacket.getId(), positionPacket);
            }
        }
    }
}
