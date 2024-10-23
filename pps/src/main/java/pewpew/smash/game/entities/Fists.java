package pewpew.smash.game.entities;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.entities.UpdatableEntity;

import java.awt.Color;

public class Fists extends UpdatableEntity {

    private int leftFistX, leftFistY;
    private int rightFistX, rightFistY;

    public Fists(Player player) {
        setDimensions(6, 6);
        updatePosition(player.getX(), player.getY(), player.getRotation());
    }

    @Override
    public void updateClient() {
        // Nothings
    }

    @Override
    public void updateServer() {
        // Nothings
    }

    @Override
    public void render(Canvas canvas) {
        renderFist(canvas, leftFistX, leftFistY);
        renderFist(canvas, rightFistX, rightFistY);
    }

    public void updatePosition(int playerX, int playerY, float rotation) {
        teleport(playerX, playerY);
        updateFistPositions(rotation);
    }

    private void renderFist(Canvas canvas, int x, int y) {
        int fistRadius = 8;
        canvas.renderCircle(x, y, fistRadius, Color.BLACK);
        canvas.renderCircle(x + 2, y + 2, fistRadius - 2, new Color(229, 194, 152));
    }

    private void updateFistPositions(float rotation) {
        int fistDistance = 20;
        double angleRad = Math.toRadians(rotation);

        leftFistX = (int) (getX() + fistDistance * Math.cos(angleRad - Math.PI / 4));
        leftFistY = (int) (getY() + fistDistance * Math.sin(angleRad - Math.PI / 4));

        rightFistX = (int) (getX() + fistDistance * Math.cos(angleRad + Math.PI / 4));
        rightFistY = (int) (getY() + fistDistance * Math.sin(angleRad + Math.PI / 4));
    }
}
