package pewpew.smash.game.hud;

import java.awt.Color;

import lombok.Setter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.utils.FontFactory;

public class BarDisplayer extends HudElement {

    @Setter
    private int value;

    public BarDisplayer(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(Canvas canvas) {
        int filled = (int) ((double) value / 100 * width);

        canvas.renderRectangle(x, y, width, height, Color.GRAY);
        canvas.renderRectangle(x, y, filled, height, Color.RED);

        FontFactory.IMPACT_SMALL.applyFont(canvas);
        canvas.renderString(value + "/100", x + 10, y + 20, Color.WHITE);
        FontFactory.resetFont(canvas);
    }
}
