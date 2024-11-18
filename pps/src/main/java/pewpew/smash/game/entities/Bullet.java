package pewpew.smash.game.entities;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.LinkedList;
import java.util.Queue;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pewpew.smash.engine.Canvas;
import pewpew.smash.game.objects.RangedWeapon;

@ToString
public class Bullet {
    private static final Color BULLET_CORE = new Color(255, 200, 0);
    private static final Color BULLET_OUTER = new Color(255, 140, 0, 180);
    private static final Color TRAIL_COLOR = new Color(255, 165, 0, 150);

    private int effectLifetime = 0;

    @Getter
    @Setter
    private int id;
    @Getter
    private final int playerOwnerID;
    @Getter
    private final int damage;
    @Getter
    private final int maxRange;
    @Getter
    private float distanceTraveled = 0;

    private final Queue<TrailPoint> trailPositions = new LinkedList<>();

    @Getter
    @Setter
    private float x, y;
    private float prevX, prevY;
    private int coreWidth = 4, coreHeight = 4;
    private int glowWidth = 8, glowHeight = 8;
    private float speed;
    private final float velocityX;
    private final float velocityY;
    private final float rotation;

    @Setter
    private boolean shouldRenderEffect = false;

    private static class TrailPoint {
        int x, y;
        long createTime;

        TrailPoint(int x, int y) {
            this.x = x;
            this.y = y;
            this.createTime = System.currentTimeMillis();
        }
    }

    public Bullet(Player owner) {
        this.playerOwnerID = owner.getId();
        RangedWeapon equippedWeapon = (RangedWeapon) owner.getEquippedWeapon();
        this.rotation = owner.getRotation();
        this.damage = equippedWeapon.getDamage();
        this.maxRange = equippedWeapon.getRange();

        float weaponCenterX = owner.getX() + owner.getWidth() / 2;
        float weaponCenterY = owner.getY() + owner.getHeight() / 2;
        double angleRad = Math.toRadians(this.rotation);
        this.x = weaponCenterX + (int) (equippedWeapon.getWeaponLength() * Math.cos(angleRad));
        this.y = weaponCenterY + (int) (equippedWeapon.getWeaponLength() * Math.sin(angleRad));

        this.speed = equippedWeapon.getBulletSpeed();

        this.velocityX = (float) (Math.cos(angleRad) * this.speed);
        this.velocityY = (float) (Math.sin(angleRad) * this.speed);
    }

    public void teleport(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void updateClient() {
        if (!shouldRenderEffect) {
            prevX = x;
            prevY = y;
            x += velocityX;
            y += velocityY;

            trailPositions.add(new TrailPoint((int) x, (int) y));

            long currentTime = System.currentTimeMillis();
            while (!trailPositions.isEmpty() &&
                    currentTime - trailPositions.peek().createTime > 150) {
                trailPositions.poll();
            }
        }
    }

    public void updateServer() {
        prevX = x;
        prevY = y;
        x += velocityX;
        y += velocityY;

        double dx = x - prevX;
        double dy = y - prevY;
        distanceTraveled += Math.sqrt(dx * dx + dy * dy);
    }

    public void render(Canvas canvas) {
        if (!shouldRenderEffect) {
            renderTrail(canvas);

            canvas.renderCircle(
                    (int) x - glowWidth / 2,
                    (int) y - glowHeight / 2,
                    glowWidth,
                    BULLET_OUTER);

            canvas.renderCircle(
                    (int) x - coreWidth / 2,
                    (int) y - coreHeight / 2,
                    coreWidth,
                    BULLET_CORE);
        } else {
            renderHitEffect(canvas);
        }
    }

    public Shape getHitbox() {
        return new Ellipse2D.Float(x - coreWidth / 2, y - coreHeight / 2, coreWidth, coreHeight);
    }

    private void renderTrail(Canvas canvas) {
        if (trailPositions.isEmpty())
            return;

        long currentTime = System.currentTimeMillis();
        int index = 0;
        int totalPoints = trailPositions.size();

        for (TrailPoint point : trailPositions) {
            float ageRatio = (currentTime - point.createTime) / 150f;
            float positionRatio = (float) index / totalPoints;
            int alpha = (int) (150 * (1 - ageRatio) * (1 - positionRatio));

            int size = (int) (coreWidth * (1 + positionRatio * 0.5f));

            Color trailColor = new Color(
                    TRAIL_COLOR.getRed(),
                    TRAIL_COLOR.getGreen(),
                    TRAIL_COLOR.getBlue(),
                    Math.max(0, Math.min(255, alpha)));

            canvas.renderCircle(
                    point.x - size / 2,
                    point.y - size / 2,
                    size,
                    trailColor);

            index++;
        }
    }

    private void renderHitEffect(Canvas canvas) {
        effectLifetime++;

        int effectSize = 20;
        Color effectColor = new Color(255, 140, 0, Math.max(0, 128 - effectLifetime * 8));

        canvas.renderCircle(
                (int) x - effectSize / 2,
                (int) y - effectSize / 2,
                effectSize,
                effectColor);

        int numSparks = 6;
        double sparkSpeed = 3.0;
        int sparkSize = 4;
        Color sparkColor = new Color(255, 200, 0, Math.max(0, 255 - effectLifetime * 15));

        for (int i = 0; i < numSparks; i++) {
            double angle = Math.toRadians(60 * i);
            int sparkDistance = (int) (sparkSpeed * effectLifetime);
            int sparkX = (int) (x + Math.cos(angle) * sparkDistance);
            int sparkY = (int) (y + Math.sin(angle) * sparkDistance);

            canvas.renderCircle(
                    sparkX - sparkSize / 2,
                    sparkY - sparkSize / 2,
                    sparkSize,
                    sparkColor);
        }
    }
}
