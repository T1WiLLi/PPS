package pewpew.smash.game.network.client;

import java.util.List;

import lombok.Getter;
import pewpew.smash.game.gamemode.GameModeManager;
import pewpew.smash.game.states.GameStateType;
import pewpew.smash.game.states.StateManager;

public class ClientPostGameManager {
    private static ClientPostGameManager instance;

    @Getter
    private String winnerName;

    @Getter
    private List<String> allPlayers;

    public static synchronized ClientPostGameManager getInstance() {
        if (instance == null) {
            instance = new ClientPostGameManager();
        }
        return instance;
    }

    public void startPostGame(String winnerName, List<String> allPlayers) {
        this.winnerName = winnerName;
        this.allPlayers = allPlayers;
        StateManager.getInstance().setState(GameStateType.POST_GAME);
        GameModeManager.getInstance().getCurrentGameMode().reset();
    }
}
