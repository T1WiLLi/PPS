package pewpew.smash.game.network;

import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public abstract class Handler {
    protected abstract void start() throws IOException;

    protected abstract void handlePacket(Connection connection, Object object);

    protected abstract void onConnect(Connection connection);

    protected abstract void onDisconnect(Connection connection);

    protected abstract void stop();

    protected void registersClasses(Kryo kryo) {
        new KryoRegister().register(kryo);
    }

    protected Listener bindListener() {
        return new Listener() {

            @Override
            public void connected(Connection connection) {
                onConnect(connection);
            }

            @Override
            public void disconnected(Connection connection) {
                onDisconnect(connection);
            }

            @Override
            public void received(Connection connection, Object object) {
                handlePacket(connection, object);
            }
        };
    }
}
