package pewpew.smash.game.entities;

import java.awt.Color;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.entities.MovableEntity;
import pewpew.smash.game.input.GamePad;
import pewpew.smash.game.network.User;

@ToString(callSuper = true)
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
        setDimensions(20, 20);
        teleport(100, 100);
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
    public void updateServer() {
        prevX = getX();
        prevY = getY();
        prevRotation = getRotation();
        setDirection(GamePad.getInstance().getDirection());
        move(1);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderCircle(x, y, 10, Color.RED);
        canvas.renderString(User.getInstance().getUsername(), x - 10, y - 10, Color.WHITE);
    }

    public boolean hasPositionChanged() {
        return getX() != prevX || getY() != prevY || getRotation() != prevRotation;
    }
}
