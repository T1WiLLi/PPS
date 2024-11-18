package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.model.PlayerState;
import pewpew.smash.game.network.packets.PlayerStatePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class ClientPlayerStatePacketProcessor extends ClientProcessor implements PacketProcessor<PlayerStatePacket> {

    public ClientPlayerStatePacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, PlayerStatePacket packet) {
        PlayerState newState = packet.getState();
        Player player = getEntityManager().getPlayerEntity(newState.getId());
        if (player != null) {
            player.applyState(newState);
        }
    }
}
