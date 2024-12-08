package pewpew.smash.game;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;

import lombok.Getter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.input.GamePad;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.utils.FontFactory;

public class SpectatorManager {
    private volatile static SpectatorManager instance;

    @Getter
    private int spectatingPlayerId = Integer.MIN_VALUE;

    @Getter
    private boolean isSpectating = false;

    private EntityManager entityManager;
    private long lastSwitchTime = 0;
    private static final long SWITCH_BUFFER = 200;

    public static SpectatorManager getInstance() {
        if (instance == null) {
            synchronized (SpectatorManager.class) {
                if (instance == null) {
                    instance = new SpectatorManager();
                }
            }
        }
        return instance;
    }

    public void initialize(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public boolean startSpectating(int playerId) {
        Player targetPlayer = entityManager.getPlayerEntity(playerId);
        if (targetPlayer != null) {
            this.spectatingPlayerId = playerId;
            this.isSpectating = true;
            Camera.getInstance().centerOn(targetPlayer);
            return true;
        }
        return false;
    }

    public void stopSpectating() {
        this.spectatingPlayerId = -1;
        this.isSpectating = false;
    }

    public Player getSpectatingTarget() {
        if (!isSpectating || spectatingPlayerId == -1) {
            return null;
        }
        return entityManager.getPlayerEntity(spectatingPlayerId);
    }

    public void update() {
        if (isSpectating) {
            Player target = getSpectatingTarget();
            if (target == null) {
                if (!trySwitchToAnotherPlayer()) {
                    stopSpectating();
                    return;
                }
                target = getSpectatingTarget();
                if (target == null) {
                    stopSpectating();
                    return;
                }
            }

            Camera.getInstance().centerOn(target);

            handleSpectatorControls();
        }
    }

    public void render(Canvas canvas) {
        FontFactory.IMPACT_X_LARGE.applyFont(canvas);
        String text = "Spectating: " + getSpectatingTarget().getUsername();
        canvas.renderString(text, 800 / 2 - FontFactory.IMPACT_X_LARGE.getFontWidth(text, canvas) / 2,
                200, Color.WHITE);
        FontFactory.resetFont(canvas);
    }

    public void reset() {
        stopSpectating();
        this.entityManager = null;
    }

    private void handleSpectatorControls() {
        long currentTime = System.currentTimeMillis();

        if (User.getInstance().isDead() && (currentTime - lastSwitchTime >= SWITCH_BUFFER)) {
            if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_RIGHT)) {
                switchToNextPlayer();
                lastSwitchTime = currentTime;
            } else if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_LEFT)) {
                switchToPreviousPlayer();
                lastSwitchTime = currentTime;
            }
        }
    }

    private boolean trySwitchToAnotherPlayer() {
        List<Player> players = new ArrayList<>(entityManager.getPlayerEntities());
        if (players.isEmpty()) {
            return false;
        }

        int currentIndex = getPlayerIndex(players, spectatingPlayerId);
        if (currentIndex == -1) {
            return startSpectating(players.get(0).getId());
        }

        switchToNextPlayer();
        return getSpectatingTarget() != null;
    }

    private void switchToNextPlayer() {
        List<Player> players = new ArrayList<>(entityManager.getPlayerEntities());
        if (players.isEmpty())
            return;

        int currentIndex = getPlayerIndex(players, spectatingPlayerId);
        int nextIndex = (currentIndex + 1) % players.size();

        startSpectating(players.get(nextIndex).getId());
    }

    private void switchToPreviousPlayer() {
        List<Player> players = new ArrayList<>(entityManager.getPlayerEntities());
        if (players.isEmpty())
            return;

        int currentIndex = getPlayerIndex(players, spectatingPlayerId);
        int prevIndex = (currentIndex - 1 + players.size()) % players.size();

        startSpectating(players.get(prevIndex).getId());
    }

    private int getPlayerIndex(List<Player> players, int playerId) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getId() == playerId) {
                return i;
            }
        }
        return -1;
    }
}
