package pewpew.smash.game.network;

import com.esotericsoftware.kryo.Kryo;

import pewpew.smash.engine.controls.Direction;
import pewpew.smash.game.network.packets.BasePacket;
import pewpew.smash.game.network.packets.BroadcastMessagePacket;
import pewpew.smash.game.network.packets.ClientIDResponsePacket;
import pewpew.smash.game.network.packets.DirectionPacket;
import pewpew.smash.game.network.packets.PlayerJoinedPacket;
import pewpew.smash.game.network.packets.PlayerLeftPacket;
import pewpew.smash.game.network.packets.PositionPacket;
import pewpew.smash.game.network.packets.WorldDataPacket;

public class KryoRegister {

    public void register(Kryo kryo) {
        registerStructures(kryo);
        registerObjects(kryo);
        registerPacket(kryo);
    }

    private void registerPacket(Kryo kryo) {
        kryo.register(BasePacket.class);
        kryo.register(BroadcastMessagePacket.class);
        kryo.register(ClientIDResponsePacket.class);
        kryo.register(DirectionPacket.class);
        kryo.register(PlayerJoinedPacket.class);
        kryo.register(PlayerLeftPacket.class);
        kryo.register(PositionPacket.class);
        kryo.register(WorldDataPacket.class);
    }

    private void registerObjects(Kryo kryo) {
        kryo.register(Direction.class);
    }

    private void registerStructures(Kryo kryo) {
        kryo.register(byte[].class);
        kryo.register(byte[][].class);
        kryo.register(int[].class);
        kryo.register(int[][].class);
    }
}
