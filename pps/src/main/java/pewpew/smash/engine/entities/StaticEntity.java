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

    public boolean isColliding(StaticEntity other) {
        if (this.getHitbox() instanceof Ellipse2D && other.getHitbox() instanceof Ellipse2D) {
            double thisCenterX = getX() + getWidth() / 2.0;
            double thisCenterY = getY() + getHeight() / 2.0;
            double otherCenterX = other.getX() + other.getWidth() / 2.0;
            double otherCenterY = other.getY() + other.getHeight() / 2.0;

            double thisRadius = getWidth() / 2.0;
            double otherRadius = other.getWidth() / 2.0;

            double dx = thisCenterX - otherCenterX;
            double dy = thisCenterY - otherCenterY;
            double distanceSquared = dx * dx + dy * dy;

            double radiiSum = thisRadius + otherRadius;
            return distanceSquared <= radiiSum * radiiSum;
        }
        return getHitbox().intersects(other.getHitbox().getBounds2D());
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
