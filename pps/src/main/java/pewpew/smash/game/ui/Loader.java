package pewpew.smash.game.ui;

import java.awt.Color;
import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.Setter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.utils.FontFactory;

@Getter
@Setter
public class Loader extends UiElement {

    private int progress;

    public Loader(int x, int y, int w, int h) {
        super(x, y, w, h);
        this.progress = 0;
    }

    @Override
    protected void loadSprites(BufferedImage spriteSheet) {
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void render(Canvas canvas) {
        drawBackground(canvas);
        drawProgressBar(canvas);
        drawPercentageText(canvas);
    }

    protected void handleMouseInput() {
    }

    protected void handleMouseMove() {
    }

    private void drawBackground(Canvas canvas) {
        canvas.renderRectangle(xPos, yPos, width, height, Color.GRAY);
    }

    private void drawProgressBar(Canvas canvas) {
        int progressWidth = (int) (width * (progress / 100.0));
        canvas.renderRectangle(xPos, yPos, progressWidth, height, Color.GREEN);
    }

    private void drawPercentageText(Canvas canvas) {
        String percentageText = progress + "%";
        FontFactory.IMPACT_MEDIUM.applyFont(canvas);
        int textWidth = FontFactory.IMPACT_MEDIUM.getFontWidth(percentageText, canvas);
        int textX = xPos + (width - textWidth) / 2;
        int textY = yPos + (height + FontFactory.IMPACT_MEDIUM.getFontHeight(canvas)) / 2;
        canvas.renderString(percentageText, textX, textY, Color.BLACK);
    }
}