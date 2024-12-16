package pewpew.smash.game.network.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.esotericsoftware.kryonet.Connection;

import pewpew.smash.game.entities.Player;
import pewpew.smash.game.gamemode.GameModeType;
import pewpew.smash.game.network.Handler;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.BasePacket;
import pewpew.smash.game.network.packets.PlayerJoinedPacket;
import pewpew.smash.game.network.packets.PlayerLeftPacket;
import pewpew.smash.game.network.packets.StartGamePacket;
import pewpew.smash.game.network.packets.SyncTimePacket;
import pewpew.smash.game.network.packets.WeaponStatePacket;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.serializer.WeaponStateSerializer;
import pewpew.smash.game.world.WorldGenerator;

public class ServerHandler extends Handler implements Runnable {

    private ExecutorService executor;
    private ServerWrapper server;
    private EntityManager entityManager;
    private ServerEntityUpdater entityUpdater;
    private ServerItemUpdater itemUpdater;
    private ServerWorldManager worldManager;
    private ServerCollisionManager collisionManager;
    private ServerEventManager eventManager;
    private ServerTime serverTime;

    private final Map<Class<? extends BasePacket>, PacketProcessor<? extends BasePacket>> packetProcessors;

    private ServerLobbyManager lobbyManager;
    private ServerPostGameManager postGameManager;
    private GameModeType gamemode;
    private boolean gameStarted = false;

    public ServerHandler(int port, GameModeType type) {
        ServerTime.reset();
        this.gamemode = type;
        this.server = new ServerWrapper(port, port);
        this.executor = Executors.newSingleThreadExecutor();
        this.entityManager = new EntityManager();
        this.entityUpdater = new ServerEntityUpdater(entityManager);
        this.itemUpdater = new ServerItemUpdater();
        this.worldManager = new ServerWorldManager(server, entityManager, 25, 40);
        this.collisionManager = new ServerCollisionManager(server, entityManager, worldManager.getWorldData());
        this.serverTime = ServerTime.getInstance();
        this.eventManager = new ServerEventManager(type, worldManager.getWorldData());
        ServerBulletTracker.getInstance().setServerReference(this.server);

        this.lobbyManager = new ServerLobbyManager(server, type.name());
        this.postGameManager = new ServerPostGameManager(server);

        registersClasses(this.server.getKryo());
        ServerPacketRegistry serverRegistry = new ServerPacketRegistry(entityManager, server, itemUpdater,
                lobbyManager);
        packetProcessors = serverRegistry.getPacketProcessors();
        ServerAudioManager.getInstance().setDependencies(server, entityManager);
    }

    @Override
    public void start() throws IOException {
        this.server.addListener(bindListener());
        this.server.start();
        this.executor.execute(this);
    }

    @Override
    public void run() {
        this.eventManager.initEvents(this.server);

        while (!Thread.currentThread().isInterrupted()) {
            if (lobbyManager.isLobbyActive()) {
                lobbyManager.updateLobby();
            } else {
                if (!gameStarted) {
                    postLobbyGameInit();
                }

                if (serverTime.shouldUpdate()) {
                    update();
                    sendStateUpdate();
                    eventManager.update(this.server, this.entityManager);
                    if (!gamemode.equals(GameModeType.SANDBOX)) {
                        checkWinCondition();
                    }
                }
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
        }
    }

    @Override
    protected void onConnect(Connection connection) {
        if (lobbyManager.isLobbyActive()) {
            lobbyManager.addPlayer(connection.getID(), "");
        } else if (gamemode.equals(GameModeType.SANDBOX)) {
            server.sendToAllTCP(new StartGamePacket(this.gamemode.name()));
            Player player = new Player(connection.getID());
            player.teleport(1000, 1000);
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
            this.worldManager.sendWorldData(connection.getID());
            this.server.sendToTCP(connection.getID(),
                    new SyncTimePacket(ServerTime.getInstance().getElapsedTimeMillis()));
        } else {
            // Game started, not in sandbox, for now, ignore connection :)
        }
    }

    @Override
    protected void onDisconnect(Connection connection) {
        if (lobbyManager.isLobbyActive()) {
            lobbyManager.removePlayer(connection.getID());
        } else {
            this.entityManager.removePlayerEntity(connection.getID());
            this.server.sendToAllTCP(new PlayerLeftPacket(connection.getID()));
        }
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

    private void checkWinCondition() {
        if (gameStarted && this.entityManager.getPlayerEntities().size() == 1) {
            Player winner = this.entityManager.getPlayerEntities().stream().findFirst().orElse(null);
            if (winner != null) {
                this.postGameManager.triggerPostGame(winner, new ArrayList<>(entityManager.getPlayerEntities()));
                gameStarted = false;
            }
        }
    }

    private void update() {
        this.entityUpdater.update(this.server);
        this.collisionManager.checkCollisions();
        this.collisionManager.checkWaterCollision();
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

    private void postLobbyGameInit() {
        ServerTime.reset();
        Map<Integer, String> finalLobbyPlayers = lobbyManager.getLobbyPlayers();

        for (Map.Entry<Integer, String> entry : finalLobbyPlayers.entrySet()) {
            int playerId = entry.getKey();
            String username = entry.getValue();

            Player player = new Player(playerId, username);
            int[] pos = getRandomPlayerPos();
            player.teleport(pos[0], pos[1]);
            player.setRotation(0);
            this.entityManager.addPlayerEntity(playerId, player);
        }

        for (Player player : this.entityManager.getPlayerEntities()) {
            PlayerJoinedPacket playerJoinedPacket = new PlayerJoinedPacket(player.getId(), player.getUsername());
            WeaponStatePacket weaponStatePacket = WeaponStateSerializer
                    .serializeWeaponState(player.getEquippedWeapon());
            this.server.sendToAllTCP(playerJoinedPacket);
            this.server.sendToAllTCP(weaponStatePacket);
        }

        for (Player player : this.entityManager.getPlayerEntities()) {
            int playerId = player.getId();
            this.worldManager.sendWorldData(playerId);
            this.server.sendToTCP(playerId, new SyncTimePacket(ServerTime.getInstance().getElapsedTimeMillis()));
        }

        lobbyManager.clearLobby();
        gameStarted = true;
    }

    private int[] getRandomPlayerPos() {
        if (gamemode.equals(GameModeType.SANDBOX)) {
            return new int[] { (WorldGenerator.getWorldWidth() / 2 * WorldGenerator.TILE_SIZE),
                    (WorldGenerator.getWorldHeight() / 2 * WorldGenerator.TILE_SIZE) };
        }

        int worldWidth = WorldGenerator.getWorldWidth() * WorldGenerator.TILE_SIZE;
        int worldHeight = WorldGenerator.getWorldHeight() * WorldGenerator.TILE_SIZE;
        Random random = new Random();

        while (true) {
            int x = random.nextInt(worldWidth);
            int y = random.nextInt(worldHeight);

            if (worldManager.getWorldData()[x][y] == WorldGenerator.GRASS) {
                boolean isNearWater = false;
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dy = -2; dy <= 2; dy++) {
                        int nx = x + dx;
                        int ny = y + dy;

                        if (nx >= 0 && nx < worldWidth && ny >= 0 && ny < worldHeight) {
                            if (worldManager.getWorldData()[nx][ny] == WorldGenerator.WATER) {
                                isNearWater = true;
                                break;
                            }
                        }
                    }
                    if (isNearWater)
                        break;
                }

                if (!isNearWater) {
                    return new int[] { x, y };
                }
            }
        }
    }
}