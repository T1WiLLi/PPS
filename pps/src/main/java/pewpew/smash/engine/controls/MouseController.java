package pewpew.smash.engine.controls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class MouseController extends MouseAdapter {
    private static int mouseX = 0, mouseY = 0;

    public abstract void mousePressed(MouseEvent e);

    public abstract void mouseReleased(MouseEvent e);

    public static int getMouseX() {
        return mouseX;
    }

    public static int getMouseY() {
        return mouseY;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        updateMousePosition(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        updateMousePosition(e);
    }

    private void updateMousePosition(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
}
