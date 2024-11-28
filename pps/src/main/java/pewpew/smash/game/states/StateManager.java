package pewpew.smash.game.states;

import java.awt.event.KeyEvent;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.RenderingEngine;
import pewpew.smash.game.GameManager;

public class StateManager {

    private static StateManager instance;

    private State currentState;

    public synchronized static StateManager getInstance() {
        if (instance == null) {
            synchronized (StateManager.class) {
                if (instance == null) {
                    instance = new StateManager();
                }
            }
        }
        return instance;
    }

    public void setState(GameStateType stateType) {
        if (stateType == GameStateType.QUIT) {
            GameManager.getInstance().conclude();
        } else {
            this.currentState = StateFactory.getState(stateType);
        }
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

    public void handleKeyPress(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F11) {
            RenderingEngine.getInstance().getDisplayManager()
                    .setFullscreen(!RenderingEngine.getInstance().getDisplayManager().isFullscreen());
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