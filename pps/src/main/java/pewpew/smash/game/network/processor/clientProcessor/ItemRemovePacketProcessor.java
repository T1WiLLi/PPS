package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.manager.ItemManager;
import pewpew.smash.game.network.packets.ItemRemovePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class ItemRemovePacketProcessor extends ClientProcessor implements PacketProcessor {

    public ItemRemovePacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, Object packet) {
        if (packet instanceof ItemRemovePacket itemRemovePacket) {
            ItemManager.getInstance(false).removeItemByID(itemRemovePacket.getId());
        }
    }
}
