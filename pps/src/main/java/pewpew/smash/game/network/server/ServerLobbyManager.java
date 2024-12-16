package pewpew.smash.game.network.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Setter;
import pewpew.smash.game.gamemode.GameModeType;
import pewpew.smash.game.network.packets.LobbyStatePacket;
import pewpew.smash.game.network.packets.StartGamePacket;
import pewpew.smash.game.utils.HelpMethods;

public class ServerLobbyManager {

    private final ServerWrapper server;
    private final String gamemode;

    private Map<Integer, String> lobbyPlayers = new ConcurrentHashMap<>();
    private boolean lobbyActive = true;
    private int guestCount = 1;
    @Setter
    private int minPlayers = 1; // threshold to start countdown
    private long countdownStartTime = -1;
    @Setter
    private int countdownDuration = 5;

    public ServerLobbyManager(ServerWrapper server, String gamemode) {
        this.server = server;
        this.gamemode = gamemode;
        this.minPlayers = HelpMethods.getGameModeTypeFromString(gamemode).equals(GameModeType.SANDBOX) ? 1 : 2;
        this.countdownDuration = HelpMethods.getGameModeTypeFromString(gamemode).equals(GameModeType.SANDBOX) ? 1 : 1; // Change
                                                                                                                       // back
                                                                                                                       // to
                                                                                                                       // 5
                                                                                                                       // &
                                                                                                                       // 30
                                                                                                                       // sec
    }

    public boolean isLobbyActive() {
        return lobbyActive;
    }

    public void addPlayer(int connectionId, String username) {
        if (username == null || username.trim().isEmpty()) {
            username = "Guest-" + guestCount++;
        }
        lobbyPlayers.put(connectionId, username);
        broadcastLobbyState();
    }

    public void removePlayer(int connectionId) {
        lobbyPlayers.remove(connectionId);
        broadcastLobbyState();
    }

    public void updateLobby() {
        if (!lobbyActive)
            return;

        if (checkThresholdReached()) {
            int remain = getCountdownRemaining();
            if (remain <= 0) {
                startGame();
            } else {
                broadcastLobbyState();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void broadcastLobbyState() {
        if (!lobbyActive)
            return;
        LobbyStatePacket packet = new LobbyStatePacket(new ArrayList<>(lobbyPlayers.values()),
                (checkThresholdReached() ? getCountdownRemaining() : 0));
        server.sendToAllTCP(packet);
    }

    private boolean checkThresholdReached() {
        return lobbyPlayers.size() >= minPlayers;
    }

    private int getCountdownRemaining() {
        if (countdownStartTime == -1 && checkThresholdReached()) {
            countdownStartTime = System.currentTimeMillis();
        }

        if (countdownStartTime == -1)
            return 0;

        long elapsed = (System.currentTimeMillis() - countdownStartTime) / 1000;
        int remaining = countdownDuration - (int) elapsed;
        return Math.max(remaining, 0);
    }

    private void startGame() {
        lobbyActive = false;
        server.sendToAllTCP(new StartGamePacket(this.gamemode));
    }

    public Map<Integer, String> getLobbyPlayers() {
        return Collections.unmodifiableMap(lobbyPlayers);
    }

    public void clearLobby() {
        lobbyPlayers.clear();
    }
}
