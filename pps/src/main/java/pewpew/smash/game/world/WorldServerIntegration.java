package pewpew.smash.game.world;

import java.util.List;

import pewpew.smash.game.network.packets.ItemAddPacket;
import pewpew.smash.game.network.packets.WorldDataPacket;
import pewpew.smash.game.network.packets.WorldEntityAddPacket;
import pewpew.smash.game.network.serializer.SerializationUtility;
import pewpew.smash.game.network.serializer.WorldStaticEntitySerializer;
import pewpew.smash.game.network.server.ServerWrapper;
import pewpew.smash.game.objects.Item;
import pewpew.smash.game.world.entities.WorldStaticEntity;

// A class to manages interaction with the server and send useful information to the clients
public class WorldServerIntegration {
    private final ServerWrapper server;

    public WorldServerIntegration(ServerWrapper server) {
        this.server = server;
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
}
