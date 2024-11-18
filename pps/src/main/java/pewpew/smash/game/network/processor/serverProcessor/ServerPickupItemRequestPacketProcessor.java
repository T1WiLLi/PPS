package pewpew.smash.game.network.processor.serverProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.PickupItemRequestPacket;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.processor.ServerProcessor;
import pewpew.smash.game.network.server.ServerItemUpdater;
import pewpew.smash.game.network.server.ServerWrapper;

public class ServerPickupItemRequestPacketProcessor extends ServerProcessor
        implements PacketProcessor<PickupItemRequestPacket> {
    private final ServerItemUpdater updater;

    public ServerPickupItemRequestPacketProcessor(EntityManager entityManager, ServerWrapper server,
            ServerItemUpdater updater) {
        super(entityManager, server);
        this.updater = updater;
    }

    @Override
    public void handle(Connection connection, PickupItemRequestPacket packet) {
        Player player = getPlayer(connection);
        if (player != null) {
            updater.tryPickupItem(player, server);
        }
    }
}
