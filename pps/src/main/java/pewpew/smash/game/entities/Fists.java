package pewpew.smash.game.entities;

import java.awt.Color;
import java.awt.Shape;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.entities.UpdatableEntity;
import pewpew.smash.game.input.MouseHandler;

public class Fists extends UpdatableEntity {
    private int leftFistX, leftFistY;
    private int rightFistX, rightFistY;
    private int centerX, centerY;
    private int radius = 18;

    private boolean isLeftFistAttacking = false;
    private boolean isRightFistAttacking = false;
    private boolean isReturning = false;
    private boolean isAttacking = false;
    private float attackProgress = 0.0f;
    private float attackSpeed = 0.025f;
    private int attackDistance = 18;

    public Fists(Player player) {
        setDimensions(6, 6);
        updatePosition(player.getX(), player.getY(), player.getRotation());
    }

    @Override
    public void updateClient() {
        if (MouseHandler.isLeftMousePressed() && !isAttacking) {
            isAttacking = true;
            isLeftFistAttacking = true;
        }
        if (isAttacking) {
            attack();
        }
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

    public Shape getHitbox() {
        return null;
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

        int forwardOffset = (int) (attackDistance * attackProgress);

        int forwardX = (int) (forwardOffset * Math.cos(angleRad));
        int forwardY = (int) (forwardOffset * Math.sin(angleRad));

        leftFistX = (int) (centerX + radius * Math.cos(leftAngle)) + (isLeftFistAttacking ? forwardX : 0);
        leftFistY = (int) (centerY + radius * Math.sin(leftAngle)) + (isLeftFistAttacking ? forwardY : 0);

        rightFistX = (int) (centerX + radius * Math.cos(rightAngle)) + (isRightFistAttacking ? forwardX : 0);
        rightFistY = (int) (centerY + radius * Math.sin(rightAngle)) + (isRightFistAttacking ? forwardY : 0);
    }

    private void attack() {
        if (isLeftFistAttacking) {
            if (!isReturning) {
                attackProgress += attackSpeed;
                if (attackProgress >= 1.0f) {
                    attackProgress = 1.0f;
                    isReturning = true;
                }
            } else {
                attackProgress -= attackSpeed;
                if (attackProgress <= 0.0f) {
                    attackProgress = 0.0f;
                    isLeftFistAttacking = false;
                    isReturning = false;
                    isRightFistAttacking = true;
                }
            }
        } else if (isRightFistAttacking) {
            if (!isReturning) {
                attackProgress += attackSpeed;
                if (attackProgress >= 1.0f) {
                    attackProgress = 1.0f;
                    isReturning = true;
                }
            } else {
                attackProgress -= attackSpeed;
                if (attackProgress <= 0.0f) {
                    attackProgress = 0.0f;
                    isRightFistAttacking = false;
                    isReturning = false;
                    isAttacking = false;
                }
            }
        }
    }
}
