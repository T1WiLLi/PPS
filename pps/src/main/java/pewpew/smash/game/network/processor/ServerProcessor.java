package pewpew.smash.game.network.processor;

import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.server.ServerWrapper;

public abstract class ServerProcessor extends Processor {
    protected final ServerWrapper server;

    public ServerProcessor(EntityManager entityManager, ServerWrapper server) {
        super(entityManager);
        this.server = server;
    }

    protected void sendToAllUDP(Object packet) {
        this.server.sendToAllUDP(packet);
    }

    protected void sendToAllTCP(Object packet) {
        this.server.sendToAllTCP(packet);
    }

    protected void sendToUDP(int id, Object packet) {
        this.server.sendToUDP(id, packet);
    }

    protected void sendToTCP(int id, Object packet) {
        this.server.sendToTCP(id, packet);
    }
}
