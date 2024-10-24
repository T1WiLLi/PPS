package pewpew.smash.game.entities;

import java.awt.Color;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.entities.UpdatableEntity;

public class Fists extends UpdatableEntity {
    private int leftFistX, leftFistY;
    private int rightFistX, rightFistY;
    private int centerX, centerY;
    private int radius = 18;

    public Fists(Player player) {
        setDimensions(6, 6);
        updatePosition(player.getX(), player.getY(), player.getRotation());
    }

    @Override
    public void updateClient() {
        // No client-side updates needed
    }

    @Override
    public void updateServer() {
        // No server-side updates needed
    }

    @Override
    public void render(Canvas canvas) {
        renderFist(canvas, leftFistX, leftFistY);
        renderFist(canvas, rightFistX, rightFistY);
    }

    public void updatePosition(int playerX, int playerY, float rotation) {
        this.centerX = playerX + 10;
        this.centerY = playerY + 10;

        teleport(centerX, centerY);
        updateFistPositions(rotation);
    }

    private void renderFist(Canvas canvas, int x, int y) {
        int fistRadius = 8;
        canvas.renderCircle(x, y, fistRadius, Color.BLACK);
        canvas.renderCircle(x + 2, y + 2, fistRadius - 2, new Color(229, 194, 152));
    }

    private void updateFistPositions(float rotation) {
        double angleRad = Math.toRadians(rotation);

        double leftAngle = angleRad - Math.PI / 4;
        double rightAngle = angleRad + Math.PI / 4;

        leftFistX = (int) (centerX + radius * Math.cos(leftAngle));
        leftFistY = (int) (centerY + radius * Math.sin(leftAngle));

        rightFistX = (int) (centerX + radius * Math.cos(rightAngle));
        rightFistY = (int) (centerY + radius * Math.sin(rightAngle));
    }
}