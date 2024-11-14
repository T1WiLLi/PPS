package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.MouseActionPacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class MouseActionPacketProcessor extends ClientProcessor implements PacketProcessor {

    public MouseActionPacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, Object packet) {
        if (packet instanceof MouseActionPacket mouseActionPacket) {
            Player player = getEntityManager().getPlayerEntity(mouseActionPacket.getPlayerID());
            if (player != null) {
                player.setMouseInput(mouseActionPacket.getMouseInput());
            } else {
                System.out.println(
                        "Cannot process mouse action for non-existent player: " + mouseActionPacket.getPlayerID());
            }
        }
    }
}
