package pewpew.smash.game.network.server;

import pewpew.smash.engine.entities.MovableEntity;
import pewpew.smash.engine.entities.StaticEntity;
import pewpew.smash.game.audio.AudioClip;
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
import pewpew.smash.game.world.entities.WorldEntityType;
import pewpew.smash.game.world.entities.WorldStaticEntity;

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

    private final ServerWrapper server;
    private final EntityManager entityManager;

    private final Map<Integer, Long> waterTimers = new HashMap<>();
    private final Map<Integer, Long> lastDamageTime = new HashMap<>();
    private final Set<Integer> playersInWater = new HashSet<>();

    private final byte[][] worldData;

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
        if (entity instanceof Player && !(other instanceof MovableEntity)) {
            Player player = (Player) entity;

            if (isPlayerStuck(player, other)) {
                int[] freePosition = findNearestFreePosition(player);
                if (freePosition != null) {
                    player.teleport(freePosition[0], freePosition[1]);
                }
            } else {
                resolveCollision(player, other);
            }

        } else if (entity instanceof MovableEntity && other instanceof MovableEntity) {
            resolveMovableCollision((MovableEntity) entity, (MovableEntity) other);
        }
    }

    private boolean isPlayerStuck(Player player, StaticEntity other) {
        return player.isColliding(other);
    }

    private int[] findNearestFreePosition(Player player) {
        int startX = player.getX() + player.getWidth() / 2;
        int startY = player.getY() + player.getHeight() / 2;
        int searchRadius = 10;
        int step = WorldGenerator.TILE_SIZE;

        for (int radius = step; radius <= searchRadius * step; radius += step) {
            for (int dx = -radius; dx <= radius; dx += step) {
                for (int dy = -radius; dy <= radius; dy += step) {
                    int checkX = startX + dx;
                    int checkY = startY + dy;

                    if (isWithinBounds(checkX, checkY) && isFreePosition(checkX, checkY, player)) {
                        return new int[] { checkX, checkY };
                    }
                }
            }
        }
        return null;
    }

    private boolean isFreePosition(int x, int y, Player player) {
        for (StaticEntity entity : entityManager.getStaticEntities()) {
            if (entity != player && entity.getHitbox() != null
                    && entity.getHitbox().intersects(x, y, player.getWidth(), player.getHeight())) {
                return false;
            }
        }
        return true;
    }

    private boolean isWithinBounds(int x, int y) {
        return x >= WORLD_MIN_X && x + WorldGenerator.TILE_SIZE <= WORLD_MAX_X &&
                y >= WORLD_MIN_Y && y + WorldGenerator.TILE_SIZE <= WORLD_MAX_Y;
    }

    private void resolveCollision(Player player, StaticEntity other) {
        double prevX = player.getPrevX();
        double prevY = player.getPrevY();
        double currX = player.getX();
        double currY = player.getY();

        double deltaX = currX - prevX;
        double deltaY = currY - prevY;

        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            player.teleport((int) prevX, (int) currY);
        } else {
            player.teleport((int) currX, (int) prevY);
        }
    }

    private void resolveMovableCollision(MovableEntity entity1, MovableEntity entity2) {
        int pushFactor = 2;
        int dx = entity1.getX() - entity2.getX();
        int dy = entity1.getY() - entity2.getY();

        if (dx == 0 && dy == 0) {
            return;
        }

        int distance = (int) Math.sqrt(dx * dx + dy * dy);
        if (distance != 0) {
            dx = (dx * pushFactor) / distance;
            dy = (dy * pushFactor) / distance;

            entity1.teleport(entity1.getX() + dx, entity1.getY() + dy);
            entity2.teleport(entity2.getX() - dx, entity2.getY() - dy);
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

                AudioClip sound = null;
                if (bullet.isCollidingWith(staticEntity)) {
                    if (staticEntity instanceof WorldStaticEntity entity) {
                        if (entity.getType() == WorldEntityType.TREE || entity.getType() == WorldEntityType.TREE_DEAD) {
                            sound = AudioClip.BULLET_EXPLODE_02;
                        } else {
                            sound = AudioClip.BULLET_EXPLODE;
                        }
                    }
                    ServerBulletTracker.getInstance().removeBullet(bullet, sound);
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
