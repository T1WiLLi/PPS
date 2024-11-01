package pewpew.smash.game.entities;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.LinkedList;
import java.util.Queue;

import lombok.Getter;
import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.entities.MovableEntity;
import pewpew.smash.game.objects.RangedWeapon;

public class Bullet extends MovableEntity {

    private static final int TRAIL_LENGTH = 10;

    @Getter
    private int playerOwnerID;
    @Getter
    private int damage;
    @Getter
    private int maxRange;
    @Getter
    private float distanceTraveled = 0;
    private Queue<int[]> trailPositions = new LinkedList<>();

    public Bullet(Player owner) {
        this.playerOwnerID = owner.getId();
        this.damage = owner.getEquippedWeapon().getDamage();
        this.maxRange = owner.getEquippedWeapon().getRange();
        setDimensions(4, 4);
        setDirection(owner.getDirection());
        teleport(owner.getX(), owner.getY());
        setSpeed(((RangedWeapon) owner.getEquippedWeapon()).getBulletSpeed());
    }

    @Override
    public void updateClient() {
    }

    @Override
    public void updateServer() {
        move(1);

        double dx = getX() - getPrevX();
        double dy = getY() - getPrevY();
        distanceTraveled += Math.sqrt(dx * dx + dy * dy);

        trailPositions.add(new int[] { getX(), getY() });
        if (trailPositions.size() > TRAIL_LENGTH) {
            trailPositions.poll();
        }
    }

    @Override
    public void render(Canvas canvas) {
        renderTrail(canvas);

        canvas.setColor(new Color(200, 200, 200));
        canvas.renderCircle(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(),
                new Color(200, 200, 200));
    }

    private void renderTrail(Canvas canvas) {
        int alpha = 150;
        int alphaDecrement = 15;

        for (int[] position : trailPositions) {
            canvas.setColor(new Color(200, 200, 200, alpha));
            canvas.renderCircle(position[0] - getWidth() / 2, position[1] - getHeight() / 2, getWidth(),
                    new Color(200, 200, 200, alpha));
            alpha = Math.max(0, alpha - alphaDecrement);
        }
    }

    @Override
    public Shape getHitbox() {
        return new Ellipse2D.Float(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
    }
}
