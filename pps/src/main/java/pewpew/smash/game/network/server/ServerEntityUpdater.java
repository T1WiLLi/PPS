package pewpew.smash.game.network.server;

import java.util.Iterator;

import pewpew.smash.engine.entities.StaticEntity;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.MouseActionPacket;
import pewpew.smash.game.network.packets.PositionPacket;
import pewpew.smash.game.utils.ScaleUtils;

public class ServerEntityUpdater {
    private final EntityManager entityManager;
    private final ServerCombatManager combatManager;
    private static final double FOV_BUFFER = 0.05;
    private static final int BASE_WIDTH = 800;
    private static final int BASE_HEIGHT = 600;

    public ServerEntityUpdater(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.combatManager = new ServerCombatManager(entityManager);
    }

    public void update(ServerWrapper server) {
        entityManager.getPlayerEntities().forEach(player -> player.updateServer());
        combatManager.updateCombat(server);

        ViewBounds combinedFOV = calculateCombinedPlayerFOV();

        entityManager.getMovableEntities().forEach(entity -> {
            if (isInView(entity, combinedFOV)) {
                entity.updateServer();
            }
        });

        entityManager.getUpdatableEntities().forEach(entity -> {
            if (isInView(entity, combinedFOV)) {
                entity.updateServer();
            }
        });
    }

    public void sendPlayerPositions(ServerWrapper server) {
        entityManager.getPlayerEntities().forEach(player -> {
            PositionPacket packet = new PositionPacket(player.getId(), player.getX(), player.getY(),
                    player.getRotation());
            server.sendToAllUDP(packet);
        });
    }

    public void sendPlayerMouseInput(ServerWrapper server) {
        entityManager.getPlayerEntities().forEach(player -> {
            MouseActionPacket packet = new MouseActionPacket(player.getId(), player.getMouseInput());
            server.sendToAllUDP(packet);
        });
    }

    private ViewBounds calculateCombinedPlayerFOV() {
        double scaleX = ScaleUtils.getScaleX();
        double scaleY = ScaleUtils.getScaleY();

        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        Iterator<Player> iterator = entityManager.getPlayerEntities().iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            double bufferX = BASE_WIDTH * FOV_BUFFER;
            double bufferY = BASE_HEIGHT * FOV_BUFFER;

            double playerMinX = player.getX() / scaleX - bufferX;
            double playerMaxX = player.getX() / scaleX + BASE_WIDTH + bufferX;
            double playerMinY = player.getY() / scaleY - bufferY;
            double playerMaxY = player.getY() / scaleY + BASE_HEIGHT + bufferY;

            minX = Math.min(minX, playerMinX);
            maxX = Math.max(maxX, playerMaxX);
            minY = Math.min(minY, playerMinY);
            maxY = Math.max(maxY, playerMaxY);
        }

        return new ViewBounds(minX, maxX, minY, maxY);
    }

    private boolean isInView(StaticEntity entity, ViewBounds bounds) {
        double scaleX = ScaleUtils.getScaleX();
        double scaleY = ScaleUtils.getScaleY();

        double scaledX = entity.getX() / scaleX;
        double scaledY = entity.getY() / scaleY;

        return scaledX >= bounds.minX && scaledX <= bounds.maxX &&
                scaledY >= bounds.minY && scaledY <= bounds.maxY;
    }

    private record ViewBounds(double minX, double maxX, double minY, double maxY) {
    }
}
