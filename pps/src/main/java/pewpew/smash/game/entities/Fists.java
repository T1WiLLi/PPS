package pewpew.smash.game.entities;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.entities.UpdatableEntity;

import java.awt.Color;

public class Fists extends UpdatableEntity {

    private int leftFistX, leftFistY;
    private int rightFistX, rightFistY;

    public Fists(Player player) {
        setDimensions(6, 6);
        teleport(player.getX(), player.getY());
        updateFistPositions();
    }

    @Override
    public void updateClient() {
        updateFistPositions();
    }

    @Override
    public void updateServer() {

    }

    @Override
    public void render(Canvas canvas) {
        renderFist(canvas, leftFistX, leftFistY);
        renderFist(canvas, rightFistX, rightFistY);
    }

    private void renderFist(Canvas canvas, int x, int y) {
        int fistRadius = 6;
        canvas.renderCircle(x, y, fistRadius, Color.BLACK);
        canvas.renderCircle(x + 2, y + 2, fistRadius - 2, new Color(229, 194, 152));
    }

    private void updateFistPositions() {
        int offsetX = 20;
        int offsetY = 10;

        leftFistX = getX() - offsetX;
        leftFistY = getY() + offsetY;

        rightFistX = getX() + offsetX;
        rightFistY = getY() + offsetY;
    }
}
