package pewpew.smash.game.world;

import java.util.List;
import java.util.Map;

import pewpew.smash.game.network.packets.ItemAddPacket;
import pewpew.smash.game.network.packets.WorldDataPacket;
import pewpew.smash.game.network.packets.WorldEntityAddPacket;
import pewpew.smash.game.network.packets.WorldEntityStatePacket;
import pewpew.smash.game.network.serializer.SerializationUtility;
import pewpew.smash.game.network.serializer.WorldStaticEntitySerializer;
import pewpew.smash.game.network.server.ServerWrapper;
import pewpew.smash.game.objects.Item;
import pewpew.smash.game.network.model.WorldEntityState;
import pewpew.smash.game.world.entities.WorldStaticEntity;

public class WorldServerIntegration {
    private final ServerWrapper server;
    private final Map<Integer, WorldEntityState> entityStates;

    public WorldServerIntegration(ServerWrapper server, Map<Integer, WorldEntityState> entityStates) {
        this.server = server;
        this.entityStates = entityStates;
    }

    public void sendSeed(long seed) {
        server.sendToAllTCP(new WorldDataPacket(seed));
    }

    public void sendWorldEntities(List<WorldStaticEntity> entities, int id) {
        entities.forEach(e -> {
            WorldEntityAddPacket packet = new WorldEntityAddPacket(WorldStaticEntitySerializer.serialize(e));
            this.server.sendToTCP(id, packet);
        });
    }

    public void sendItem(List<Item> items, int id) {
        items.forEach(i -> {
            ItemAddPacket packet = new ItemAddPacket(i.getX(), i.getY(), SerializationUtility.serializeItem(i));
            this.server.sendToTCP(id, packet);
        });
    }

    public void sendEntityStates(int id) {
        entityStates.values().forEach(state -> {
            WorldEntityStatePacket packet = new WorldEntityStatePacket(state);
            this.server.sendToTCP(id, packet);
        });
    }

    public void sendWorldData(int id, long seed, List<WorldStaticEntity> entities, List<Item> items) {
        sendSeed(seed);
        sendWorldEntities(entities, id);
        sendItem(items, id);
        sendEntityStates(id);
    }

    public void updateEntityState(WorldEntityState state) {
        entityStates.put(state.getId(), state);
        server.sendToAllTCP(new WorldEntityStatePacket(state));
    }
}
