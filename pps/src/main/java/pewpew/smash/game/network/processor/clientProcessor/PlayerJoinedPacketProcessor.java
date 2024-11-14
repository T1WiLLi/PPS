package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.client.ClientHandler;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.PlayerJoinedPacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class PlayerJoinedPacketProcessor extends ClientProcessor implements PacketProcessor {

    private final ClientHandler clientHandler;

    public PlayerJoinedPacketProcessor(EntityManager entityManager, ClientWrapper client, ClientHandler clientHandler) {
        super(entityManager, client);
        this.clientHandler = clientHandler;
    }

    @Override
    public void handle(Connection connection, Object packet) {
        if (packet instanceof PlayerJoinedPacket playerJoinedPacket) {
            if (getEntityManager().getPlayerEntity(playerJoinedPacket.getId()) == null) {
                Player player = new Player(playerJoinedPacket.getId(), playerJoinedPacket.getUsername());
                getEntityManager().addPlayerEntity(player.getId(), player);
                clientHandler.setCurrentBroadcastedMessage(player.getUsername() + " has joined the game.");
                clientHandler.processQueuedPositionPackets(player.getId());
            }
        }
    }
}
