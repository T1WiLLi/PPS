package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.hud.HudManager;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.PlayerOutOfWaterPacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class ClientPlayerOutOfWaterPacketProcessor extends ClientProcessor
        implements PacketProcessor<PlayerOutOfWaterPacket> {

    public ClientPlayerOutOfWaterPacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, PlayerOutOfWaterPacket packet) {
        if (User.getInstance().getLocalID().get() == packet.getPlayerID()) {
            HudManager.getInstance().stopWaterWarning();
        }
    }
}
