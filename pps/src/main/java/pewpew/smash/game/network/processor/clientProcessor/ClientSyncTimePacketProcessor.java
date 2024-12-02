package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.SyncTimePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.server.ServerTime;

public class ClientSyncTimePacketProcessor extends ClientProcessor implements PacketProcessor<SyncTimePacket> {

    public ClientSyncTimePacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, SyncTimePacket packet) {
        ServerTime.getInstance().setTime(packet.getTime());
    }
}
