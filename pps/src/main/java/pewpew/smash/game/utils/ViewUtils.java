package pewpew.smash.game.utils;

import pewpew.smash.game.Camera;

public class ViewUtils {
    private static final double FOV_BUFFER = 0.5;
    private static final int SCREEN_DEFAULT_WIDTH = 800;
    private static final int SCREEN_DEFAULT_HEIGHT = 600;

    public static boolean isInView(int x, int y) {
        ViewBounds bounds = calculateBounds();

        double scaleX = ScaleUtils.getScaleX();
        double scaleY = ScaleUtils.getScaleY();

        double scaledX = x / scaleX;
        double scaledY = y / scaleY;

        return scaledX >= bounds.minX && scaledX <= bounds.maxX &&
                scaledY >= bounds.minY && scaledY <= bounds.maxY;
    }

    public static ViewBounds calculateBounds() {
        double scaleX = ScaleUtils.getScaleX();
        double scaleY = ScaleUtils.getScaleY();
        double zoom = Camera.getZoom();

        double bufferX = (SCREEN_DEFAULT_WIDTH / zoom) * FOV_BUFFER;
        double bufferY = (SCREEN_DEFAULT_HEIGHT / zoom) * FOV_BUFFER;

        return new ViewBounds(
                Camera.getInstance().getX() / scaleX - bufferX,
                Camera.getInstance().getX() / scaleX + (SCREEN_DEFAULT_WIDTH / zoom) + bufferX,
                Camera.getInstance().getY() / scaleY - bufferY,
                Camera.getInstance().getY() / scaleY + (SCREEN_DEFAULT_HEIGHT / zoom) + bufferY);
    }

    public static record ViewBounds(double minX, double maxX, double minY, double maxY) {
    }
}
