package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.event.StormEvent;
import pewpew.smash.game.event.StormStage;
import pewpew.smash.game.network.client.ClientEventManager;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.StormEventCreationPacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class ClientStormEventCreationPacketProcessor extends ClientProcessor
        implements PacketProcessor<StormEventCreationPacket> {
    private final ClientEventManager clientEventManager;

    public ClientStormEventCreationPacketProcessor(EntityManager entityManager, ClientWrapper client,
            ClientEventManager clientEventManager) {
        super(entityManager, client);
        this.clientEventManager = clientEventManager;
    }

    @Override
    public void handle(Connection connection, StormEventCreationPacket packet) {
        StormEvent storm = new StormEvent(packet.getInitialRadius(), StormStage.PRE_INITIAL);
        storm.setCenter(packet.getCenterX(), packet.getCenterY());
        storm.transitionToStage(StormStage.PRE_INITIAL);
        clientEventManager.setStorm(storm);
    }
}
