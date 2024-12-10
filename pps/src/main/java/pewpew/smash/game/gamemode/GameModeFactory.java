package pewpew.smash.game.gamemode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class GameModeFactory {
    private static final Map<GameModeType, Supplier<GameMode>> gameModeSuppliers = new ConcurrentHashMap<>();
    private static final Map<GameModeType, GameMode> cachedGameModes = new ConcurrentHashMap<>();

    // Add other gamemode as we progress :)
    static {
        gameModeSuppliers.put(GameModeType.SANDBOX, Sandbox::new);
        gameModeSuppliers.put(GameModeType.BATTLE_ROYALE, BattleRoyale::new);
    }

    public static void preLoadGameModes() {
        for (GameModeType gameModeType : GameModeType.values()) {
            cachedGameModes.computeIfAbsent(gameModeType,
                    type -> gameModeSuppliers.getOrDefault(type, () -> null).get());
        }
    }

    public static GameMode getGameMode(GameModeType type) {
        return cachedGameModes.get(type);
    }
}
