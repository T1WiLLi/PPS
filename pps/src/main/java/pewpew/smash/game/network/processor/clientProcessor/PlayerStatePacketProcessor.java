package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.model.PlayerState;
import pewpew.smash.game.network.packets.PlayerStatePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class PlayerStatePacketProcessor extends ClientProcessor implements PacketProcessor {

    public PlayerStatePacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, Object packet) {
        if (packet instanceof PlayerStatePacket playerStatePacket) {
            PlayerState newState = playerStatePacket.getState();
            Player player = getEntityManager().getPlayerEntity(newState.getId());
            if (player != null) {
                player.applyState(newState);
            }
        }
    }
}
