package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.client.ClientHandler;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.PlayerLeftPacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class PlayerLeftPacketProcessor extends ClientProcessor implements PacketProcessor {

    private final ClientHandler clientHandler;

    public PlayerLeftPacketProcessor(EntityManager entityManager, ClientWrapper client, ClientHandler clientHandler) {
        super(entityManager, client);
        this.clientHandler = clientHandler;
    }

    @Override
    public void handle(Connection connection, Object packet) {
        if (packet instanceof PlayerLeftPacket playerLeftPacket) {
            Player player = getEntityManager().getPlayerEntity(playerLeftPacket.getId());
            if (player != null) {
                clientHandler.setCurrentBroadcastedMessage(player.getUsername() + " has left the game.");
                getEntityManager().removePlayerEntity(playerLeftPacket.getId());
            }
        }
    }
}
