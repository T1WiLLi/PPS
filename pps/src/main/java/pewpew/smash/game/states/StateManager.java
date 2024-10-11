package pewpew.smash.game.states;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.RenderingEngine;
import pewpew.smash.game.GameManager;

public class StateManager {

    private State currentState;

    public void setState(GameStateType stateType) {
        if (stateType == GameStateType.QUIT) {
            GameManager.getInstance().conclude();
        } else {
            this.currentState = StateFactory.getState(stateType);
        }
    }

    public State getCurrentState() {
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