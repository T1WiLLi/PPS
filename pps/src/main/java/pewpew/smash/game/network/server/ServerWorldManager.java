package pewpew.smash.game.network.server;

import java.util.Arrays;

import pewpew.smash.game.network.packets.WorldDataPacket;
import pewpew.smash.game.world.WorldDisplayerHelper;
import pewpew.smash.game.world.WorldGenerator;

public final class ServerWorldManager {
    private final WorldGenerator worldGenerator;
    private final byte[][] worldData;

    public ServerWorldManager() {
        this.worldGenerator = new WorldGenerator();
        this.worldData = this.worldGenerator.getWorldData();
    }

    public void displayWorld() {
        WorldDisplayerHelper.displayWorld(WorldGenerator.getWorldImage(this.worldData));
    }

    public byte[][] getWorldData() {
        return Arrays.copyOf(this.worldData, this.worldData.length);
    }

    public void sendWorldDataToClient(ServerWrapper server, int clientID) {
        server.sendToTCP(clientID, new WorldDataPacket(this.worldData));

    }
}
