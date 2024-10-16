package pewpew.smash.game.input;

import java.awt.event.MouseEvent;

import pewpew.smash.engine.RenderingEngine;
import pewpew.smash.engine.controls.MouseController;
import pewpew.smash.game.states.StateManager;

public class MouseHandler extends MouseController {

    private StateManager stateManager;

    public MouseHandler(StateManager stateManager) {
        this.stateManager = stateManager;
        RenderingEngine.getInstance().addMouseListener(this);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        this.stateManager.handleMousePress(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        this.stateManager.handleMouseRelease(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        this.stateManager.handleMouseMove(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        this.stateManager.handleMouseDrag(e);
    }
}
