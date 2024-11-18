package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.MouseActionPacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class ClientMouseActionPacketProcessor extends ClientProcessor implements PacketProcessor<MouseActionPacket> {

    public ClientMouseActionPacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, MouseActionPacket packet) {
        Player player = getEntityManager().getPlayerEntity(packet.getPlayerID());
        if (player != null) {
            player.setMouseInput(packet.getMouseInput());
        } else {
            System.out.println(
                    "Cannot process mouse action for non-existent player: " + packet.getPlayerID());
        }
    }
}
