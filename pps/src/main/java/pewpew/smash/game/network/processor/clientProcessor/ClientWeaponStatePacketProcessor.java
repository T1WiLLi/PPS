package pewpew.smash.game.network.processor.clientProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.client.ClientWrapper;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.WeaponStatePacket;
import pewpew.smash.game.network.processor.ClientProcessor;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.serializer.WeaponStateSerializer;

public class ClientWeaponStatePacketProcessor extends ClientProcessor implements PacketProcessor<WeaponStatePacket> {

    public ClientWeaponStatePacketProcessor(EntityManager entityManager, ClientWrapper client) {
        super(entityManager, client);
    }

    @Override
    public void handle(Connection connection, WeaponStatePacket packet) {
        Player player = getEntityManager().getPlayerEntity(packet.getOwnerID());
        if (player != null) {
            WeaponStateSerializer.deserializeWeaponState(packet, player);
        }
    }
}
