package pewpew.smash.game.states;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class StateFactory {
    private static final Map<GameStateType, Supplier<State>> stateSuppliers = new ConcurrentHashMap<>();
    private static final Map<GameStateType, State> cachedStates = new ConcurrentHashMap<>();

    static {
        stateSuppliers.put(GameStateType.MENU, Menu::new);
        stateSuppliers.put(GameStateType.PLAYING, Playing::new);
    }

    public static void preLoadStates() {
        for (GameStateType stateType : GameStateType.values()) {
            cachedStates.computeIfAbsent(stateType, type -> stateSuppliers.getOrDefault(type, () -> null).get());
        }
    }

    public static State getState(GameStateType stateType) {
        return cachedStates.get(stateType);
    }
}
