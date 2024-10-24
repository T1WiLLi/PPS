package pewpew.smash.game.network.server;

import pewpew.smash.engine.entities.MovableEntity;
import pewpew.smash.engine.entities.StaticEntity;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.EntityManager;

import java.util.Collection;
import java.awt.geom.Ellipse2D;

public class ServerCollisionManager {

    private static final int WORLD_MIN_X = 0;
    private static final int WORLD_MIN_Y = 0;
    private static final int WORLD_MAX_X = 1980;
    private static final int WORLD_MAX_Y = 1980;
    private static final int DELTA_RADIUS = 100;

    private EntityManager entityManager;

    public ServerCollisionManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void checkCollisions() {
        Collection<StaticEntity> entities = entityManager.getAllEntities();

        for (StaticEntity entity : entities) {
            checkWorldBoundaries(entity);

            for (StaticEntity other : entities) {
                if (entity == other)
                    continue;

                if (areEntitiesClose(entity, other, DELTA_RADIUS)) {
                    if (isColliding(entity, other)) {
                        handleCollision(entity, other);
                    }
                }
            }
        }
    }

    private void checkWorldBoundaries(StaticEntity entity) {
        if (entity.getX() < WORLD_MIN_X)
            entity.teleport(WORLD_MIN_X, entity.getY());
        if (entity.getY() < WORLD_MIN_Y)
            entity.teleport(entity.getX(), WORLD_MIN_Y);
        if (entity.getX() + entity.getWidth() > WORLD_MAX_X)
            entity.teleport(WORLD_MAX_X - entity.getWidth(), entity.getY());
        if (entity.getY() + entity.getHeight() > WORLD_MAX_Y) {
            entity.teleport(entity.getX(), WORLD_MAX_Y - entity.getHeight());
        }
    }

    private boolean areEntitiesClose(StaticEntity entity, StaticEntity other, int radius) {
        int dx = entity.getX() - other.getX();
        int dy = entity.getY() - other.getY();
        return (dx * dx + dy * dy) <= (radius * radius);
    }

    private boolean isColliding(StaticEntity entity, StaticEntity other) {
        if (entity instanceof Player && other instanceof Player) {
            Ellipse2D hitbox1 = ((Player) entity).getHitbox();
            Ellipse2D hitbox2 = ((Player) other).getHitbox();
            return hitbox1.intersects(hitbox2.getBounds2D());
        }

        return false;
    }

    private void handleCollision(StaticEntity entity, StaticEntity other) {
        if (entity instanceof MovableEntity && other instanceof MovableEntity) {
            MovableEntity movableEntity1 = (MovableEntity) entity;
            MovableEntity movableEntity2 = (MovableEntity) other;

            int pushFactor = 2;
            int dx = movableEntity1.getX() - movableEntity2.getX();
            int dy = movableEntity1.getY() - movableEntity2.getY();

            if (dx == 0 && dy == 0) {
                return;
            }

            int distance = (int) Math.sqrt(dx * dx + dy * dy);
            if (distance != 0) {
                dx = (dx * pushFactor) / distance;
                dy = (dy * pushFactor) / distance;

                movableEntity1.teleport(movableEntity1.getX() + dx, movableEntity1.getY() + dy);
                movableEntity2.teleport(movableEntity2.getX() - dx, movableEntity2.getY() - dy);
            }
        } else if ((entity instanceof MovableEntity && other instanceof StaticEntity) ||
                (entity instanceof StaticEntity && other instanceof MovableEntity)) {
            MovableEntity movableEntity = (entity instanceof MovableEntity) ? (MovableEntity) entity
                    : (MovableEntity) other;
            movableEntity.teleport(movableEntity.getPrevX(), movableEntity.getPrevY());
        }

    }
}
