package pewpew.smash.game.entities;

import java.awt.Color;

import lombok.Getter;
import lombok.Setter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.entities.MovableEntity;

public class Player extends MovableEntity {

    @Setter
    @Getter
    String username;

    public Player(int id) {
        setDimensions(30, 30);
        setSpeed(2);
        this.id = id;
    }

    @Override
    public void updateClient() {

    }

    @Override
    public void updateServer(double deltaTime) {
        move();
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderCircle(x, y, 30, Color.WHITE);
    }

}
