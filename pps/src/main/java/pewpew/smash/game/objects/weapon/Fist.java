package pewpew.smash.game.objects.weapon;

import java.awt.Shape;
import java.awt.image.BufferedImage;

import java.awt.Color;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.controls.MouseInput;
import pewpew.smash.game.objects.MeleeWeapon;

public class Fist extends MeleeWeapon {

    private int leftFistX, leftFistY;
    private int rightFistX, rightFistY;
    private int centerX, centerY;
    private int radius = 18;

    private boolean isLeftFistAttacking = false;
    private boolean isRightFistAttacking = false;

    public Fist(String name, String description, BufferedImage preview) {
        super(name, description, preview);
        setDimensions(6, 6);
    }

    @Override
    public void updateClient() {
        if (getOwner() != null) {
            updatePosition((int) getOwner().getX(), (int) getOwner().getY(), getOwner().getRotation());
        }
    }

    @Override
    public void updateServer() {
        if (getOwner().getMouseInput() == MouseInput.LEFT_CLICK && !isAttacking) {
            isAttacking = true;
            isLeftFistAttacking = true;
        }

        if (isAttacking) {
            attack();
        }

        updatePosition(getOwner().getX(), getOwner().getY(), getOwner().getRotation());
    }

    @Override
    public void render(Canvas canvas) {
        renderFist(canvas, leftFistX, leftFistY);
        renderFist(canvas, rightFistX, rightFistY);
    }

    @Override
    public void preview(Canvas canvas) {

    }

    @Override
    public void attack() {
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

    @Override
    public Shape getHitbox() {
        return null;
    }

    private void updatePosition(int playerX, int playerY, float rotation) {
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

        int forwardOffset = (int) (range * attackProgress);

        int forwardX = (int) (forwardOffset * Math.cos(angleRad));
        int forwardY = (int) (forwardOffset * Math.sin(angleRad));

        leftFistX = (int) (centerX + radius * Math.cos(leftAngle)) + (isLeftFistAttacking ? forwardX : 0);
        leftFistY = (int) (centerY + radius * Math.sin(leftAngle)) + (isLeftFistAttacking ? forwardY : 0);

        rightFistX = (int) (centerX + radius * Math.cos(rightAngle)) + (isRightFistAttacking ? forwardX : 0);
        rightFistY = (int) (centerY + radius * Math.sin(rightAngle)) + (isRightFistAttacking ? forwardY : 0);
    }
}
