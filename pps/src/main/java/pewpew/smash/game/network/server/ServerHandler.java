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
import pewpew.smash.game.network.packets.DirectionPacket;
import pewpew.smash.game.network.packets.MouseInputPacket;
import pewpew.smash.game.network.packets.PlayerJoinedPacket;
import pewpew.smash.game.network.packets.PlayerLeftPacket;
import pewpew.smash.game.network.packets.PlayerUsernamePacket;
import pewpew.smash.game.network.packets.ReloadWeaponRequestPacket;
import pewpew.smash.game.network.packets.WeaponStatePacket;
import pewpew.smash.game.network.packets.WeaponSwitchRequestPacket;
import pewpew.smash.game.network.serializer.WeaponStateSerializer;
import pewpew.smash.game.objects.RangedWeapon;

public class ServerHandler extends Handler implements Runnable {

    private ExecutorService executor;
    private ServerWrapper server;
    private EntityManager entityManager;
    private ServerEntityUpdater entityUpdater;
    private ServerWorldManager worldManager;
    private ServerCollisionManager collisionManager;

    private GameTime gameTime;

    public ServerHandler(int port) {
        this.server = new ServerWrapper(port, port);
        this.executor = Executors.newSingleThreadExecutor();
        this.entityManager = new EntityManager();
        this.entityUpdater = new ServerEntityUpdater(entityManager);
        this.collisionManager = new ServerCollisionManager(entityManager);
        this.worldManager = new ServerWorldManager();
        this.worldManager.displayWorld();
        this.gameTime = GameTime.getServerInstance();
        ServerBulletTracker.getInstance().setServerReference(this.server);
        registersClasses(this.server.getKryo());
    }

    @Override
    public void start() throws IOException {
        this.server.addListener(bindListener());
        this.server.start();
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
            this.server.sendToAllUDP(joinedPacket);
        } else if (packet instanceof DirectionPacket) {
            DirectionPacket directionPacket = (DirectionPacket) packet;
            Player player = this.entityManager.getPlayerEntity(connection.getID());
            if (player != null) {
                player.setDirection(directionPacket.getDirection());
                player.setRotation(directionPacket.getRotation());
            }
        } else if (packet instanceof MouseInputPacket) {
            MouseInputPacket mouseInputPacket = (MouseInputPacket) packet;
            Player player = this.entityManager.getPlayerEntity(connection.getID());
            if (player != null) {
                player.setMouseInput(mouseInputPacket.getInput());
            }
        } else if (packet instanceof ReloadWeaponRequestPacket) {
            Player player = this.entityManager.getPlayerEntity(connection.getID());
            if (player != null) {
                ((RangedWeapon) player.getEquippedWeapon()).reload();
                WeaponStatePacket weaponStatePacket = WeaponStateSerializer.serializeWeaponState(player);
                this.server.sendToTCP(connection.getID(), weaponStatePacket);
            }
        } else if (packet instanceof WeaponSwitchRequestPacket) {
            WeaponSwitchRequestPacket weaponSwitchRequestPacket = (WeaponSwitchRequestPacket) packet;
            Player player = this.entityManager.getPlayerEntity(connection.getID());

            switch (weaponSwitchRequestPacket.getKeyCode()) {
                case 1 -> player.setEquippedWeapon(player.getFists());
                case 2 -> player.getInventory().getPrimaryWeapon().ifPresent(player::setEquippedWeapon);
            }

            WeaponStatePacket newWeaponState = WeaponStateSerializer.serializeWeaponState(player);
            this.server.sendToAllTCP(newWeaponState);
        } else if (packet instanceof WeaponStatePacket) {
            WeaponStatePacket weaponStatePacket = (WeaponStatePacket) packet;
            Player player = this.entityManager.getPlayerEntity(connection.getID());
            WeaponStateSerializer.deserializeWeaponState(weaponStatePacket, player);
        }
    }

    @Override
    protected void onConnect(Connection connection) {
        Player player = new Player(connection.getID());
        player.teleport(100, 100);
        player.setRotation(0);

        this.entityManager.getPlayerEntities().forEach(existingPlayer -> {
            PlayerJoinedPacket existingPlayerPacket = new PlayerJoinedPacket(
                    existingPlayer.getId(),
                    existingPlayer.getUsername());
            WeaponStatePacket weaponStatePacket = WeaponStateSerializer.serializeWeaponState(existingPlayer);
            this.server.sendToTCP(connection.getID(), existingPlayerPacket);
            this.server.sendToTCP(connection.getID(), weaponStatePacket);
        });
        WeaponStatePacket playerWeaponStatePacket = WeaponStateSerializer.serializeWeaponState(player);
        this.server.sendToTCP(connection.getID(), playerWeaponStatePacket);
        this.entityManager.addPlayerEntity(player.getId(), player);
        this.worldManager.sendWorldDataToClient(server, connection.getID());
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

    private void update(double deltaTime) {
        this.entityUpdater.update(this.server);
        this.collisionManager.checkCollisions();
    }

    // Do other state update, such as hp, collision, bullet, ammo, inventory , etc.
    private void sendStateUpdate() {
        sendPlayerPos();
        sendPlayerMouseInput();
    }

    private void sendPlayerPos() {
        this.entityUpdater.sendPlayerPositions(this.server);
    }

    private void sendPlayerMouseInput() {
        this.entityUpdater.sendPlayerMouseInput(this.server);
    }
}
