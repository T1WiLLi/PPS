package pewpew.smash.game.ui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import lombok.Getter;
import pewpew.smash.engine.Canvas;

public class ButtonImage extends Button {

    @Getter
    private BufferedImage normalSprite;
    private BufferedImage grayscaleSprite;
    private double scaleFactor = 1.0;

    public ButtonImage(int x, int y, int width, int height, BufferedImage spriteSheet, Runnable onClick) {
        super(x, y, width, height, spriteSheet, onClick);
        loadSprites(spriteSheet);
    }

    @Override
    public void update() {
        if (mouseOver) {
            scaleOnMouseOver();
        } else if (mousePressed && mouseOver) {
            onClick.run();
            resetState();
        }
        updateScaledBounds();
    }

    @Override
    public void render(Canvas canvas) {
        if (mouseOver) {
            canvas.renderImage(normalSprite, (int) (xPos - (width * (scaleFactor - 1) / 2)),
                    (int) (yPos - (height * (scaleFactor - 1) / 2)),
                    (int) (width * scaleFactor), (int) (height * scaleFactor));
        } else {
            canvas.renderImage(grayscaleSprite, xPos, yPos, width, height);
        }
    }

    @Override
    protected void loadSprites(BufferedImage spriteSheet) {
        this.normalSprite = spriteSheet;
        this.grayscaleSprite = applyGrayScaleFilter(spriteSheet);
    }

    @Override
    protected void resetState() {
        super.resetState();
        scaleFactor = 1.0;
    }

    private void scaleOnMouseOver() {
        scaleFactor = Math.min(scaleFactor + 0.05, 1.1);
    }

    private BufferedImage applyGrayScaleFilter(BufferedImage image) {
        BufferedImage grayscale = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = grayscale.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        RescaleOp op = new RescaleOp(new float[] { 0.6f, 0.6f, 0.6f, 1f }, new float[4], null);
        op.filter(grayscale, grayscale);
        return grayscale;
    }
}
