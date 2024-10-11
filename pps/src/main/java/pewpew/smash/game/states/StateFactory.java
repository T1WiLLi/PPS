package pewpew.smash.game.states;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class StateFactory {
    private static final Map<GameStateType, Supplier<GameState>> stateSuppliers = new ConcurrentHashMap<>();
    private static final Map<GameStateType, GameState> cachedStates = new ConcurrentHashMap<>();

    static {
        stateSuppliers.put(GameStateType.MENU, Menu::new);
        stateSuppliers.put(GameStateType.PLAYING, Playing::new);
    }

    public static void preLoadStates() {
        for (GameStateType stateType : GameStateType.values()) {
            cachedStates.computeIfAbsent(stateType, type -> stateSuppliers.getOrDefault(type, () -> null).get());
        }
    }

    public static GameState getState(GameStateType stateType) {
        return cachedStates.get(stateType);
    }
}
