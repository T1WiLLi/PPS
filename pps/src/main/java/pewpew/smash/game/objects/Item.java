package pewpew.smash.game.objects;

import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.ToString;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.entities.Player;

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

    private Player owner;

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

    // We would probably want to override this function in the actual class (while
    // still calling the super)
    public Item pickup(Player newOwner) {
        this.owner = newOwner;
        isOnScreen = false;
        return this;
    }

    // We would probably want to override this function in the actual class (while
    // still calling the super)
    public void drop(int newX, int newY) {
        this.owner = null;
        this.x = newX;
        this.y = newY;
        isOnScreen = true;
    }
}
