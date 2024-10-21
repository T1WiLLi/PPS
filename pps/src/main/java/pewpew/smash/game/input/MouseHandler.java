package pewpew.smash.game.input;

import java.awt.event.MouseEvent;

import pewpew.smash.engine.RenderingEngine;
import pewpew.smash.engine.controls.MouseController;

public class MouseHandler extends MouseController {

    public MouseHandler() {
        RenderingEngine.getInstance().addMouseListener(this);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
    }
}
