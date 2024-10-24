package pewpew.smash.game.entities;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.Shape;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.entities.MovableEntity;
import pewpew.smash.game.hud.HudManager;
import pewpew.smash.game.network.User;

@ToString(callSuper = true)
public class Player extends MovableEntity {

    private Fists fists;

    @Setter
    @Getter
    private float rotation;

    @Setter
    @Getter
    private String username;

    @Setter
    @Getter
    private int health;

    public Player(int id) {
        this.fists = new Fists(this);
        setDimensions(20, 20);
        teleport(100, 100);
        setSpeed(2);
        this.rotation = 0f;
        this.id = id;
        this.health = 100;

        HudManager.getInstance().setPlayer(this);
    }

    public Player(int id, String username) {
        this(id);
        this.username = username;
    }

    @Override
    public void updateClient() {
        this.fists.updateClient();
        this.fists.updatePosition(getX(), getY(), getRotation());
    }

    @Override
    public void updateServer() {
        move(1);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderCircle(x, y, width, new Color(168, 168, 168));
        canvas.renderCircle(x + 4, y + 4, width - 4, new Color(229, 194, 152));

        this.fists.render(canvas);

        canvas.renderString(User.getInstance().getUsername() + "-" + id, x - width, y - height, Color.WHITE);
    }

    public Shape getHitbox() {
        return new Ellipse2D.Float(getX(), getY(), width * 2, width * 2);
    }
}
