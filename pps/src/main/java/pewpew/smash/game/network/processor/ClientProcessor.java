package pewpew.smash.game.network.processor;

import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;

public class ClientProcessor extends Processor {
    protected final ClientWrapper client;

    public ClientProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager);
        this.client = client;
    }

    protected void sendToUDP(Object packet) {
        this.client.sendToUDP(packet);
    }

    protected void sendToTCP(Object packet) {
        this.client.sendToTCP(packet);
    }
}
