package pewpew.smash.game.network.server;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.packets.PostGamePacket;

public class ServerPostGameManager {
    private final ServerWrapper server;
    private final AtomicBoolean postGameTriggered;

    public ServerPostGameManager(ServerWrapper server) {
        this.server = server;
        this.postGameTriggered = new AtomicBoolean(false);
    }

    public void triggerPostGame(Player winner, List<Player> allPlayers) {
        if (postGameTriggered.compareAndSet(false, true)) {
            List<String> playerNames = allPlayers.stream()
                    .map(Player::getUsername)
                    .collect(Collectors.toList());

            PostGamePacket packet = new PostGamePacket(winner.getUsername(), playerNames);
            ServerAudioManager.getInstance().stop();
            server.sendToAllTCP(packet);
        }
    }

    public void reset() {
        postGameTriggered.set(false);
    }
}