package pewpew.smash.game.entities;

import java.awt.Color;

import lombok.Getter;
import lombok.Setter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.entities.MovableEntity;
import pewpew.smash.game.input.GamePad;

public class Player extends MovableEntity {
    private int prevX, prevY;
    private float prevRotation;

    @Setter
    @Getter
    private String username;

    @Setter
    @Getter
    private float rotation;

    public Player(int id) {
        setDimensions(10, 10);
        setSpeed(2);
        this.id = id;
    }

    public Player(int id, String username) {
        this(id);
        this.username = username;
    }

    @Override
    public void updateClient() {

    }

    @Override
    public void updateServer(double deltaTime) {
        prevX = getX();
        prevY = getY();
        prevRotation = getRotation();
        setDirection(GamePad.getInstance().getDirection());
        move(deltaTime);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderCircle(x, y, width / 2 + height / 2, Color.RED);
    }

    public boolean hasPositionChanged() {
        return getX() != prevX || getY() != prevY || getRotation() != prevRotation;
    }
}
