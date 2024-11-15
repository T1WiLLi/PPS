package pewpew.smash.game.network.processor.serverProcessor;

import java.awt.event.KeyEvent;
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
import pewpew.smash.game.objects.ConsumableType;

public class UseConsumableRequestPacketProcessor extends ServerProcessor implements PacketProcessor {

    public UseConsumableRequestPacketProcessor(EntityManager entityManager, ServerWrapper server) {
        super(entityManager, server);
    }

    @Override
    public void handle(Connection connection, Object packet) {
        Player player = getPlayer(connection);
        if (player != null && player.getHealth() < 100) {
            Optional<ConsumableType> type = getConsumableType(((UseConsumableRequestPacket) packet).getKeyCode());
            if (type.isPresent() && player.getInventory().useConsumable(type.get()).isPresent()) {
                player.setHealth(Math.clamp(player.getHealth() + type.get().getHealAmount(), 0, 100));
                server.sendToTCP(connection.getID(), new InventoryPacket(connection.getID(),
                        InventorySerializer.serializeInventory(player.getInventory())));
                server.sendToUDP(connection.getID(),
                        new PlayerStatePacket(new PlayerState(connection.getID(), player.getHealth())));
            }
        }
    }

    private Optional<ConsumableType> getConsumableType(int code) {
        return switch (code) {
            case KeyEvent.VK_3 -> Optional.of(ConsumableType.MEDIKIT);
            case KeyEvent.VK_4 -> Optional.of(ConsumableType.BANDAGE);
            case KeyEvent.VK_5 -> Optional.of(ConsumableType.PILL);
            default -> Optional.empty();
        };
    }
}