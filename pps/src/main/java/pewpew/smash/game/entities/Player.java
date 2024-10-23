package pewpew.smash.game.entities;

import java.awt.Color;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.entities.MovableEntity;
import pewpew.smash.game.network.User;

@ToString(callSuper = true)
public class Player extends MovableEntity {

    private Fists fists;

    @Setter
    @Getter
    private float rotation;
    private int prevX, prevY;
    private float prevRotation;

    @Setter
    @Getter
    private String username;

    public Player(int id) {
        this.fists = new Fists(this);
        setDimensions(20, 20);
        teleport(100, 100);
        setSpeed(2);
        this.rotation = 0f;
        this.id = id;
    }

    public Player(int id, String username) {
        this(id);
        this.username = username;
    }

    @Override
    public void updateClient() {
        this.fists.updatePosition(getX(), getY(), getRotation());
    }

    @Override
    public void updateServer() {
        move(1);
        this.prevX = getX();
        this.prevY = getY();
        this.prevRotation = getRotation();
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderCircle(x, y, width, new Color(168, 168, 168));
        canvas.renderCircle(x + 4, y + 4, width - 4, new Color(229, 194, 152));

        this.fists.render(canvas);

        canvas.renderString(User.getInstance().getUsername() + "-" + id, x - width, y - height, Color.WHITE);
    }

    public boolean hasStateChanged() {
        return (getX() != prevX || getY() != prevY) || getRotation() != prevRotation;
    }
}
