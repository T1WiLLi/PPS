package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.client.ClientHandler;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.PlayerJoinedPacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class ClientPlayerJoinedPacketProcessor extends ClientProcessor implements PacketProcessor<PlayerJoinedPacket> {

    private final ClientHandler clientHandler;

    public ClientPlayerJoinedPacketProcessor(EntityManager entityManager, ClientWrapper client,
            ClientHandler clientHandler) {
        super(entityManager, client);
        this.clientHandler = clientHandler;
    }

    @Override
    public void handle(Connection connection, PlayerJoinedPacket packet) {
        if (getEntityManager().getPlayerEntity(packet.getId()) == null) {
            Player player = new Player(packet.getId(), packet.getUsername());
            getEntityManager().addPlayerEntity(player.getId(), player);
            clientHandler.setCurrentBroadcastMessage(player.getUsername() + " has joined the game.");
            clientHandler.processQueuedPositionPackets(player.getId());
        }
    }
}
