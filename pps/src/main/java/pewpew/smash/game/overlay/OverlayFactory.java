package pewpew.smash.game.overlay;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OverlayFactory {

    private static final Map<OverlayType, Overlay> overlays = new ConcurrentHashMap<>();

    public static void preLoadOverlays() {
        for (OverlayType type : OverlayType.values()) {
            overlays.put(type, createOverlay(type));
        }
    }

    public static Overlay getOverlay(OverlayType type) {
        return overlays.get(type);
    }

    private static Overlay createOverlay(OverlayType type) {
        int x = 0, y = 0, width = 800, height = 600;
        return switch (type) {
            case ABOUT -> new AboutOverlay(x, y, width, height);
            case OPTIONS -> new OptionsOverlay(x, y, width, height);
            case CONNECTION -> new ConnectionOverlay(x, y, width, height);
            case ACCOUNT -> new AccountOverlay(x, y, width, height);
            case PLAY -> new PlayOverlay(x, y, width, height);
            default -> throw new IllegalArgumentException("Unknown overlay type: " + type);
        };
    }

}