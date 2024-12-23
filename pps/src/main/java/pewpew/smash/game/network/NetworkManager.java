package pewpew.smash.game.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import java.io.IOException;

import pewpew.smash.game.gamemode.GameModeType;
import pewpew.smash.game.network.client.ClientEventManager;
import pewpew.smash.game.network.client.ClientHandler;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.server.ServerHandler;
import pewpew.smash.game.network.upnp.UPnPPortManager;

public class NetworkManager {
    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();

    private ClientHandler client;
    private ServerHandler server;
    private final AtomicBoolean isHost;

    private static NetworkManager instance;

    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    private NetworkManager() {
        this.isHost = new AtomicBoolean(false);
    }

    public void initialize(String host, int port, boolean hosting, GameModeType type)
            throws IOException {
        this.isHost.set(hosting);

        if (hosting) {
            server = new ServerHandler(port, type);
            server.start();
            client = new ClientHandler("127.0.0.1", port);

            if (!host.equals("127.0.0.1")) {
                backgroundExecutor.submit(() -> {
                    UPnPPortManager.getInstance().openPort(port, port);
                    System.out.println("Port opened!");
                });
            }
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

    public ClientEventManager getEventsManager() {
        if (client == null) {
            throw new IllegalStateException("NetworkManager not initialized");
        }
        return client.getClientEventManager();
    }

    // Start a 5 sec timer, after that call client.resetCurrentBroadcastedMessage();
    public String getBroadcastMessage() {
        if (client == null) {
            throw new IllegalStateException("NetworkManager not initialized");
        }
        return client.getCurrentBroadcastedMessage();
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
                backgroundExecutor.submit(() -> {
                    System.out.println("Trying to close the server!");
                    UPnPPortManager.getInstance().closeAllPorts();
                });
            }
        } finally {
            server = null;
            client = null;
            shutdownExecutors();
        }
    }

    public boolean isHost() {
        return isHost.get();
    }

    private void shutdownExecutors() {
        backgroundExecutor.shutdown();
        try {
            if (!backgroundExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                backgroundExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            backgroundExecutor.shutdownNow();
        }
    }
}
