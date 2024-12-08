package pewpew.smash.game.network.client;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import pewpew.smash.game.gamemode.GameModeManager;
import pewpew.smash.game.gamemode.GameModeType;
import pewpew.smash.game.states.GameStateType;
import pewpew.smash.game.states.StateManager;

public class ClientLobbyManager {
    private static ClientLobbyManager instance;
    @Getter
    private List<String> players = new ArrayList<>();
    @Getter
    private int countdown = 0;
    @Getter
    private boolean inLobby = false;

    public static synchronized ClientLobbyManager getInstance() {
        if (instance == null) {
            instance = new ClientLobbyManager();
        }
        return instance;
    }

    public void enterLobby() {
        inLobby = true;
        StateManager.getInstance().setState(GameStateType.LOBBY);
    }

    public void updateLobbyState(List<String> playerNames, int countdownRemaining) {
        this.players = playerNames;
        this.countdown = countdownRemaining;
    }

    public void onStartGame() {
        inLobby = false;
        GameModeManager.getInstance().setGameMode(GameModeType.SANDBOX);
        GameModeManager.getInstance().getCurrentGameMode().build(new String[] { "127.0.0.1", "25565", "false" });
        StateManager.getInstance().setState(GameStateType.PLAYING);
    }
}