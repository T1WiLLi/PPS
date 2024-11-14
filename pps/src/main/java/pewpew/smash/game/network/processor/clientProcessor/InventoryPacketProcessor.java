package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Inventory;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.InventoryPacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.serializer.InventorySerializer;

public class InventoryPacketProcessor extends ClientProcessor implements PacketProcessor {

    public InventoryPacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, Object packet) {
        if (packet instanceof InventoryPacket inventoryPacket) {
            Player player = getEntityManager().getPlayerEntity(inventoryPacket.getPlayerID());
            if (player != null) {
                Inventory inventory = player.getInventory();
                InventorySerializer.deserializeInventory(inventoryPacket.getItems(), inventory);
            }
        }
    }
}
