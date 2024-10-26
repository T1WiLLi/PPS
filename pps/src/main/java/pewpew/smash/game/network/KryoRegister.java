package pewpew.smash.game.network;

import java.util.Map;
import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;

import pewpew.smash.engine.controls.Direction;
import pewpew.smash.engine.controls.MouseInput;
import pewpew.smash.game.network.model.PlayerState;
import pewpew.smash.game.network.model.SerializedItem;
import pewpew.smash.game.network.packets.BasePacket;
import pewpew.smash.game.network.packets.BroadcastMessagePacket;
import pewpew.smash.game.network.packets.DirectionPacket;
import pewpew.smash.game.network.packets.InventoryPacket;
import pewpew.smash.game.network.packets.MouseInputPacket;
import pewpew.smash.game.network.packets.PlayerJoinedPacket;
import pewpew.smash.game.network.packets.PlayerLeftPacket;
import pewpew.smash.game.network.packets.PlayerStatePacket;
import pewpew.smash.game.network.packets.PlayerUsernamePacket;
import pewpew.smash.game.network.packets.PositionPacket;
import pewpew.smash.game.network.packets.WeaponStatePacket;
import pewpew.smash.game.network.packets.WorldDataPacket;
import pewpew.smash.game.objects.WeaponType;

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
        kryo.register(PlayerJoinedPacket.class);
        kryo.register(PlayerLeftPacket.class);
        kryo.register(PlayerUsernamePacket.class);
        kryo.register(PositionPacket.class);
        kryo.register(WorldDataPacket.class);
        kryo.register(PlayerStatePacket.class);
        kryo.register(WeaponStatePacket.class);
        kryo.register(InventoryPacket.class);
    }

    private void registerObjects(Kryo kryo) {
        kryo.register(Direction.class);
        kryo.register(MouseInput.class);
        kryo.register(PlayerState.class);
        kryo.register(SerializedItem.ItemType.class);
        kryo.register(SerializedItem.class);
        kryo.register(WeaponType.class);
    }

    private void registerStructures(Kryo kryo) {
        kryo.register(byte[].class);
        kryo.register(byte[][].class);
        kryo.register(int[].class);
        kryo.register(int[][].class);
        kryo.register(Object.class);
        kryo.register(Integer.class);
        kryo.register(Map.class);
        kryo.register(HashMap.class);
    }
}
