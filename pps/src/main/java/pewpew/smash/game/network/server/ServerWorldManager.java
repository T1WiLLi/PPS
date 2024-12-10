package pewpew.smash.game.network.server;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.manager.ItemManager;
import pewpew.smash.game.network.model.WorldEntityState;
import pewpew.smash.game.world.WorldEntitiesGenerator;
import pewpew.smash.game.world.WorldGenerator;
import pewpew.smash.game.world.WorldServerIntegration;

public final class ServerWorldManager {
    private final WorldGenerator worldGenerator;
    private final WorldEntitiesGenerator worldEntitiesGenerator;
    private final WorldServerIntegration worldServerIntegration;
    private final EntityManager entityManager;
    private final Map<Integer, WorldEntityState> entityStates;
    private final byte[][] worldData;
    private final long seed;

    public ServerWorldManager(ServerWrapper server, EntityManager entityManager, int amountOfEntityToBeGenerated,
            int numItems) {
        this.entityManager = entityManager;
        this.seed = WorldGenerator.generateSeed();
        this.entityStates = new ConcurrentHashMap<>();
        this.worldGenerator = new WorldGenerator(this.seed);
        this.worldData = this.worldGenerator.getWorldData();
        this.worldEntitiesGenerator = new WorldEntitiesGenerator();
        this.worldServerIntegration = new WorldServerIntegration(server, entityStates);

        entityManager.addWorldStaticEntity(this.worldEntitiesGenerator.generateWorldEntities(seed, worldData,
                amountOfEntityToBeGenerated));
        this.worldEntitiesGenerator.generateItems(worldData, numItems);
    }

    public byte[][] getWorldData() {
        return Arrays.copyOf(this.worldData, this.worldData.length);
    }

    public void sendWorldData(int id) {
        worldServerIntegration.sendWorldData(id, seed, entityManager.getWorldStaticEntities(),
                ItemManager.getInstance(true).getItems());
    }
}
