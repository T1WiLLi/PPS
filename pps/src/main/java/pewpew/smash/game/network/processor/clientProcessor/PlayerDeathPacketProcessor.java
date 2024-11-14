package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.SpectatorManager;
import pewpew.smash.game.hud.HudManager;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.PlayerDeathPacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class PlayerDeathPacketProcessor extends ClientProcessor implements PacketProcessor {

    public PlayerDeathPacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, Object packet) {
        if (packet instanceof PlayerDeathPacket playerDeathPacket) {
            int deadPlayerId = playerDeathPacket.getDeadPlayerID();
            int killerPlayerId = playerDeathPacket.getKillerPlayerID();

            if (deadPlayerId == User.getInstance().getLocalID().get()) {
                User.getInstance().setDead(true);
                SpectatorManager.getInstance().startSpectating(killerPlayerId);
            } else if (SpectatorManager.getInstance().isSpectating() &&
                    deadPlayerId == SpectatorManager.getInstance().getSpectatingPlayerId()) {
                SpectatorManager.getInstance().startSpectating(killerPlayerId);
            }
            getEntityManager().removePlayerEntity(deadPlayerId);
            HudManager.getInstance().setAmountOfPlayerAlive(getEntityManager().getPlayerEntities().size());
        }
    }
}
