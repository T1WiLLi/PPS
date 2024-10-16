package pewpew.smash.engine.controls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class MouseController extends MouseAdapter {
    private static int mouseX = 0, mouseY = 0;
    private static boolean mousePressed = false;

    public static int getMouseX() {
        return mouseX;
    }

    public static int getMouseY() {
        return mouseY;
    }

    public static boolean isPressed() {
        return mousePressed;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        updateMousePosition(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        updateMousePosition(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mousePressed = false;
    }

    private void updateMousePosition(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
}
