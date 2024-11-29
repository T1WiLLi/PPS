package pewpew.smash.game.network.server;

import pewpew.smash.engine.entities.MovableEntity;
import pewpew.smash.engine.entities.StaticEntity;
import pewpew.smash.game.entities.Bullet;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.model.PlayerState;
import pewpew.smash.game.network.packets.BroadcastMessagePacket;
import pewpew.smash.game.network.packets.PlayerDeathPacket;
import pewpew.smash.game.network.packets.PlayerInWaterWarningPacket;
import pewpew.smash.game.network.packets.PlayerOutOfWaterPacket;
import pewpew.smash.game.network.packets.PlayerStatePacket;
import pewpew.smash.game.utils.HelpMethods;
import pewpew.smash.game.world.WorldGenerator;
import pewpew.smash.game.world.entities.Bush;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ServerCollisionManager {

    private static final int DAMAGE_INTERVAL = 1000;
    private static final int WATER_DAMAGE = 10;
    private static final int WARNING_DELAY = 3000;

    private static final int WORLD_MIN_X = 0;
    private static final int WORLD_MIN_Y = 0;
    private static final int WORLD_MAX_X = WorldGenerator.getWorldWidth();
    private static final int WORLD_MAX_Y = WorldGenerator.getWorldHeight();

    private ServerWrapper server;
    private EntityManager entityManager;

    private final Map<Integer, Long> waterTimers = new HashMap<>();
    private final Map<Integer, Long> lastDamageTime = new HashMap<>();
    private final Set<Integer> playersInWater = new HashSet<>();

    private byte[][] worldData;

    public ServerCollisionManager(ServerWrapper server, EntityManager entityManager, byte[][] worldData) {
        this.server = server;
        this.entityManager = entityManager;
        this.worldData = worldData;
    }

    public void checkCollisions() {
        Collection<StaticEntity> entities = entityManager.getAllEntities();

        for (StaticEntity entity : entities) {
            checkWorldBoundaries(entity);

            for (StaticEntity other : entities) {
                if (entity == other)
                    continue;

                if (!(entity instanceof Bush) && !(other instanceof Bush) && areEntitiesClose(entity, other)) {
                    if (entity.isColliding(other)) {
                        handleCollision(entity, other);
                    }
                }
            }
        }
        checkBulletCollision();
    }

    public void checkWaterCollision() {
        long currentTime = System.currentTimeMillis();

        entityManager.getPlayerEntities().forEach(player -> {
            int playerTileX = player.getX() / WorldGenerator.TILE_SIZE;
            int playerTileY = player.getY() / WorldGenerator.TILE_SIZE;

            if (playerTileX >= 0 && playerTileX < worldData.length && playerTileY >= 0
                    && playerTileY < worldData[0].length) {
                boolean isInWater = worldData[playerTileX][playerTileY] == 2;

                if (isInWater) {
                    handlePlayerInWater(player, currentTime);
                } else {
                    handlePlayerOutOfWater(player);
                }
            }
        });
    }

    private void handlePlayerInWater(Player player, long currentTime) {
        int playerId = player.getId();

        if (!playersInWater.contains(playerId)) {
            player.setSpeed(1f);
            playersInWater.add(playerId);
            waterTimers.put(playerId, currentTime);
            PlayerInWaterWarningPacket warningPacket = new PlayerInWaterWarningPacket(playerId);
            server.sendToUDP(playerId, warningPacket);
        }

        if (currentTime - waterTimers.getOrDefault(playerId, 0L) >= WARNING_DELAY) {
            if (currentTime - lastDamageTime.getOrDefault(playerId, 0L) >= DAMAGE_INTERVAL) {
                player.setHealth(player.getHealth() - WATER_DAMAGE);
                lastDamageTime.put(playerId, currentTime);

                PlayerStatePacket statePacket = new PlayerStatePacket(
                        new PlayerState(player.getId(), player.getHealth()));
                server.sendToUDP(playerId, statePacket);

                if (player.getHealth() <= 0) {
                    entityManager.removePlayerEntity(player.getId());

                    String deathMessage = String.format("%s tried to swim", player.getUsername());
                    BroadcastMessagePacket messagePacket = new BroadcastMessagePacket(deathMessage);
                    server.sendToAllTCP(messagePacket);

                    HelpMethods.dropInventoryOfDeadPlayer(player.getInventory(), server);

                    PlayerDeathPacket deathPacket = new PlayerDeathPacket(player.getId(),
                            entityManager.getRandomAlivePlayerID());
                    server.sendToAllTCP(deathPacket);
                }
            }
        }
    }

    private void handlePlayerOutOfWater(Player player) {
        int playerId = player.getId();

        if (playersInWater.contains(playerId)) {
            player.setSpeed(2f);
            playersInWater.remove(playerId);
            waterTimers.remove(playerId);
            lastDamageTime.remove(playerId);

            PlayerOutOfWaterPacket outOfWaterPacket = new PlayerOutOfWaterPacket(playerId);
            server.sendToUDP(playerId, outOfWaterPacket);
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

    private boolean areEntitiesClose(StaticEntity entity, StaticEntity other) {
        Shape hitbox1 = entity.getHitbox();
        Shape hitbox2 = other.getHitbox();

        if (hitbox1 instanceof Ellipse2D.Float && hitbox2 instanceof Ellipse2D.Float) {
            Ellipse2D.Float circle1 = (Ellipse2D.Float) hitbox1;
            Ellipse2D.Float circle2 = (Ellipse2D.Float) hitbox2;

            double dx = circle1.getCenterX() - circle2.getCenterX();
            double dy = circle1.getCenterY() - circle2.getCenterY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            double radius1 = circle1.getWidth() / 2.0;
            double radius2 = circle2.getWidth() / 2.0;

            return distance <= (radius1 + radius2);
        }

        return hitbox1.getBounds2D().intersects(hitbox2.getBounds2D()) || hitbox1.intersects(hitbox2.getBounds2D());
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

            double prevX = movableEntity.getPrevX();
            double prevY = movableEntity.getPrevY();
            double currX = movableEntity.getX();
            double currY = movableEntity.getY();

            double deltaX = currX - prevX;
            double deltaY = currY - prevY;

            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                movableEntity.teleport((int) prevX, (int) currY);
            } else {
                movableEntity.teleport((int) currX, (int) prevY);
            }
        }
    }

    private void checkBulletCollision() {
        Collection<Bullet> bullets = ServerBulletTracker.getInstance().getBullets();
        Collection<StaticEntity> staticEntities = entityManager.getStaticEntities();

        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            boolean bulletRemoved = false;

            for (StaticEntity staticEntity : staticEntities) {
                if (staticEntity instanceof Bush) {
                    continue;
                }

                if (bullet.isCollidingWith(staticEntity)) {
                    ServerBulletTracker.getInstance().removeBullet(bullet);
                    bulletRemoved = true;
                    break;
                }
            }

            if (bulletRemoved) {
                bulletIterator.remove();
            }
        }
    }
}
