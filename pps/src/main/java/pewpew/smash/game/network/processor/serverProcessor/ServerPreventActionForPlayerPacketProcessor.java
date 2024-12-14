package pewpew.smash.game.network.processor.serverProcessor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Optional;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.audio.AudioClip;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.PreventActionForPlayerPacket;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.processor.ServerProcessor;
import pewpew.smash.game.network.server.ServerAudioManager;
import pewpew.smash.game.network.server.ServerWrapper;

public class ServerPreventActionForPlayerPacketProcessor extends ServerProcessor
        implements PacketProcessor<PreventActionForPlayerPacket> {

    private static final long ACTION_COOLDOWN_MS = 500;

    private final Map<Integer, PlayerAction> playerActionState = new ConcurrentHashMap<>();
    private final Map<Integer, Long> playerActionTimestamps = new ConcurrentHashMap<>();

    public ServerPreventActionForPlayerPacketProcessor(EntityManager entityManager, ServerWrapper server) {
        super(entityManager, server);
    }

    @Override
    public void handle(Connection connection, PreventActionForPlayerPacket packet) {
        Player player = getPlayer(connection);
        if (player != null) {
            boolean isHealing = packet.isHealing();

            PlayerAction newAction = isHealing ? PlayerAction.HEALING : PlayerAction.RELOADING;

            PlayerAction lastAction = playerActionState.getOrDefault(player.getId(), PlayerAction.NONE);
            long lastTimestamp = playerActionTimestamps.getOrDefault(player.getId(), 0L);
            long currentTimestamp = System.currentTimeMillis();

            if (newAction != lastAction || (currentTimestamp - lastTimestamp) >= ACTION_COOLDOWN_MS) {
                playerActionState.put(player.getId(), newAction);
                playerActionTimestamps.put(player.getId(), currentTimestamp);

                if (newAction == PlayerAction.HEALING) {
                    if (packet.getC() == 'm') {
                        ServerAudioManager.getInstance().play(AudioClip.MEDIKIT, player, 1200, Optional.empty());
                    } else {
                        ServerAudioManager.getInstance().play(AudioClip.HEALING, player, 1200, Optional.empty());
                    }
                } else if (newAction == PlayerAction.RELOADING) {
                    ServerAudioManager.getInstance().play(AudioClip.RELOAD, player, 1200, Optional.empty());
                }
            }
            player.preventAction();
        }
    }

    private enum PlayerAction {
        NONE,
        HEALING,
        RELOADING
    }
}
