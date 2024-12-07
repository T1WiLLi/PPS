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

    private static final int MIN_TIME_BETWEEN_EVENTS = 30_000;
    private static final int MAX_TIME_BEFORE_EVENT = 120_000;
    private static final double DELAY_FACTOR = 0.005;

    private long lastAirdropTime = 0;
    private boolean isActive;
    private AirdropEvent currentAirdrop;
    private long dropStartTime = 0;

    public void update(ServerWrapper server, EntityManager entityManager, byte[][] world) {
        long currentTime = ServerTime.getInstance().getElapsedTimeMillis();

        if (!isActive && canTriggerEvent(currentTime)) {
            System.out.println("Event trigger!");
            triggerEvent(server, currentTime);
        }

        if (isActive && currentAirdrop != null) {
            updateEvent(server, entityManager, world, currentTime);
        }
    }

    private boolean canTriggerEvent(long currentTime) {
        if ((currentTime - lastAirdropTime) < MIN_TIME_BETWEEN_EVENTS) {
            return false;
        }

        if (currentTime < MAX_TIME_BEFORE_EVENT) {
            return false;
        }

        if ((currentTime - lastAirdropTime) >= MIN_TIME_BETWEEN_EVENTS) {
            lastAirdropTime = currentTime;
            return Math.random() < 0.25;
        }

        return false;
    }

    private void triggerEvent(ServerWrapper server, long currentTime) {
        lastAirdropTime = currentTime;
        currentAirdrop = new AirdropEvent();
        isActive = true;

        Plane plane = currentAirdrop.getPlane();
        PlaneStatePacket planePacket = new PlaneStatePacket(plane.getX(), plane.getY(), plane.getDirection(),
                plane.getRotation());
        server.sendToAllTCP(planePacket);
        dropStartTime = currentTime;
    }

    private void updateEvent(ServerWrapper server, EntityManager entityManager, byte[][] world, long currentTime) {
        Plane plane = currentAirdrop.getPlane();
        plane.updateServer();

        if (!currentAirdrop.isCrateDropped() && shouldDropCrate(plane, entityManager, world, currentTime)) {
            dropCrate(server, entityManager, world);
        }

        if (currentAirdrop.isCrateDropped()) {
            isActive = false;
            currentAirdrop = null;
        }
    }

    private boolean shouldDropCrate(Plane plane, EntityManager entityManager, byte[][] world, long currentTime) {
        int worldWidth = WorldGenerator.getWorldWidth();
        int worldHeight = WorldGenerator.getWorldHeight();

        int planeX = plane.getX();
        int planeY = plane.getY();
        double xProgress = (double) planeX / worldWidth;
        double yProgress = (double) planeY / worldHeight;

        if (xProgress < 0.2 || xProgress > 0.8 || yProgress < 0.2 || yProgress > 0.8) {
            return false;
        }

        long delay = (long) ((worldWidth + worldHeight) * DELAY_FACTOR);
        if ((currentTime - dropStartTime) < delay) {
            return false;
        }

        int mapMarginX = worldWidth / 10;
        int mapMarginY = worldHeight / 10;
        boolean withinBounds = planeX > mapMarginX && planeX < (worldWidth - mapMarginX)
                && planeY > mapMarginY && planeY < (worldHeight - mapMarginY);

        int planeTileX = planeX / WorldGenerator.TILE_SIZE;
        int planeTileY = planeY / WorldGenerator.TILE_SIZE;
        boolean aboveGrass = (planeTileX >= 0 && planeTileX < world.length) &&
                (planeTileY >= 0 && planeTileY < world[0].length) &&
                world[planeTileX][planeTileY] == WorldGenerator.GRASS;

        boolean noCollision = !isCollidingWithStaticEntities(planeX, planeY, entityManager);

        return withinBounds && aboveGrass && noCollision;
    }

    private void dropCrate(ServerWrapper server, EntityManager entityManager, byte[][] world) {
        int[] cratePosition = getAirdropPosition(currentAirdrop.getPlane());
        int dropX = cratePosition[0];
        int dropY = cratePosition[1];

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

    private int[] getAirdropPosition(Plane plane) {
        int centerX = plane.getX() + plane.getWidth() / 2;
        int centerY = plane.getY() + plane.getHeight() / 2;

        switch (plane.getDirection()) {
            case UP_LEFT -> {
                centerX -= plane.getWidth();
                centerY -= plane.getHeight() - 200;
            }
            default -> {
                // do nothing
            }
        }

        System.out.printf(
                "Adjusted Airdrop Position: CenterX=%d, CenterY=%d (Plane: X=%d, Y=%d, Direction=%s, Width=%d, Height=%d)\n",
                centerX, centerY, plane.getX(), plane.getY(), plane.getDirection(), plane.getWidth(),
                plane.getHeight());

        return new int[] { centerX, centerY };
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
