package pewpew.smash.engine.entities;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pewpew.smash.engine.Canvas;

@Getter
@ToString
public abstract class StaticEntity {

    @Setter
    protected int id;
    protected int x, y, width, height;

    public abstract void render(Canvas canvas);

    public abstract Shape getHitbox();

    public void teleport(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean isColliding(StaticEntity entity, StaticEntity other) {
        return entity.getHitbox().intersects(other.getHitbox().getBounds2D());
    }

    protected void renderHitbox(Canvas canvas) {
        Shape hitbox = getHitbox();
        if (hitbox instanceof Ellipse2D) {
            canvas.renderCircleBorder((Ellipse2D) hitbox, 2, Color.RED);
        } else if (hitbox.getBounds() != null) {
            canvas.renderRectangleBorder(x, y, width, height, 2, Color.RED);
        }
    }

    protected Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
