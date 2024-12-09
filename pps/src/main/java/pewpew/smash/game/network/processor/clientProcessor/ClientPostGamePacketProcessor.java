package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;
import pewpew.smash.game.network.client.ClientPostGameManager;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.PostGamePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class ClientPostGamePacketProcessor extends ClientProcessor implements PacketProcessor<PostGamePacket> {

    public ClientPostGamePacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, PostGamePacket packet) {
        ClientPostGameManager.getInstance().startPostGame(packet.getWinnerName(), packet.getAllPlayers());
    }
}
