package pewpew.smash.game.event;

import pewpew.smash.engine.entities.StaticEntity;
import pewpew.smash.game.entities.Plane;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.CrateDropPacket;
import pewpew.smash.game.network.packets.PlaneStatePacket;
import pewpew.smash.game.network.serializer.WorldStaticEntitySerializer;
import pewpew.smash.game.network.server.ServerTime;
import pewpew.smash.game.network.server.ServerWrapper;
import pewpew.smash.game.world.WorldGenerator;
import pewpew.smash.game.world.entities.Crate;

import java.awt.Shape;
import java.util.Timer;
import java.util.TimerTask;

public class AirdropManager {

    private static final long FIRST_EVENT_DELAY = 180_000L; // 3 minutes
    private static final long MIN_TIME_BETWEEN_EVENTS = 30_000L; // 30 seconds
    private static final double EVENT_TRIGGER_CHANCE = 0.5;

    private boolean firstEventTriggered = false;
    private long lastAirdropTime = 0;
    private boolean isActive;
    private AirdropEvent currentAirdrop;

    public void update(ServerWrapper server, EntityManager entityManager, byte[][] world) {
        long currentTime = ServerTime.getInstance().getElapsedTimeMillis();

        if (!isActive && canTriggerEvent(currentTime)) {
            triggerEvent(server, currentTime);
        }

        if (isActive && currentAirdrop != null) {
            updateEvent(server, entityManager, world);
        }
    }

    private boolean canTriggerEvent(long currentTime) {
        if (!firstEventTriggered) {
            if (currentTime < FIRST_EVENT_DELAY) {
                return false;
            }
            if (Math.random() < EVENT_TRIGGER_CHANCE) {
                return true;
            }
            return false;
        } else {
            if ((currentTime - lastAirdropTime) < MIN_TIME_BETWEEN_EVENTS) {
                return false;
            }
            return Math.random() < EVENT_TRIGGER_CHANCE;
        }
    }

    private void triggerEvent(ServerWrapper server, long currentTime) {
        lastAirdropTime = currentTime;
        currentAirdrop = new AirdropEvent();
        isActive = true;
        if (!firstEventTriggered) {
            firstEventTriggered = true;
        }

        Plane plane = currentAirdrop.getPlane();
        PlaneStatePacket planePacket = new PlaneStatePacket(plane.getX(), plane.getY(), plane.getDirection(),
                plane.getRotation());
        server.sendToAllTCP(planePacket);
    }

    private void updateEvent(ServerWrapper server, EntityManager entityManager, byte[][] world) {
        Plane plane = currentAirdrop.getPlane();
        plane.updateServer();

        if (!currentAirdrop.isCrateDropped() && shouldDropCrate(plane, entityManager, world)) {
            dropCrate(server, entityManager, world);
        }

        if (currentAirdrop.isCrateDropped()) {
            isActive = false;
            currentAirdrop = null;
        }
    }

    private boolean shouldDropCrate(Plane plane, EntityManager entityManager, byte[][] world) {
        if (currentAirdrop == null)
            return false;

        int planeCenterX = plane.getX() + plane.getWidth() / 2;
        int planeCenterY = plane.getY() + plane.getHeight() / 2;

        int targetX = currentAirdrop.getDropX();
        int targetY = currentAirdrop.getDropY();

        int threshold = 50;
        if (Math.abs(planeCenterX - targetX) > threshold || Math.abs(planeCenterY - targetY) > threshold) {
            return false;
        }

        int planeTileX = planeCenterX / WorldGenerator.TILE_SIZE;
        int planeTileY = planeCenterY / WorldGenerator.TILE_SIZE;
        if (planeTileX < 0 || planeTileX >= world.length || planeTileY < 0 || planeTileY >= world[0].length) {
            return false;
        }

        boolean aboveGrass = world[planeTileX][planeTileY] == WorldGenerator.GRASS;
        if (!aboveGrass)
            return false;

        if (isCollidingWithStaticEntities(planeCenterX, planeCenterY, entityManager)) {
            return false;
        }
        return true;
    }

    private void dropCrate(ServerWrapper server, EntityManager entityManager, byte[][] world) {
        int dropX = currentAirdrop.getDropX();
        int dropY = currentAirdrop.getDropY();

        Crate crate = currentAirdrop.createCrate(dropX, dropY);
        crate.setId(entityManager.getNextID(StaticEntity.class));
        CrateDropPacket dropPacket = new CrateDropPacket(
                WorldStaticEntitySerializer.serialize(crate));
        server.sendToAllTCP(dropPacket);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                entityManager.addStaticEntity(crate.getId(), crate);
            }
        }, 2000);
    }

    private boolean isCollidingWithStaticEntities(int x, int y, EntityManager entityManager) {
        for (StaticEntity entity : entityManager.getStaticEntities()) {
            Shape hitbox = entity.getHitbox();
            if (hitbox != null && hitbox.intersects(x, y, 92, 92)) {
                return true;
            }
        }
        return false;
    }
}
