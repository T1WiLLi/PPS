package pewpew.smash.game.network.processor.serverProcessor;

import java.util.Optional;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.model.PlayerState;
import pewpew.smash.game.network.packets.InventoryPacket;
import pewpew.smash.game.network.packets.PlayerStatePacket;
import pewpew.smash.game.network.packets.UseConsumableRequestPacket;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.processor.ServerProcessor;
import pewpew.smash.game.network.serializer.InventorySerializer;
import pewpew.smash.game.network.server.ServerWrapper;
import pewpew.smash.game.objects.Consumable;
import pewpew.smash.game.objects.ConsumableType;
import pewpew.smash.game.objects.ItemFactory;
import pewpew.smash.game.utils.HelpMethods;

public class ServerUseConsumableRequestPacketProcessor extends ServerProcessor
        implements PacketProcessor<UseConsumableRequestPacket> {

    public ServerUseConsumableRequestPacketProcessor(EntityManager entityManager, ServerWrapper server) {
        super(entityManager, server);
    }

    @Override
    public void handle(Connection connection, UseConsumableRequestPacket packet) {
        Player player = getPlayer(connection);
        if (player != null && player.getHealth() < 100) {
            Optional<ConsumableType> type = HelpMethods
                    .getConsumableType((packet).getKeyCode());
            if (type.isPresent() && player.getInventory().useConsumable(type.get()).isPresent()) {
                Consumable consumable = ItemFactory.createItem(type.get());
                consumable.pickup(player);
                consumable.consume();

                player.allowAction();
                server.sendToTCP(connection.getID(), new InventoryPacket(connection.getID(),
                        InventorySerializer.serializeInventory(player.getInventory())));
                server.sendToUDP(connection.getID(),
                        new PlayerStatePacket(new PlayerState(connection.getID(), player.getHealth())));
            }
        }
    }
}