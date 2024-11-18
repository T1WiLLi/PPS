package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.client.ClientHandler;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.PlayerLeftPacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class ClientPlayerLeftPacketProcessor extends ClientProcessor implements PacketProcessor<PlayerLeftPacket> {

    private final ClientHandler clientHandler;

    public ClientPlayerLeftPacketProcessor(EntityManager entityManager, ClientWrapper client,
            ClientHandler clientHandler) {
        super(entityManager, client);
        this.clientHandler = clientHandler;
    }

    @Override
    public void handle(Connection connection, PlayerLeftPacket packet) {
        Player player = getEntityManager().getPlayerEntity(packet.getId());
        if (player != null) {
            clientHandler.setCurrentBroadcastedMessage(player.getUsername() + " has left the game.");
            getEntityManager().removePlayerEntity(packet.getId());
        }
    }
}
