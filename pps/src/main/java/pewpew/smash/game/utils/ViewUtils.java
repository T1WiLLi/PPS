package pewpew.smash.game.utils;

import pewpew.smash.game.Camera;

public class ViewUtils {
    private static final double FOV_BUFFER = 0.5;
    private static final int SCREEN_DEFAULT_WIDTH = 800;
    private static final int SCREEN_DEFAULT_HEIGHT = 600;

    private static ViewBounds cachedBounds;
    private static double lastScaleX = -1;
    private static double lastScaleY = -1;
    private static double lastZoom = -1;
    private static int lastCameraX = -1;
    private static int lastCameraY = -1;

    public static boolean isInView(int x, int y) {
        ViewBounds bounds = getCurrentBounds();

        double scaleX = ScaleUtils.getScaleX();
        double scaleY = ScaleUtils.getScaleY();

        double scaledX = x / scaleX;
        double scaledY = y / scaleY;

        return scaledX >= bounds.minX && scaledX <= bounds.maxX &&
                scaledY >= bounds.minY && scaledY <= bounds.maxY;
    }

    public static ViewBounds getCurrentBounds() {
        double scaleX = ScaleUtils.getScaleX();
        double scaleY = ScaleUtils.getScaleY();
        double zoom = Camera.getZoom();
        int cameraX = (int) Camera.getInstance().getX();
        int cameraY = (int) Camera.getInstance().getY();

        if (cachedBounds == null || scaleX != lastScaleX || scaleY != lastScaleY || zoom != lastZoom
                || cameraX != lastCameraX || cameraY != lastCameraY) {
            double bufferX = (SCREEN_DEFAULT_WIDTH / zoom) * FOV_BUFFER;
            double bufferY = (SCREEN_DEFAULT_HEIGHT / zoom) * FOV_BUFFER;

            cachedBounds = new ViewBounds(
                    cameraX / scaleX - bufferX,
                    cameraX / scaleX + (SCREEN_DEFAULT_WIDTH / zoom) + bufferX,
                    cameraY / scaleY - bufferY,
                    cameraY / scaleY + (SCREEN_DEFAULT_HEIGHT / zoom) + bufferY);

            lastScaleX = scaleX;
            lastScaleY = scaleY;
            lastZoom = zoom;
            lastCameraX = cameraX;
            lastCameraY = cameraY;
        }

        return cachedBounds;
    }

    public static record ViewBounds(double minX, double maxX, double minY, double maxY) {
    }
}
