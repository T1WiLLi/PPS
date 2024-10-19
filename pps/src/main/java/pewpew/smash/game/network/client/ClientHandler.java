package pewpew.smash.game.network.client;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.engine.GameTime;
import pewpew.smash.engine.controls.Direction;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.input.GamePad;
import pewpew.smash.game.network.Handler;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.ClientIDResponsePacket;
import pewpew.smash.game.network.packets.DirectionPacket;
import pewpew.smash.game.network.packets.PlayerJoinedPacket;
import pewpew.smash.game.network.packets.PlayerLeftPacket;
import pewpew.smash.game.network.packets.PlayerUsernamePacket;
import pewpew.smash.game.network.packets.PositionPacket;

public class ClientHandler extends Handler implements Runnable {
    private ExecutorService executor;

    private ClientWrapper client;
    private EntityManager entityManager;
    private GamePad gamePad;
    private User local;

    public ClientHandler(String host, int port) {
        this.client = new ClientWrapper(host, port, port);
        this.local = User.getInstance();
        this.entityManager = new EntityManager();
        this.gamePad = GamePad.getInstance();
        this.executor = Executors.newSingleThreadExecutor();
        registersClasses(this.client.getKryo());
    }

    @Override
    public void start() throws IOException {
        this.client.start();
        this.client.addListener(bindListener());
        this.executor.execute(this);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            // This is not the best way to handle this, but it's the simplest, We should
            // allow the server to time the update, but since the update cannot be directly
            // modified (at least not in production (except for cheat)) this should be fine
            // for now.
            if (GameTime.getInstance().shouldUpdate()) {
                sendDirection();
            }
            if (GameTime.getInstance().shouldRender()) {
                // Render
            }
        }
    }

    @Override
    protected void handlePacket(Connection connection, Object packet) {
        if (packet instanceof PositionPacket) {
            PositionPacket position = (PositionPacket) packet;
            Player player = this.entityManager.getPlayerEntity(position.getId());
            player.teleport(position.getX(), position.getY());
            player.setRotation(position.getR());
        } else if (packet instanceof PlayerJoinedPacket) {
            PlayerJoinedPacket playerJoined = (PlayerJoinedPacket) packet;
            Player player = new Player(playerJoined.getId(), playerJoined.getUsername());
            this.entityManager.addPlayerEntity(player.getId(), player);
        } else if (packet instanceof PlayerLeftPacket) {
            PlayerLeftPacket playerLeft = (PlayerLeftPacket) packet;
            this.entityManager.removePlayerEntity(playerLeft.getId());
        }
    }

    @Override
    protected void onConnect(Connection connection) {
        Player player = new Player(connection.getID(),
                local.getUsername().equals("Guest") ? local.getUsername() + "-" + connection.getID()
                        : local.getUsername());

        this.client.sendToTCP(new PlayerUsernamePacket(
                local.getUsername().equals("Guest") ? local.getUsername() + "-" + connection.getID()
                        : local.getUsername()));

        this.entityManager.addPlayerEntity(player.getId(), player);
    }

    @Override
    protected void onDisconnect(Connection connection) {

    }

    @Override
    public void stop() {
        try {
            this.client.stop();
            this.executor.shutdown();

            if (!this.executor.awaitTermination(30, TimeUnit.SECONDS)) {
                this.executor.shutdownNow();
                if (!this.executor.awaitTermination(15, TimeUnit.SECONDS)) {
                    throw new InterruptedException("Executor  did not terminate");
                }
            }
        } catch (InterruptedException e) {
            this.executor.shutdownNow();
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendDirection() {
        Direction direction = this.gamePad.getDirection();
        this.client.sendToUDP(new DirectionPacket(direction));
    }
}
