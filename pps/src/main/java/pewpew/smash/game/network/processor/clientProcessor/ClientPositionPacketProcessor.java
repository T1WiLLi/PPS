package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.client.ClientHandler;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.PositionPacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.utils.HelpMethods;

public class ClientPositionPacketProcessor extends ClientProcessor implements PacketProcessor<PositionPacket> {

    private final ClientHandler clientHandler;

    public ClientPositionPacketProcessor(EntityManager entityManager, ClientWrapper client,
            ClientHandler clientHandler) {
        super(entityManager, client);
        this.clientHandler = clientHandler;
    }

    @Override
    public void handle(Connection connection, PositionPacket packet) {
        Player player = getEntityManager().getPlayerEntity(packet.getId());
        if (player != null) {
            player.teleport(packet.getX(), packet.getY());
            player.setRotation(packet.getR());
            player.setDirection(HelpMethods.getDirectionFromByte(packet.getD()));
        } else {
            System.out.println("Queuing position update for player: " + packet.getId());
            clientHandler.queuePositionPacket(packet.getId(), packet);
        }
    }
}
