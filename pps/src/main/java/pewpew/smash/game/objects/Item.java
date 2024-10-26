package pewpew.smash.game.objects;

import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.ToString;
import pewpew.smash.engine.Canvas;

@ToString
@Getter
public abstract class Item {

    private BufferedImage preview;

    private String name;
    private String description;
    private int x;
    private int y;
    private int width;
    private int height;

    @Getter
    private boolean isOnScreen;

    // Render when using the actual item in the game world
    public abstract void render(Canvas canvas);

    // Render when seeing from inventory or when the item is on the ground
    public abstract void preview(Canvas canvas);

    public Item(String name, String description, BufferedImage preview) {
        this.name = name;
        this.description = description;
        this.preview = preview;
    }

    public void teleport(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Item pickup() {
        isOnScreen = false;
        return this;
    }

    public void drop(int newX, int newY) {
        this.x = newX;
        this.y = newY;
        isOnScreen = true;
    }
}
