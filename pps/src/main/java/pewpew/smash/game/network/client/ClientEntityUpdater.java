package pewpew.smash.game.network.client;

import pewpew.smash.game.network.manager.EntityManager;

public class ClientEntityUpdater {
    private final EntityManager entityManager;

    public ClientEntityUpdater(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void update() {
        updateBulletEntities();
    }

    private void updateBulletEntities() {
        this.entityManager.getBulletEntities().forEach(b -> {
            b.updateClient();
        });
    }
}
