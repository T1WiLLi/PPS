package pewpew.smash.game.network;

import java.util.concurrent.atomic.AtomicBoolean;

import java.io.IOException;

import pewpew.smash.game.network.client.ClientHandler;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.server.ServerHandler;

public class NetworkManager {
    private ClientHandler client;
    private ServerHandler server;
    private final AtomicBoolean isHost;
    private static final String LOCALHOST = "127.0.0.1";

    public NetworkManager() {
        this.isHost = new AtomicBoolean(false);
    }

    public void initialize(String host, int port, boolean isHosting) throws IOException {
        this.isHost.set(isHosting);

        if (isHosting) {
            server = new ServerHandler(port);
            server.start();

            client = new ClientHandler(LOCALHOST, port);
        } else {
            client = new ClientHandler(host, port);
        }
        client.start();
    }

    public EntityManager getEntityManager() {
        if (client == null) {
            throw new IllegalStateException("NetworkManager not initialized");
        }
        return client.getEntityManager();
    }

    public void update() {
        if (client != null) {
            client.update();
        }
    }

    public void stop() {
        try {
            if (client != null) {
                client.stop();
            }

            if (server != null) {
                server.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isHost() {
        return isHost.get();
    }
}
