package pewpew.smash.game.network.server;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.engine.GameTime;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.network.Handler;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.ClientIDResponsePacket;
import pewpew.smash.game.network.packets.DirectionPacket;
import pewpew.smash.game.network.packets.PlayerJoinedPacket;
import pewpew.smash.game.network.packets.PlayerLeftPacket;
import pewpew.smash.game.network.packets.PlayerUsernamePacket;
import pewpew.smash.game.network.packets.PositionPacket;

public class ServerHandler extends Handler implements Runnable {

    private ExecutorService executor;
    private ServerWrapper server;
    private EntityManager entityManager;
    private GameTime gameTime;

    public ServerHandler(int port) {
        this.server = new ServerWrapper(port, port);
        this.executor = Executors.newSingleThreadExecutor();
        this.entityManager = new EntityManager();
        this.gameTime = GameTime.getInstance();
        registersClasses(this.server.getKryo());
    }

    @Override
    public void start() throws IOException {
        this.server.start();
        this.server.addListener(bindListener());
        this.executor.execute(this);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (gameTime.shouldUpdate()) {
                update(gameTime.getDeltaTime());
                sendStateUpdate();
            }
        }
    }

    @Override
    protected void handlePacket(Connection connection, Object packet) {
        if (packet instanceof PlayerUsernamePacket) {
            PlayerUsernamePacket usernamePacket = (PlayerUsernamePacket) packet;
            this.entityManager.getPlayerEntity(connection.getID()).setUsername(usernamePacket.getUsername());
            PlayerJoinedPacket joinedPacket = new PlayerJoinedPacket(connection.getID(), usernamePacket.getUsername());
            this.server.sendToAllTCP(joinedPacket); // TODO : Should we exclude the sender? if so, we will need to use
                                                    // the function sendToAllExceptTCP
        } else if (packet instanceof DirectionPacket) {
            DirectionPacket directionPacket = (DirectionPacket) packet;
            this.entityManager.getPlayerEntity(connection.getID()).setDirection(directionPacket.getDirection());
        }
    }

    @Override
    protected void onConnect(Connection connection) {
        Player player = new Player(connection.getID());
        player.teleport(400, 300);
        this.entityManager.addPlayerEntity(player.getId(), player);
        this.server.sendToTCP(connection.getID(), new ClientIDResponsePacket(player.getId()));
        PlayerJoinedPacket joinedPacket = new PlayerJoinedPacket(player.getId(), player.getUsername());

        this.server.sendToAllExceptTCP(connection.getID(), joinedPacket);

        this.entityManager.playerEntitiesIterator().forEachRemaining(existingPlayer -> {
            if (existingPlayer.getId() != player.getId()) {
                PlayerJoinedPacket existingPlayerPacket = new PlayerJoinedPacket(
                        existingPlayer.getId(),
                        existingPlayer.getUsername());
                this.server.sendToTCP(connection.getID(), existingPlayerPacket);
            }
        });
    }

    @Override
    protected void onDisconnect(Connection connection) {
        this.entityManager.removePlayerEntity(connection.getID());
        this.server.sendToAllTCP(new PlayerLeftPacket(connection.getID()));
    }

    @Override
    public void stop() {
        try {
            this.server.stop();
            this.executor.shutdown();

            if (!this.executor.awaitTermination(30, TimeUnit.SECONDS)) {
                this.executor.shutdownNow();
                if (!this.executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    System.err.println("Thread did not terminate");
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

    private void update(double deltaTime) { // Ensure that the update() function in the player class never modifies the
                                            // collection (it shouldn't anyway lmao)
        this.entityManager.playerEntitiesIterator().forEachRemaining(player -> player.updateServer(deltaTime));
    }

    private void sendStateUpdate() {
        sendPlayerPos();
    }

    private void sendPlayerPos() {
        this.entityManager.playerEntitiesIterator().forEachRemaining(player -> {
            PositionPacket packet = new PositionPacket(player.getId(), player.getX(), player.getY(),
                    player.getRotation());
            this.server.sendToAllUDP(packet);
        });
    }
}
