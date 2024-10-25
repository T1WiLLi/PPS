package pewpew.smash.game.objects;

import lombok.Getter;
import lombok.ToString;
import pewpew.smash.engine.Canvas;

@ToString
@Getter
public abstract class Item {
    private String name;
    private String description;
    private int x;
    private int y;
    private int width;
    private int height;

    // Render when using the actual item in the game world
    public abstract void render(Canvas canvas);

    // Render when seeing from inventory or when the item is on the ground
    public abstract void preview(Canvas canvas);

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void teleport(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
