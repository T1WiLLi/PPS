package pewpew.smash.engine.entities;

import java.awt.Color;
import java.awt.Rectangle;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.controls.Direction;

@Getter
@ToString
public abstract class MovableEntity extends UpdatableEntity {

    @Setter
    private Direction direction = Direction.UP;
    private boolean hasMoved;
    private int speed;

    public void move() {
        hasMoved = true;
        x += direction.getVelocityX(speed);
        y += direction.getVelocityY(speed);
    }

    public void move(Direction direction) {
        this.direction = direction;
        move();
    }

    public void moveUp() {
        this.move(Direction.UP);
    }

    public void moveDown() {
        this.move(Direction.DOWN);
    }

    public void moveLeft() {
        this.move(Direction.LEFT);
    }

    public void moveRight() {
        this.move(Direction.RIGHT);
    }

    public void setSpeed(int speed) {
        if (speed < 0) {
            throw new IllegalArgumentException("Speed can't be negative");
        }
        this.speed = speed;
    }

    public boolean hasMoved() {
        return this.hasMoved;
    }

    public void renderHitbox(Canvas canvas) {
        Rectangle rectangle = super.getBounds();
        canvas.renderRectangleBorder(rectangle.x, rectangle.y, rectangle.width, rectangle.height, 1, Color.RED);
    }
}
