package pewpew.smash.game.utils;

import pewpew.smash.engine.RenderingEngine;
import pewpew.smash.game.constants.Constants;

import java.awt.Rectangle;

public class ScaleUtils {

    public static Rectangle getScaledBounds(Rectangle bounds) {
        return new Rectangle(
                scaleX(bounds.x),
                scaleY(bounds.y),
                scaleButtonWidth(),
                scaleButtonHeight());
    }

    public static int scaleX(int x) {
        double scaleX = RenderingEngine.getInstance().getScale()[0];
        return (int) (x * scaleX);
    }

    public static int scaleY(int y) {
        double scaleY = RenderingEngine.getInstance().getScale()[1];
        return (int) (y * scaleY);
    }

    public static int scaleWidth(int width) {
        double scaleWidth = RenderingEngine.getInstance().getScale()[0];
        return (int) (width * scaleWidth);
    }

    public static int scaleHeight(int height) {
        double scaleHeight = RenderingEngine.getInstance().getScale()[1];
        return (int) (height * scaleHeight);
    }

    public static int scaleButtonWidth() {
        return (int) (RenderingEngine.getInstance().getScale()[0] * Constants.BUTTON_WIDTH);
    }

    public static int scaleButtonHeight() {
        return (int) (RenderingEngine.getInstance().getScale()[1] * Constants.BUTTON_HEIGHT);
    }

    public static double getScaleX() {
        return RenderingEngine.getInstance().getScale()[0];
    }

    public static double getScaleY() {
        return RenderingEngine.getInstance().getScale()[1];
    }
}
