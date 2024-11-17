package pewpew.smash.game.network.server;

import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.MouseActionPacket;
import pewpew.smash.game.network.packets.PositionPacket;

public class ServerEntityUpdater {
    private final EntityManager entityManager;
    private final ServerCombatManager combatManager;

    public ServerEntityUpdater(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.combatManager = new ServerCombatManager(entityManager);
    }

    public void update(ServerWrapper server) {
        entityManager.getPlayerEntities().forEach(player -> player.updateServer());
        entityManager.getMovableEntities().forEach(entity -> entity.updateServer());
        combatManager.updateCombat(server);
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
}
