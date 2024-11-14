package pewpew.smash.game.network.processor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.EntityManager;

public abstract class Processor {
    private final EntityManager entityManager;

    public Processor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    protected Player getPlayer(Connection connection) {
        return this.entityManager.getPlayerEntity(connection.getID());
    }
}
