package pewpew.smash.game.network;

import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.kryo.Kryo;

import pewpew.smash.engine.controls.Direction;
import pewpew.smash.engine.controls.MouseInput;

import pewpew.smash.game.event.StormStage;
import pewpew.smash.game.objects.WeaponType;
import pewpew.smash.game.world.entities.WorldEntityType;

import pewpew.smash.game.network.model.PlayerState;
import pewpew.smash.game.network.model.SerializedItem;
import pewpew.smash.game.network.model.SerializedWorldStaticEntity;
import pewpew.smash.game.network.model.StormState;
import pewpew.smash.game.network.model.WorldEntityState;

import pewpew.smash.game.network.packets.BasePacket;
import pewpew.smash.game.network.packets.BroadcastMessagePacket;
import pewpew.smash.game.network.packets.BulletCreatePacket;
import pewpew.smash.game.network.packets.BulletRemovePacket;
import pewpew.smash.game.network.packets.DirectionPacket;
import pewpew.smash.game.network.packets.InventoryPacket;
import pewpew.smash.game.network.packets.ItemAddPacket;
import pewpew.smash.game.network.packets.ItemRemovePacket;
import pewpew.smash.game.network.packets.MouseActionPacket;
import pewpew.smash.game.network.packets.MouseInputPacket;
import pewpew.smash.game.network.packets.PickupItemRequestPacket;
import pewpew.smash.game.network.packets.PlayerDeathPacket;
import pewpew.smash.game.network.packets.PlayerInWaterWarningPacket;
import pewpew.smash.game.network.packets.PlayerJoinedPacket;
import pewpew.smash.game.network.packets.PlayerLeftPacket;
import pewpew.smash.game.network.packets.PlayerOutOfWaterPacket;
import pewpew.smash.game.network.packets.PlayerStatePacket;
import pewpew.smash.game.network.packets.PlayerUsernamePacket;
import pewpew.smash.game.network.packets.PositionPacket;
import pewpew.smash.game.network.packets.PreventActionForPlayerPacket;
import pewpew.smash.game.network.packets.ReloadWeaponRequestPacket;
import pewpew.smash.game.network.packets.StormEventCreationPacket;
import pewpew.smash.game.network.packets.StormStatePacket;
import pewpew.smash.game.network.packets.SyncTimePacket;
import pewpew.smash.game.network.packets.UseConsumableRequestPacket;
import pewpew.smash.game.network.packets.WeaponStatePacket;
import pewpew.smash.game.network.packets.WeaponSwitchRequestPacket;
import pewpew.smash.game.network.packets.WorldDataPacket;
import pewpew.smash.game.network.packets.WorldEntityAddPacket;
import pewpew.smash.game.network.packets.WorldEntityRemovePacket;
import pewpew.smash.game.network.packets.WorldEntityStatePacket;

public class KryoRegister {

    public void register(Kryo kryo) {
        registerStructures(kryo);
        registerObjects(kryo);
        registerPacket(kryo);
    }

    private void registerPacket(Kryo kryo) {
        kryo.register(BasePacket.class);
        kryo.register(BroadcastMessagePacket.class);
        kryo.register(DirectionPacket.class);
        kryo.register(MouseInputPacket.class);
        kryo.register(MouseActionPacket.class);
        kryo.register(PlayerJoinedPacket.class);
        kryo.register(PlayerLeftPacket.class);
        kryo.register(PlayerUsernamePacket.class);
        kryo.register(PositionPacket.class);
        kryo.register(WorldDataPacket.class);
        kryo.register(PlayerStatePacket.class);
        kryo.register(WorldEntityStatePacket.class);
        kryo.register(WeaponStatePacket.class);
        kryo.register(WeaponSwitchRequestPacket.class);
        kryo.register(InventoryPacket.class);
        kryo.register(PlayerDeathPacket.class);
        kryo.register(BulletCreatePacket.class);
        kryo.register(BulletRemovePacket.class);
        kryo.register(ReloadWeaponRequestPacket.class);
        kryo.register(ItemAddPacket.class);
        kryo.register(ItemRemovePacket.class);
        kryo.register(PickupItemRequestPacket.class);
        kryo.register(UseConsumableRequestPacket.class);
        kryo.register(PreventActionForPlayerPacket.class);
        kryo.register(WorldEntityAddPacket.class);
        kryo.register(WorldEntityRemovePacket.class);
        kryo.register(PlayerInWaterWarningPacket.class);
        kryo.register(PlayerOutOfWaterPacket.class);
        kryo.register(StormStatePacket.class);
        kryo.register(StormEventCreationPacket.class);
        kryo.register(SyncTimePacket.class);
    }

    private void registerObjects(Kryo kryo) {
        kryo.register(Direction.class);
        kryo.register(MouseInput.class);
        kryo.register(PlayerState.class);
        kryo.register(WorldEntityState.class);
        kryo.register(SerializedItem.ItemType.class);
        kryo.register(SerializedItem.class);
        kryo.register(WeaponType.class);
        kryo.register(SerializedWorldStaticEntity.class);
        kryo.register(WorldEntityType.class);
        kryo.register(StormStage.class);
        kryo.register(StormState.class);
    }

    private void registerStructures(Kryo kryo) {
        kryo.register(byte[].class);
        kryo.register(byte[][].class);
        kryo.register(int[].class);
        kryo.register(int[][].class);
        kryo.register(String.class);
        kryo.register(Integer.class);
        kryo.register(Map.class);
        kryo.register(HashMap.class);
    }
}
