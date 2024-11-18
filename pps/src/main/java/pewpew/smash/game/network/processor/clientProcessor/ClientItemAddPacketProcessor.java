package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.manager.ItemManager;
import pewpew.smash.game.network.model.SerializedItem;
import pewpew.smash.game.network.packets.ItemAddPacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.serializer.SerializationUtility;
import pewpew.smash.game.objects.Item;

public class ClientItemAddPacketProcessor extends ClientProcessor implements PacketProcessor<ItemAddPacket> {

    public ClientItemAddPacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, ItemAddPacket packet) {
        SerializedItem serializedItem = packet.getSerializedItem();
        Item item = SerializationUtility.deserializeItem(serializedItem);
        item.teleport(packet.getX(), packet.getY());
        ItemManager.getInstance(false).addItem(item);
    }
}
