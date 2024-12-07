package pewpew.smash.game.network.client;

import pewpew.smash.engine.entities.MovableEntity;
import pewpew.smash.game.entities.Bullet;
import pewpew.smash.game.network.manager.EntityManager;

public class ClientEntityUpdater {
    private final EntityManager entityManager;

    public ClientEntityUpdater(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void update() {
        updateBulletEntities();
        updateMovablesEntities();
    }

    private void updateMovablesEntities() {
        this.entityManager.getMovableEntities().forEach(MovableEntity::updateClient);
    }

    private void updateBulletEntities() {
        this.entityManager.getBulletEntities().forEach(Bullet::updateClient);
    }
}
