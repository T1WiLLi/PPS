package pewpew.smash.game.input;

import pewpew.smash.engine.RenderingEngine;
import pewpew.smash.game.states.StateManager;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    private static KeyEvent lastEvent = null;

    private StateManager stateManager;

    public KeyHandler(StateManager stateManager) {
        this.stateManager = stateManager;
        RenderingEngine.getInstance().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        lastEvent = e;
        this.stateManager.handleKeyPress(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        lastEvent = null;
        this.stateManager.handleKeyRelease(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Nothings
    }

    public static KeyEvent getLastEvent() {
        KeyEvent event = lastEvent;
        lastEvent = null;
        return event;
    }
}
