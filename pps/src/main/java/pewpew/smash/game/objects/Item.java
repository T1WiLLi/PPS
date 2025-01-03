package pewpew.smash.game.objects;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.ItemManager;

@ToString(exclude = { "owner" })
@Getter
public abstract class Item {

    @Setter
    private int id;

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

    @Getter
    private Rectangle hitbox;

    // Render when using the actual item in the game world
    public abstract void render(Canvas canvas);

    public Item(int id, String name, String description, BufferedImage preview) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.preview = preview;
        setDimensions(42, 42);
        this.hitbox = new Rectangle(x, y, width, height);
    }

    public void preview(Canvas canvas) {
        canvas.renderImage(getPreview(), getX(), getY(), getWidth(), getHeight());
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
        ItemManager.getInstance(true).removeItem(this);
        this.owner = newOwner;
        isOnScreen = false;
        return this;
    }

    // We would probably want to override this function in the actual class (while
    // still calling the super)
    public void drop() {
        ItemManager.getInstance(true).addItem(this);
        if (owner != null) {
            this.x = owner.getX();
            this.y = owner.getY();
            this.owner = null;
        }
        isOnScreen = true;
    }
}
