package pewpew.smash.game.ui;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Rectangle;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.controls.MouseController;
import pewpew.smash.game.utils.ScaleUtils;

@ToString
public class Slider extends UiElement {

    @Getter
    private float value;

    @Setter
    @Getter
    private boolean mouseOver, mousePressed;

    private static final int HANDLE_WIDTH = 20;
    private static final int TRACK_HEIGHT = 15;

    @Getter
    private int handleX;
    private Rectangle handleBounds;

    public Slider(int x, int y, int width, int height, float initialValue) {
        super(x, y, width, height);
        this.value = Math.max(0.0f, Math.min(1.0f, initialValue));
        this.handleBounds = new Rectangle();
        updateHandlePosition();
    }

    @Override
    protected void loadSprites(BufferedImage spriteSheet) {
    }

    private void updateHandlePosition() {
        handleX = xPos + (int) ((width - HANDLE_WIDTH) * value);
        updateHandleBounds();
    }

    private void updateHandleBounds() {
        handleBounds.setBounds(
                ScaleUtils.scaleX(handleX),
                ScaleUtils.scaleY(yPos),
                ScaleUtils.scaleWidth(HANDLE_WIDTH),
                ScaleUtils.scaleHeight(height));
    }

    @Override
    public void update() {
        updateScaledBounds();
        updateHandleBounds();

        int scaledMouseX = MouseController.getMouseX();
        int scaledMouseY = MouseController.getMouseY();

        if (mousePressed) {
            int scaledSliderStart = ScaleUtils.scaleX(xPos);
            int scaledSliderEnd = ScaleUtils.scaleX(xPos + width - HANDLE_WIDTH);

            int constrainedMouseX = Math.max(scaledSliderStart, Math.min(scaledMouseX, scaledSliderEnd));

            float newValue = (float) (constrainedMouseX - scaledSliderStart) /
                    (scaledSliderEnd - scaledSliderStart);
            setValue(newValue);
        }

        mouseOver = bounds.contains(scaledMouseX, scaledMouseY);
    }

    @Override
    public void render(Canvas canvas) {
        int trackY = yPos + (height - TRACK_HEIGHT) / 2;
        canvas.renderRectangleBorder(xPos, trackY, width, TRACK_HEIGHT, 3, Color.WHITE);

        canvas.renderRectangle(handleX, yPos, HANDLE_WIDTH, height, Color.WHITE);
        canvas.renderRectangleBorder(handleX, yPos, HANDLE_WIDTH, height, 1, Color.BLACK);
    }

    public void setValue(float newValue) {
        this.value = Math.max(0.0f, Math.min(1.0f, newValue));
        updateHandlePosition();
    }

    public int getHandleXStart() {
        return ScaleUtils.scaleX(handleX);
    }

    public int getHandleXEnd() {
        return ScaleUtils.scaleX(handleX + HANDLE_WIDTH);
    }
}