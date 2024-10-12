package pewpew.smash.game.states;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import pewpew.smash.engine.Canvas;

public class Playing implements State {

    public Playing() {
    }

    @Override
    public void update(double deltaTime) {

    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderString("HI GUYS", 400, 300, Color.RED);
    }

    @Override
    public void handleMousePress(MouseEvent e) {

    }

    @Override
    public void handleMouseRelease(MouseEvent e) {

    }

    @Override
    public void handleMouseMove(MouseEvent e) {

    }

    @Override
    public void handleMouseDrag(MouseEvent e) {

    }

    @Override
    public void handleKeyPress(KeyEvent e) {

    }

    @Override
    public void handleKeyRelease(KeyEvent e) {

    }
}
