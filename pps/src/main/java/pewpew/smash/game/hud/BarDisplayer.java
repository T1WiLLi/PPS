package pewpew.smash.game.hud;

import java.awt.Color;

import lombok.Setter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.utils.FontFactory;

public class BarDisplayer extends HudElement {

    @Setter
    private int value;
    @Setter
    private int maxValue;

    private Color accentColor;

    public BarDisplayer(int x, int y, int width, int height, Color accentColor) {
        super(x, y, width, height);
        this.accentColor = accentColor;
    }

    @Override
    public void render(Canvas canvas) {
        int filled = (int) ((double) value / maxValue * width);

        canvas.renderRectangle(x, y, width, height, Color.GRAY);
        canvas.renderRectangle(x, y, filled, height, this.accentColor);

        FontFactory.IMPACT_SMALL.applyFont(canvas);
        canvas.renderString(value + "/" + maxValue, x + 10, y + 20, Color.WHITE);
        FontFactory.resetFont(canvas);
    }
}
