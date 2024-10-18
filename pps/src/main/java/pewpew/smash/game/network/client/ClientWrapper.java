package pewpew.smash.game.network.client;

import java.io.IOException;

import org.apache.http.annotation.Obsolete;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import pewpew.smash.game.network.KryoNetWrapper;

public class ClientWrapper extends KryoNetWrapper {
    private final int TIMEOUT = 5000;
    private final String HOST;

    public ClientWrapper(String host, int tcp, int udp) {
        super(tcp, udp);
        this.endPoint = new Client(WRITE_BUFFER_SIZE, READ_BUFFER_SIZE);
        this.HOST = host;
    }

    public void start() throws IOException {
        this.endPoint.start();
        ((Client) this.endPoint).connect(this.TIMEOUT, this.HOST, this.TCP, this.UDP);
    }

    public void stop() throws IOException {
        this.endPoint.stop();
        ((Client) this.endPoint).dispose();
    }

    public void addListener(Listener listener) {
        this.endPoint.addListener(listener);
    }

    public void sendToTCP(Object packet) {
        ((Connection) this.endPoint).sendTCP(packet);
    }

    public void sendToUDP(Object packet) {
        ((Connection) this.endPoint).sendUDP(packet);
    }

    public Client getEndPoint() {
        return (Client) this.endPoint;
    }

    public Kryo getKryo() {
        return this.endPoint.getKryo();
    }

    public boolean isConnected() {
        return ((Connection) this.endPoint).isConnected();
    }

    @Override
    public String toString() {
        return "Client = [HOST=" + HOST + "," + super.toString();

    }

    /**
     * This method is obsolete and should not be used.
     * Calling this method will throw an UnsupportedOperationException.
     *
     * @param connection the connection identifier.
     * @param packet     the packet object.
     * @throws UnsupportedOperationException always thrown when called.
     */
    @Override
    @Obsolete
    protected void sendToTCP(int connection, Object packet) {
        throw new UnsupportedOperationException("Unimplemented method 'sendToTCP'");
    }

    /**
     * This method is obsolete and should not be used.
     * Calling this method will throw an UnsupportedOperationException.
     *
     * @param connection the connection identifier.
     * @param packet     the packet object.
     * @throws UnsupportedOperationException always thrown when called.
     */
    @Override
    @Obsolete
    protected void sendToUDP(int connection, Object packet) {
        throw new UnsupportedOperationException("Unimplemented method 'sendToUDP'");
    }

}