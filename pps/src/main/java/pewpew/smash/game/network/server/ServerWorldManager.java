package pewpew.smash.game.network.server;

import java.util.Arrays;
import java.util.List;

import pewpew.smash.game.world.WorldEntitiesGenerator;
import pewpew.smash.game.world.WorldGenerator;
import pewpew.smash.game.world.WorldServerIntegration;
import pewpew.smash.game.world.entities.WorldStaticEntity;
import pewpew.smash.game.objects.Item;

public final class ServerWorldManager {
    private final WorldGenerator worldGenerator;
    private final WorldEntitiesGenerator worldEntitiesGenerator;
    private final WorldServerIntegration worldServerIntegration;
    private final byte[][] worldData;
    private final long seed;

    private final List<WorldStaticEntity> entities;
    private final List<Item> items;

    public ServerWorldManager(ServerWrapper server, int amountOfEntityToBeGenerated, int numItems) {
        this.seed = WorldGenerator.generateSeed();
        this.worldGenerator = new WorldGenerator(this.seed);
        System.out.println("World generated");
        this.worldData = this.worldGenerator.getWorldData();
        this.worldEntitiesGenerator = new WorldEntitiesGenerator();
        this.worldServerIntegration = new WorldServerIntegration(server);

        entities = this.worldEntitiesGenerator.generateWorldEntities(seed, worldData,
                amountOfEntityToBeGenerated);
        System.out.println("World entities generated");
        items = this.worldEntitiesGenerator.generateItems(worldData, numItems);
        System.out.println("Items generated");
    }

    public byte[][] getWorldData() {
        return Arrays.copyOf(this.worldData, this.worldData.length);
    }

    public void sendWorldData(int id) {
        this.worldServerIntegration.sendSeed(seed);
        this.worldServerIntegration.sendWorldEntities(entities, id);
        this.worldServerIntegration.sendItem(items, id);
    }

    public List<WorldStaticEntity> getStaticEntities() {
        return entities;
    }
}
