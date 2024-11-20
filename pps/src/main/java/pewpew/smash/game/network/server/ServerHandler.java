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
import pewpew.smash.game.network.packets.BasePacket;
import pewpew.smash.game.network.packets.DirectionPacket;
import pewpew.smash.game.network.packets.MouseInputPacket;
import pewpew.smash.game.network.packets.PickupItemRequestPacket;
import pewpew.smash.game.network.packets.PlayerJoinedPacket;
import pewpew.smash.game.network.packets.PlayerLeftPacket;
import pewpew.smash.game.network.packets.PlayerUsernamePacket;
import pewpew.smash.game.network.packets.PreventActionForPlayerPacket;
import pewpew.smash.game.network.packets.ReloadWeaponRequestPacket;
import pewpew.smash.game.network.packets.UseConsumableRequestPacket;
import pewpew.smash.game.network.packets.WeaponStatePacket;
import pewpew.smash.game.network.packets.WeaponSwitchRequestPacket;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.processor.serverProcessor.ServerDirectionPacketProcessor;
import pewpew.smash.game.network.processor.serverProcessor.ServerMouseInputPacketProcessor;
import pewpew.smash.game.network.processor.serverProcessor.ServerPickupItemRequestPacketProcessor;
import pewpew.smash.game.network.processor.serverProcessor.ServerPreventActionForPlayerPacketProcessor;
import pewpew.smash.game.network.processor.serverProcessor.ServerReloadWeaponRequestPacketProcessor;
import pewpew.smash.game.network.processor.serverProcessor.ServerUseConsumableRequestPacketProcessor;
import pewpew.smash.game.network.processor.serverProcessor.ServerUsernamePacketProcessor;
import pewpew.smash.game.network.processor.serverProcessor.ServerWeaponStatePacketProcessor;
import pewpew.smash.game.network.processor.serverProcessor.ServerWeaponSwitchRequestPacketProcessor;
import pewpew.smash.game.network.serializer.WeaponStateSerializer;
import pewpew.smash.game.objects.ItemGenerator;
import pewpew.smash.game.world.entities.Crate;
import pewpew.smash.game.world.entities.WorldEntityType;
import pewpew.smash.game.world.entities.WorldStaticEntity;

public class ServerHandler extends Handler implements Runnable {

    private ExecutorService executor;
    private ServerWrapper server;
    private EntityManager entityManager;
    private ServerEntityUpdater entityUpdater;
    private ServerItemUpdater itemUpdater;
    private ServerWorldManager worldManager;
    private ServerCollisionManager collisionManager;
    private GameTime gameTime;

    private final Map<Class<? extends BasePacket>, PacketProcessor<? extends BasePacket>> packetProcessors = new HashMap<>();

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

        WorldStaticEntity stone = new WorldStaticEntity(WorldEntityType.STONE, 1200, 1200);
        entityManager.addStaticEntity(1, stone);

        WorldStaticEntity tree = new WorldStaticEntity(WorldEntityType.TREE, 1000, 1000);
        entityManager.addStaticEntity(2, tree);

        WorldStaticEntity bush = new WorldStaticEntity(WorldEntityType.BUSH, 750, 300);
        entityManager.addStaticEntity(3, bush);

        Crate crate = new Crate(600, 600, null);
        entityManager.addStaticEntity(4, crate);
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
        if (packet instanceof BasePacket basePacket) {
            @SuppressWarnings("unchecked")
            PacketProcessor<BasePacket> processor = (PacketProcessor<BasePacket>) packetProcessors
                    .get(packet.getClass());
            if (processor != null) {
                processor.process(connection, basePacket);
            } else {
                System.out.println("Unknown packet type: " + packet.getClass().getName());
            }
        } else {
            System.err.println("Received an invalid packet type: " + packet.getClass().getName());
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
        packetProcessors.put(PlayerUsernamePacket.class, new ServerUsernamePacketProcessor(entityManager, server));
        packetProcessors.put(DirectionPacket.class, new ServerDirectionPacketProcessor(entityManager, server));
        packetProcessors.put(MouseInputPacket.class, new ServerMouseInputPacketProcessor(entityManager, server));
        packetProcessors.put(ReloadWeaponRequestPacket.class,
                new ServerReloadWeaponRequestPacketProcessor(entityManager, server));
        packetProcessors.put(WeaponSwitchRequestPacket.class,
                new ServerWeaponSwitchRequestPacketProcessor(entityManager, server));
        packetProcessors.put(PickupItemRequestPacket.class,
                new ServerPickupItemRequestPacketProcessor(entityManager, server, itemUpdater));
        packetProcessors.put(WeaponStatePacket.class, new ServerWeaponStatePacketProcessor(entityManager, server));
        packetProcessors.put(UseConsumableRequestPacket.class,
                new ServerUseConsumableRequestPacketProcessor(entityManager, server));
        packetProcessors.put(PreventActionForPlayerPacket.class,
                new ServerPreventActionForPlayerPacketProcessor(entityManager, server));
    }
}
