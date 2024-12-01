package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.event.StormEvent;
import pewpew.smash.game.network.client.ClientEventManager;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.model.StormState;
import pewpew.smash.game.network.packets.StormStatePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;

public class ClientStormStatePacketProcessor extends ClientProcessor implements PacketProcessor<StormStatePacket> {

    private final ClientEventManager clientEventManager;

    public ClientStormStatePacketProcessor(EntityManager entityManager, ClientWrapper client,
            ClientEventManager clientEventManager) {
        super(entityManager, client);
        this.clientEventManager = clientEventManager;
    }

    @Override
    public void handle(Connection connection, StormStatePacket packet) {
        StormState state = packet.getState();
        StormEvent storm = this.clientEventManager.getStorm();
        if (storm != null) {
            storm.applyState(state);
        } else {
            storm = new StormEvent(state);
            this.clientEventManager.setStorm(storm);
        }
    }
}