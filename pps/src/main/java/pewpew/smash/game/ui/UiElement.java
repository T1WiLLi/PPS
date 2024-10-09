package pewpew.smash.game.ui;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.utils.ScaleUtils;

@ToString
public abstract class UiElement {
    @Getter
    @Setter
    protected int xPos, yPos, width, height;
    @Getter
    protected Rectangle bounds;

    protected abstract void loadSprites(BufferedImage spriteSheet);

    protected abstract void update();

    protected abstract void render(Canvas canvas);

    public UiElement(int x, int y, int w, int h) {
        xPos = x;
        yPos = y;
        width = w;
        height = h;
        loadBounds();
    }

    protected void updateScaledBounds() {
        this.bounds.setBounds(
                ScaleUtils.scaleX(this.xPos),
                ScaleUtils.scaleY(this.yPos),
                ScaleUtils.scaleWidth(this.width),
                ScaleUtils.scaleHeight(this.height));
    }

    private void loadBounds() {
        bounds = new Rectangle(xPos, yPos, width, height);
    }
}
