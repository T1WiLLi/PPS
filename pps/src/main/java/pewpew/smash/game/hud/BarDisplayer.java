package pewpew.smash.game.hud;

import java.awt.Color;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.utils.FontFactory;

public class BarDisplayer {

    private int x, y, width, height;

    public BarDisplayer(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(Canvas canvas, int value) {
        int filled = (int) ((double) value / 100 * width);

        canvas.renderRectangle(x, y, width, height, Color.GRAY);
        canvas.renderRectangle(x, y, filled, height, Color.RED);

        FontFactory.IMPACT_SMALL.applyFont(canvas);
        canvas.renderString(value + "/100", x + 10, y + 20, Color.WHITE);
        FontFactory.resetFont(canvas);
    }
}
