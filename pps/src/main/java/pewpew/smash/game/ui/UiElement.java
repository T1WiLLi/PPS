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

    protected abstract void render(Canvas canvas);

    protected abstract void handleMouseInput();

    protected abstract void handleMouseMove();

    public UiElement(int x, int y, int w, int h) {
        xPos = x;
        yPos = y;
        width = w;
        height = h;
        loadBounds();
    }

    protected void update() {
        updateScaledBounds();
        handleMouseInput();
        handleMouseMove();
    }

    protected void updateScaledBounds() {
        this.bounds.setBounds(ScaleUtils.getScaledBounds(this.bounds));
    }

    private void loadBounds() {
        bounds = new Rectangle(xPos, yPos, width, height);
    }
}
