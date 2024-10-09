package pewpew.smash.game.states;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.RenderingEngine;
import pewpew.smash.game.PewPewSmash;

public class StateManager {

    private GameState currentState;
    private final PewPewSmash pewPewSmash;

    private final Map<GameStateType, Supplier<GameState>> stateFactories = new HashMap<>();
    private final Map<GameStateType, GameState> cachedStates = new HashMap<>();

    public StateManager(PewPewSmash pewPewSmash) {
        this.pewPewSmash = pewPewSmash;

        stateFactories.put(GameStateType.MENU, () -> new Menu(this.pewPewSmash));
        stateFactories.put(GameStateType.PLAYING, () -> new Playing(this.pewPewSmash));
    }

    public void setState(GameStateType stateType) {
        if (stateType == GameStateType.QUIT) {
            pewPewSmash.conclude();
        } else {
            if (!cachedStates.containsKey(stateType)) {
                cachedStates.put(stateType, stateFactories.get(stateType).get());
            }
            this.currentState = cachedStates.get(stateType);
        }
    }

    public GameState getCurrentState() {
        return this.currentState;
    }

    public void update() {
        if (currentState != null) {
            currentState.update();
        }
    }

    public void render(Canvas canvas) {
        if (currentState != null) {
            currentState.render(canvas);
        }
    }

    public void handleMousePress(MouseEvent e) {
        if (currentState != null) {
            currentState.handleMousePress(e);
        }
    }

    public void handleMouseRelease(MouseEvent e) {
        if (currentState != null) {
            currentState.handleMouseRelease(e);
        }
    }

    public void handleMouseMove(MouseEvent e) {
        if (currentState != null) {
            currentState.handleMouseMove(e);
        }
    }

    public void handleMouseDrag(MouseEvent e) {
        if (currentState != null) {
            currentState.handleMouseDrag(e);
        }
    }

    public void handleKeyPress(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F11) {
            RenderingEngine.getInstance().getScreen().toggleFullscreen();
            return;
        }

        if (currentState != null) {
            currentState.handleKeyPress(e);
        }
    }

    public void handleKeyRelease(KeyEvent e) {
        if (currentState != null) {
            currentState.handleKeyRelease(e);
        }
    }
}