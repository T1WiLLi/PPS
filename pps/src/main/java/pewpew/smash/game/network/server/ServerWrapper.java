package pewpew.smash.game.network.server;

import java.io.IOException;

import org.apache.http.annotation.Obsolete;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import pewpew.smash.game.network.KryoNetWrapper;

public class ServerWrapper extends KryoNetWrapper {

    public ServerWrapper(int TCP, int UDP) {
        super(TCP, UDP);
        this.endPoint = new Server(WRITE_BUFFER_SIZE, READ_BUFFER_SIZE); // 2M bytes on buffer
    }

    @Override
    public void start() throws IOException {
        this.endPoint.start();
        ((Server) this.endPoint).bind(TCP, UDP);
    }

    @Override
    public void stop() throws IOException {
        this.endPoint.stop();
        ((Server) this.endPoint).dispose();
    }

    @Override
    public void addListener(Listener listener) {
        this.endPoint.addListener(listener);
    }

    public void sendToAllTCP(Object packet) {
        ((Server) this.endPoint).sendToAllTCP(packet);
    }

    public void sendToAllUDP(Object packet) {
        ((Server) this.endPoint).sendToAllUDP(packet);
    }

    @Override
    public void sendToTCP(int connection, Object packet) {
        ((Server) this.endPoint).sendToTCP(connection, packet);
    }

    @Override
    public void sendToUDP(int connection, Object packet) {
        ((Server) this.endPoint).sendToUDP(connection, packet);
    }

    @Override
    public Server getEndPoint() {
        return (Server) this.endPoint;
    }

    @Override
    public Kryo getKryo() {
        return this.endPoint.getKryo();
    }

    @Override
    public String toString() {
        return "Server = [" + super.toString();
    }

    /**
     * This method is obsolete and should not be used.
     * Calling this method will throw an UnsupportedOperationException.
     *
     * @param packet the packet object.
     * @throws UnsupportedOperationException always thrown when called.
     */
    @Override
    @Obsolete
    protected void sendToTCP(Object packet) {
        throw new UnsupportedOperationException("Unimplemented method 'sendToTCP'");
    }

    /**
     * This method is obsolete and should not be used.
     * Calling this method will throw an UnsupportedOperationException.
     *
     * @param packet the packet object.
     * @throws UnsupportedOperationException always thrown when called.
     */
    @Override
    @Obsolete
    protected void sendToUDP(Object packet) {
        throw new UnsupportedOperationException("Unimplemented method 'sendToUDP'");
    }
}
