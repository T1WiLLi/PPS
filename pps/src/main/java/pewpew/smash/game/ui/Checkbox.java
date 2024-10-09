package pewpew.smash.game.ui;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.utils.ResourcesLoader;

import java.awt.image.BufferedImage;
import java.awt.Color;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Checkbox extends UiElement {
    @Getter
    @Setter
    private boolean checked;

    private BufferedImage checkSprite;

    public Checkbox(int x, int y) {
        super(x, y, 25, 25);
        this.checked = false;
        loadSprites(ResourcesLoader.getImage(ResourcesLoader.MISC_PATH, "check"));
    }

    @Override
    public void update() {
        updateScaledBounds();
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderRectangleBorder(xPos, yPos, width, height, 3, Color.WHITE);
        if (checked) {
            canvas.renderImage(checkSprite, xPos, yPos - 10, this.width + 10, this.height + 10);
        }
    }

    @Override
    protected void loadSprites(BufferedImage sprite) {
        this.checkSprite = sprite;
    }
}
