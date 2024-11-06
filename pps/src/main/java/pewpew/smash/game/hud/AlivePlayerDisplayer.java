package pewpew.smash.game.hud;

import lombok.Setter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.utils.FontFactory;

import java.awt.Color;

public class AlivePlayerDisplayer extends HudElement {

    @Setter
    private int amountOfPlayerAlive;

    public AlivePlayerDisplayer(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    protected void render(Canvas canvas) {
        FontFactory.IMPACT_LARGE.applyFont(canvas);
        canvas.renderRectangle(x, y, width, height, new Color(200, 200, 200, 125));
        canvas.renderRectangleBorder(x, y, width, height, 2, Color.WHITE);
        String title = "Alive";
        String amount = String.valueOf(amountOfPlayerAlive);
        int titleWidth = FontFactory.IMPACT_LARGE.getFontWidth(title, canvas);
        int amountWidth = FontFactory.IMPACT_LARGE.getFontWidth(amount, canvas);
        canvas.renderString(title, x + width / 2 - titleWidth / 2, y + 40, Color.WHITE);
        canvas.renderString(amount, x + width / 2 - amountWidth / 2, y + 90, Color.WHITE);
    }
}
