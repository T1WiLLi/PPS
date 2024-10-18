package pewpew.smash.game.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

public abstract class KryoNetWrapper {
    public static final int WRITE_BUFFER_SIZE = 8192 * 1024; // 8,388,608 bytes or 8.38MB MAX BUFFER SIZE
    public static final int READ_BUFFER_SIZE = 8192 * 1024;

    protected EndPoint endPoint;
    protected final int TCP;
    protected final int UDP;

    protected abstract void start() throws IOException;

    protected abstract void stop() throws IOException;

    protected abstract void addListener(Listener listener);

    protected abstract void sendToTCP(int connection, Object packet);

    protected abstract void sendToTCP(Object packet);

    protected abstract void sendToUDP(int connection, Object packet);

    protected abstract void sendToUDP(Object packet);

    protected abstract EndPoint getEndPoint();

    protected abstract Kryo getKryo();

    public KryoNetWrapper(final int TCP, final int UDP) {
        this.TCP = TCP;
        this.UDP = UDP;
    }

    @Override
    public String toString() {
        return "TCP port=" + TCP + ", UDP port=" + UDP + "]";
    }
}
