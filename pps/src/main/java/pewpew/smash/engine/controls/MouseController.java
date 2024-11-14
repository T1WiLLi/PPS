package pewpew.smash.engine.controls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import lombok.Getter;
import pewpew.smash.engine.RenderingEngine;

public abstract class MouseController extends MouseAdapter {
    @Getter
    private static int mouseX = 0, mouseY = 0;
    private static boolean leftMousePressed = false;
    private static boolean rightMousePressed = false;

    public static boolean isLeftMousePressed() {
        return leftMousePressed;
    }

    public static boolean isRightMousePressed() {
        return rightMousePressed;
    }

    public static void consumeLeftClick() {
        leftMousePressed = false;
    }

    public static void consumeRightClick() {
        rightMousePressed = false;
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
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftMousePressed = true;
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            rightMousePressed = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftMousePressed = false;
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            rightMousePressed = false;
        }
    }

    private void updateMousePosition(MouseEvent e) {
        double systemScaleFactor = RenderingEngine.getInstance().getSystemScaleFactor();
        mouseX = (int) (e.getX() * systemScaleFactor);
        mouseY = (int) (e.getY() * systemScaleFactor);
    }
}
