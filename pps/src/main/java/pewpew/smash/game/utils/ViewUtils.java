package pewpew.smash.game.utils;

import pewpew.smash.game.Camera;

public class ViewUtils {
    private static final double FOV_BUFFER = 0.5;
    private static final int SCREEN_DEFAULT_WIDTH = 800;
    private static final int SCREEN_DEFAULT_HEIGHT = 600;

    private static ViewBounds cachedBounds;
    private static double lastCameraX;
    private static double lastCameraY;
    private static double lastZoom;
    private static double lastScaleX;
    private static double lastScaleY;

    public static boolean isInView(int x, int y) {
        ViewBounds bounds = getCachedBounds();

        double scaleX = ScaleUtils.getScaleX();
        double scaleY = ScaleUtils.getScaleY();

        double scaledX = x / scaleX;
        double scaledY = y / scaleY;

        return scaledX >= bounds.minX && scaledX <= bounds.maxX &&
                scaledY >= bounds.minY && scaledY <= bounds.maxY;
    }

    private static ViewBounds getCachedBounds() {
        double currentCameraX = Camera.getInstance().getX();
        double currentCameraY = Camera.getInstance().getY();
        double currentZoom = Camera.getZoom();
        double currentScaleX = ScaleUtils.getScaleX();
        double currentScaleY = ScaleUtils.getScaleY();

        if (cachedBounds == null ||
                currentCameraX != lastCameraX ||
                currentCameraY != lastCameraY ||
                currentZoom != lastZoom ||
                currentScaleX != lastScaleX ||
                currentScaleY != lastScaleY) {

            cachedBounds = calculateBounds(currentCameraX, currentCameraY, currentZoom, currentScaleX, currentScaleY);

            lastCameraX = currentCameraX;
            lastCameraY = currentCameraY;
            lastZoom = currentZoom;
            lastScaleX = currentScaleX;
            lastScaleY = currentScaleY;
        }

        return cachedBounds;
    }

    private static ViewBounds calculateBounds(double cameraX, double cameraY, double zoom, double scaleX,
            double scaleY) {
        double bufferX = (SCREEN_DEFAULT_WIDTH / zoom) * FOV_BUFFER;
        double bufferY = (SCREEN_DEFAULT_HEIGHT / zoom) * FOV_BUFFER;

        return new ViewBounds(
                cameraX / scaleX - bufferX,
                cameraX / scaleX + (SCREEN_DEFAULT_WIDTH / zoom) + bufferX,
                cameraY / scaleY - bufferY,
                cameraY / scaleY + (SCREEN_DEFAULT_HEIGHT / zoom) + bufferY);
    }

    public static record ViewBounds(double minX, double maxX, double minY, double maxY) {
    }
}
