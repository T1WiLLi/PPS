package pewpew.smash.game.network.server;

import java.util.Arrays;

import pewpew.smash.game.network.manager.ItemManager;
import pewpew.smash.game.network.model.SerializedItem;
import pewpew.smash.game.network.packets.ItemAddPacket;
import pewpew.smash.game.network.packets.WorldDataPacket;
import pewpew.smash.game.network.serializer.SerializationUtility;
import pewpew.smash.game.world.WorldDisplayerHelper;
import pewpew.smash.game.world.WorldGenerator;

public final class ServerWorldManager {
    private final WorldGenerator worldGenerator;
    private final byte[][] worldData;
    private final long seed;

    public ServerWorldManager() {
        this.seed = WorldGenerator.generateSeed();
        this.worldGenerator = new WorldGenerator(this.seed);
        this.worldData = this.worldGenerator.getWorldData();
    }

    public void displayWorld() {
        WorldDisplayerHelper.displayWorld(WorldGenerator.getWorldImage(this.worldData));
    }

    public byte[][] getWorldData() {
        return Arrays.copyOf(this.worldData, this.worldData.length);
    }

    public void sendWorldDataToClient(ServerWrapper server, int clientID) {
        server.sendToTCP(clientID, new WorldDataPacket(this.seed));
        ItemManager.getInstance(true).getItems().forEach(i -> {
            SerializedItem serializedItem = SerializationUtility.serializeItem(i);
            server.sendToTCP(clientID, new ItemAddPacket(i.getX(), i.getY(), serializedItem));
        });
    }
}
