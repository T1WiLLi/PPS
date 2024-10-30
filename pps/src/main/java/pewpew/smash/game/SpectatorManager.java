package pewpew.smash.game;

import java.awt.event.KeyEvent;
import java.util.Collection;

import lombok.Getter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.input.GamePad;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.utils.FontFactory;

// TODO: FIX SWITCHING SPECTATING, AND ALSO IMPROVE HUD WHEN SPECTATING (GIVE HINTS ABOUT THE GAME, KEYS TO CHANGE SPECTACTOR, ECT)
public class SpectatorManager {
    private volatile static SpectatorManager instance;

    @Getter
    private int spectatingPlayerId = Integer.MIN_VALUE;

    @Getter
    private boolean isSpectating = false;

    private EntityManager entityManager;

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
            if (target != null) {
                Camera.getInstance().centerOn(target);
            } else {
                stopSpectating();
            }

            handleSpectatorControls();
        }
    }

    public void render(Canvas canvas) {
        FontFactory.IMPACT_X_LARGE.applyFont(canvas);
        String text = "Spectating: " + getSpectatingTarget().getUsername();
        canvas.renderString(text, 800 / 2 - FontFactory.IMPACT_X_LARGE.getFontWidth(text, canvas) / 2,
                200);
        FontFactory.resetFont(canvas);
    }

    public void reset() {
        stopSpectating();
        this.entityManager = null;
    }

    private void handleSpectatorControls() {
        if (User.getInstance().isDead()) {
            if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_RIGHT)) {
                switchToNextPlayer();
            } else if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_LEFT)) {
                switchToPreviousPlayer();
            }
        }
    }

    private void switchToNextPlayer() {
        Collection<Player> players = entityManager.getPlayerEntities();
        if (players.isEmpty())
            return;

        int currentId = getSpectatingPlayerId();
        Player nextPlayer = null;
        boolean foundCurrent = false;

        for (Player player : players) {
            if (foundCurrent) {
                nextPlayer = player;
                break;
            }
            if (player.getId() == currentId) {
                foundCurrent = true;
            }
        }

        if (nextPlayer == null && !players.isEmpty()) {
            nextPlayer = players.iterator().next();
        }

        if (nextPlayer != null) {
            startSpectating(nextPlayer.getId());
        }
    }

    private void switchToPreviousPlayer() {
        Collection<Player> players = entityManager.getPlayerEntities();
        if (players.isEmpty())
            return;

        Player previousPlayer = null;
        Player lastPlayer = null;

        for (Player player : players) {
            if (player.getId() == getSpectatingPlayerId()) {
                if (previousPlayer != null) {
                    startSpectating(previousPlayer.getId());
                    return;
                }
            }
            previousPlayer = player;
            lastPlayer = player;
        }

        if (lastPlayer != null) {
            startSpectating(lastPlayer.getId());
        }
    }
}
