package pewpew.smash.game.hud;

import java.awt.Color;
import java.awt.image.BufferedImage;

import lombok.Setter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.ResourcesLoader;

public class AmmoDisplayer extends HudElement { // 550, 540, 110, 45

    @Setter
    private int ammo;

    private BufferedImage sprite;

    public AmmoDisplayer(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.sprite = ResourcesLoader.getImage(ResourcesLoader.PREVIEW_PATH, "ammo");
    }

    @Override
    protected void render(Canvas canvas) {
        canvas.renderRectangle(x, y, width, height, new Color(34, 85, 24, 200));

        if (sprite != null) {
            int iconX = x + 5;
            int iconY = y + 5;
            canvas.renderImage(sprite, iconX, iconY, 42, 42);
        }

        FontFactory.IMPACT_LARGE.applyFont(canvas);
        int textX = x + 48;
        int textY = y + height - 10;
        canvas.renderString(String.valueOf(ammo), textX, textY, Color.WHITE);
    }
}
