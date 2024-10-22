package pewpew.smash.engine.controls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import lombok.Getter;

public abstract class MouseController extends MouseAdapter {
    @Getter
    private static int mouseX = 0, mouseY = 0;
    private static boolean mousePressed = false;

    public static boolean isMousePressed() {
        return mousePressed;
    }

    public static void consumeEvent() {
        mousePressed = false;
    }

    public static double getMouseAngle() {
        return Math.toDegrees(Math.atan2(mouseY, mouseX));
    }

    public static double getMouseAngleFrom(double x, double y) {
        double deltaX = mouseX - x;
        double deltaY = mouseY - y;
        return Math.toDegrees(Math.atan2(deltaY, deltaX));
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
