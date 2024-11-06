package pewpew.smash.game.hud;

import java.awt.image.BufferedImage;

import lombok.Setter;
import pewpew.smash.engine.Canvas;

public class ScopeElementDisplayer extends HudElement {

    @Setter
    private BufferedImage scope;

    public ScopeElementDisplayer(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderImage(scope, x, y, width, height);
    }
}
