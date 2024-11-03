package pewpew.smash.game.objects;

import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.Polygon;

import java.awt.Color;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.controls.MouseInput;

public class Fist extends MeleeWeapon {

    private int leftFistX, leftFistY;
    private int rightFistX, rightFistY;
    private int centerX, centerY;
    private int radius = 20;

    private Polygon damageZone;

    private boolean isLeftFistAttacking = false;
    private boolean isRightFistAttacking = false;

    public Fist(String name, String description, BufferedImage preview) {
        super(name, description, preview);
        setDimensions(6, 6);
        this.damageZone = new Polygon();
    }

    @Override
    public void updateClient() {
        if (getOwner().getMouseInput() == MouseInput.LEFT_CLICK && !isAttacking) {
            isAttacking = true;
            isLeftFistAttacking = true;
        }

        if (isAttacking) {
            attack();
        }

        updatePosition(getOwner().getX() + getOwner().getWidth() / 2, getOwner().getY() + getOwner().getHeight() / 2,
                getOwner().getRotation());
        updateDamageZone();
    }

    @Override
    public void updateServer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void render(Canvas canvas) {
        renderFist(canvas, leftFistX, leftFistY);
        renderFist(canvas, rightFistX, rightFistY);

        Color damageZoneColor = new Color(255, 0, 0, 125);
        canvas.renderPolygon(damageZone, damageZoneColor);
    }

    @Override
    public void attack() {
        attackProgress += (isReturning ? -attackSpeed : attackSpeed);

        if (attackProgress >= 1.0f) {
            attackProgress = 1.0f;
            isReturning = true;
        } else if (attackProgress <= 0.0f) {
            attackProgress = 0.0f;
            isReturning = false;

            if (isLeftFistAttacking) {
                isLeftFistAttacking = false;
                isRightFistAttacking = true;
            } else {
                isRightFistAttacking = false;
                isAttacking = false;
            }
        }
    }

    @Override
    public Shape getHitbox() {
        return null;
    }

    private void updatePosition(int playerX, int playerY, float rotation) {
        this.centerX = playerX;
        this.centerY = playerY;

        teleport(centerX, centerY);
        updateFistPositions(rotation);
    }

    private void renderFist(Canvas canvas, int x, int y) {
        int fistRadius = 16;
        canvas.renderCircle(x, y, fistRadius, Color.BLACK);
        canvas.renderCircle(x + 1, y + 1, fistRadius - 2, new Color(229, 194, 152));
    }

    private void updateFistPositions(float rotation) {
        double angleRad = Math.toRadians(rotation);

        double leftAngle = angleRad - Math.PI / 4;
        double rightAngle = angleRad + Math.PI / 4;

        int forwardOffset = (int) (range * attackProgress);

        int forwardX = (int) (forwardOffset * Math.cos(angleRad));
        int forwardY = (int) (forwardOffset * Math.sin(angleRad));

        leftFistX = (int) (centerX - 6 + radius * Math.cos(leftAngle)) + (isLeftFistAttacking ? forwardX : 0);
        leftFistY = (int) (centerY - 6 + radius * Math.sin(leftAngle)) + (isLeftFistAttacking ? forwardY : 0);

        rightFistX = (int) (centerX - 6 + radius * Math.cos(rightAngle)) + (isRightFistAttacking ? forwardX : 0);
        rightFistY = (int) (centerY - 6 + radius * Math.sin(rightAngle)) + (isRightFistAttacking ? forwardY : 0);
    }

    private void updateDamageZone() {
        double angleRad = Math.toRadians(getOwner().getRotation());

        int centerX = getOwner().getX() + getOwner().getWidth() / 2;
        int centerY = getOwner().getY() + getOwner().getHeight() / 2;

        double baseCenterX = centerX + (this.range * 1.6) * Math.cos(angleRad);
        double baseCenterY = centerY + (this.range * 1.6) * Math.sin(angleRad);

        int basePoint1X = (int) (baseCenterX + ((radius * 2) / 1.4) * -Math.sin(angleRad));
        int basePoint1Y = (int) (baseCenterY + ((radius * 2) / 1.4) * Math.cos(angleRad));

        int basePoint2X = (int) (baseCenterX - ((radius * 2) / 1.4) * -Math.sin(angleRad));
        int basePoint2Y = (int) (baseCenterY - ((radius * 2) / 1.4) * Math.cos(angleRad));

        damageZone.reset();
        damageZone.addPoint(centerX, centerY);
        damageZone.addPoint(basePoint1X, basePoint1Y);
        damageZone.addPoint(basePoint2X, basePoint2Y);
    }
}
