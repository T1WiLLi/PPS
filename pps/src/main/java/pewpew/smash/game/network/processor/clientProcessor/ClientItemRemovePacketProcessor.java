package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.manager.ItemManager;
import pewpew.smash.game.network.packets.ItemRemovePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class ClientItemRemovePacketProcessor extends ClientProcessor implements PacketProcessor<ItemRemovePacket> {

    public ClientItemRemovePacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, ItemRemovePacket packet) {
        ItemManager.getInstance(false).removeItemByID(packet.getId());
    }
}
