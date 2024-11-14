package pewpew.smash.game.network.processor.serverProcessor;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.InventoryPacket;
import pewpew.smash.game.network.packets.ReloadWeaponRequestPacket;
import pewpew.smash.game.network.packets.WeaponStatePacket;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.processor.ServerProcessor;
import pewpew.smash.game.network.serializer.InventorySerializer;
import pewpew.smash.game.network.serializer.WeaponStateSerializer;
import pewpew.smash.game.network.server.ServerWrapper;
import pewpew.smash.game.objects.RangedWeapon;

public class ReloadWeaponRequestPacketProcessor extends ServerProcessor implements PacketProcessor {

    public ReloadWeaponRequestPacketProcessor(EntityManager entityManager, ServerWrapper server) {
        super(entityManager, server);
    }

    @Override
    public void handle(Connection connection, Object packet) {
        if (packet instanceof ReloadWeaponRequestPacket) {
            Player player = getPlayer(connection);
            if (player != null && player.getInventory().getPrimaryWeapon().isPresent()) {
                RangedWeapon weapon = (RangedWeapon) player.getInventory().getPrimaryWeapon().get();
                weapon.reload();

                WeaponStatePacket weaponStatePacket = WeaponStateSerializer.serializeWeaponState(weapon);
                InventoryPacket inventoryPacket = new InventoryPacket(player.getId(),
                        InventorySerializer.serializeInventory(player.getInventory()));

                sendToTCP(connection.getID(), inventoryPacket);
                sendToTCP(connection.getID(), weaponStatePacket);
            }
        }
    }
}
