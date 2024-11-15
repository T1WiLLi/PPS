package pewpew.smash.game.network.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
import pewpew.smash.game.network.packets.PickupItemRequestPacket;
import pewpew.smash.game.network.packets.PlayerJoinedPacket;
import pewpew.smash.game.network.packets.PlayerLeftPacket;
import pewpew.smash.game.network.packets.PlayerUsernamePacket;
import pewpew.smash.game.network.packets.ReloadWeaponRequestPacket;
import pewpew.smash.game.network.packets.UseConsumableRequestPacket;
import pewpew.smash.game.network.packets.WeaponStatePacket;
import pewpew.smash.game.network.packets.WeaponSwitchRequestPacket;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.processor.serverProcessor.DirectionPacketProcessor;
import pewpew.smash.game.network.processor.serverProcessor.MouseInputPacketProcessor;
import pewpew.smash.game.network.processor.serverProcessor.PickupItemRequestPacketProcessor;
import pewpew.smash.game.network.processor.serverProcessor.ReloadWeaponRequestPacketProcessor;
import pewpew.smash.game.network.processor.serverProcessor.UseConsumableRequestPacketProcessor;
import pewpew.smash.game.network.processor.serverProcessor.UsernamePacketProcessor;
import pewpew.smash.game.network.processor.serverProcessor.WeaponStatePacketProcessor;
import pewpew.smash.game.network.processor.serverProcessor.WeaponSwitchRequestPacketProcessor;
import pewpew.smash.game.network.serializer.WeaponStateSerializer;
import pewpew.smash.game.objects.ItemGenerator;

public class ServerHandler extends Handler implements Runnable {

    private ExecutorService executor;
    private ServerWrapper server;
    private EntityManager entityManager;
    private ServerEntityUpdater entityUpdater;
    private ServerItemUpdater itemUpdater;
    private ServerWorldManager worldManager;
    private ServerCollisionManager collisionManager;
    private GameTime gameTime;

    private final Map<Class<?>, PacketProcessor> packetProcessors = new HashMap<>();

    public ServerHandler(int port) {
        this.server = new ServerWrapper(port, port);
        this.executor = Executors.newSingleThreadExecutor();
        this.entityManager = new EntityManager();
        this.entityUpdater = new ServerEntityUpdater(entityManager);
        this.itemUpdater = new ServerItemUpdater();
        this.collisionManager = new ServerCollisionManager(entityManager);
        this.worldManager = new ServerWorldManager();
        this.gameTime = GameTime.getServerInstance();

        // this.worldManager.displayWorld();
        ServerBulletTracker.getInstance().setServerReference(this.server);

        initPacketProcessors();
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
        new ItemGenerator().generateItems(server, this.worldManager.getWorldData(), 15);
        while (!Thread.currentThread().isInterrupted()) {
            if (gameTime.shouldUpdate()) {
                update(gameTime.getDeltaTime());
                sendStateUpdate();
            }
        }
    }

    @Override
    protected void handlePacket(Connection connection, Object packet) {
        PacketProcessor processor = packetProcessors.get(packet.getClass());
        if (processor != null) {
            processor.process(connection, packet);
        } else {
            System.out.println("Unknown packet type: " + packet.getClass().getName());
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
            WeaponStatePacket weaponStatePacket = WeaponStateSerializer
                    .serializeWeaponState(existingPlayer.getEquippedWeapon());
            this.server.sendToTCP(connection.getID(), existingPlayerPacket);
            this.server.sendToTCP(connection.getID(), weaponStatePacket);
        });

        WeaponStatePacket playerWeaponStatePacket = WeaponStateSerializer
                .serializeWeaponState(player.getEquippedWeapon());
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
    public synchronized void stop() {
        try {
            if (server != null) {
                server.stop();
            }

            if (executor != null) {
                executor.shutdown();

                if (!executor.awaitTermination(30, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                    if (!executor.awaitTermination(30, TimeUnit.MILLISECONDS)) {
                        System.err.println("Executor did not terminate");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error during server shutdown: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Shutdown was interrupted: " + e.getMessage());
        } finally {
            server = null;
            executor = null;
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

    private void initPacketProcessors() {
        packetProcessors.put(PlayerUsernamePacket.class, new UsernamePacketProcessor(entityManager, server));
        packetProcessors.put(DirectionPacket.class, new DirectionPacketProcessor(entityManager, server));
        packetProcessors.put(MouseInputPacket.class, new MouseInputPacketProcessor(entityManager, server));
        packetProcessors.put(ReloadWeaponRequestPacket.class,
                new ReloadWeaponRequestPacketProcessor(entityManager, server));
        packetProcessors.put(WeaponSwitchRequestPacket.class,
                new WeaponSwitchRequestPacketProcessor(entityManager, server));
        packetProcessors.put(PickupItemRequestPacket.class,
                new PickupItemRequestPacketProcessor(entityManager, server, itemUpdater));
        packetProcessors.put(WeaponStatePacket.class, new WeaponStatePacketProcessor(entityManager, server));
        packetProcessors.put(UseConsumableRequestPacket.class,
                new UseConsumableRequestPacketProcessor(entityManager, server));
    }
}
