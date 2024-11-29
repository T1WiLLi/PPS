package pewpew.smash.game.input;

import java.awt.event.MouseEvent;

import pewpew.smash.engine.RenderingEngine;
import pewpew.smash.engine.controls.MouseController;
import pewpew.smash.game.Camera;

public class MouseHandler extends MouseController {

    private static final float ROTATION_SMOOTHING_FACTOR = 0.05f;
    private static double currentRotation = 0;

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

    public static double getSmoothedAngle(double entityX, double entityY) {
        double targetRotation = calculateTargetAngle(entityX, entityY);
        double delta = ((targetRotation - currentRotation + 540) % 360) - 180;
        currentRotation = (currentRotation + delta * ROTATION_SMOOTHING_FACTOR) % 360;
        if (currentRotation < 0) {
            currentRotation += 360;
        }
        return currentRotation;
    }

    private static double calculateTargetAngle(double entityX, double entityY) {
        double scaleX = RenderingEngine.getInstance().getScale()[0];
        double scaleY = RenderingEngine.getInstance().getScale()[1];

        double adjustedMouseX = MouseController.getMouseX() / scaleX;
        double adjustedMouseY = MouseController.getMouseY() / scaleY;

        double adjustedEntityX = (entityX - Camera.getOffsetX()) * Camera.getZoom();
        double adjustedEntityY = (entityY - Camera.getOffsetY()) * Camera.getZoom();

        return (Math.toDegrees(Math.atan2(adjustedMouseY - adjustedEntityY, adjustedMouseX - adjustedEntityX)) + 360)
                % 360;
    }
}
