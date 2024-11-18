package pewpew.smash.game.network.client;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.esotericsoftware.kryonet.Connection;
import lombok.Getter;
import lombok.Setter;
import pewpew.smash.game.Alert.AlertManager;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.hud.HudManager;
import pewpew.smash.game.network.Handler;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.*;
import pewpew.smash.game.network.processor.PacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.BroadcastMessagePacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.BulletCreatePacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.BulletRemovePacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.InventoryPacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.ItemAddPacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.ItemRemovePacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.MouseActionPacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.PlayerDeathPacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.PlayerJoinedPacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.PlayerLeftPacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.PlayerStatePacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.PositionPacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.WeaponStatePacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.WorldDataPacketProcessor;
import pewpew.smash.game.world.entities.WorldEntityType;
import pewpew.smash.game.world.entities.WorldStaticEntity;

public class ClientHandler extends Handler {

    @Getter
    private final EntityManager entityManager;
    private final ClientUpdater clientUpdater;
    private final ClientWrapper client;
    private final Map<Integer, List<PositionPacket>> positionPacketQueue; // Queue for PositionPackets only
    private final Map<Class<?>, PacketProcessor> packetProcessors = new ConcurrentHashMap<>();

    @Getter
    @Setter
    private byte[][] worldData;
    @Getter
    @Setter
    private boolean isWorldDataReceived;

    @Setter
    @Getter
    private boolean isIntentionalDisconnect;

    @Setter
    private String currentBroadcastedMessage = "";

    public ClientHandler(String host, int port) {
        this.client = new ClientWrapper(host, port, port);
        this.entityManager = new EntityManager();
        this.clientUpdater = new ClientUpdater(this.entityManager);
        this.positionPacketQueue = new ConcurrentHashMap<>();
        registersClasses(this.client.getKryo());
        initPacketProcessors();

        WorldStaticEntity stone = new WorldStaticEntity(WorldEntityType.STONE, 1200, 1200);
        entityManager.addStaticEntity(1, stone);

        WorldStaticEntity tree = new WorldStaticEntity(WorldEntityType.TREE, 1000, 1000);
        entityManager.addStaticEntity(2, tree);

        WorldStaticEntity crate = new WorldStaticEntity(WorldEntityType.CRATE, 600, 600);
        entityManager.addStaticEntity(3, crate);
    }

    private void initPacketProcessors() {
        packetProcessors.put(PositionPacket.class, new PositionPacketProcessor(entityManager, client, this));
        packetProcessors.put(MouseActionPacket.class, new MouseActionPacketProcessor(entityManager, client));
        packetProcessors.put(PlayerStatePacket.class, new PlayerStatePacketProcessor(entityManager, client));
        packetProcessors.put(BulletCreatePacket.class, new BulletCreatePacketProcessor(entityManager, client));
        packetProcessors.put(BulletRemovePacket.class, new BulletRemovePacketProcessor(entityManager, client));
        packetProcessors.put(WeaponStatePacket.class, new WeaponStatePacketProcessor(entityManager, client));
        packetProcessors.put(ItemAddPacket.class, new ItemAddPacketProcessor(entityManager, client));
        packetProcessors.put(ItemRemovePacket.class, new ItemRemovePacketProcessor(entityManager, client));
        packetProcessors.put(InventoryPacket.class, new InventoryPacketProcessor(entityManager, client));
        packetProcessors.put(PlayerDeathPacket.class, new PlayerDeathPacketProcessor(entityManager, client));
        packetProcessors.put(BroadcastMessagePacket.class,
                new BroadcastMessagePacketProcessor(entityManager, client, this));
        packetProcessors.put(PlayerJoinedPacket.class, new PlayerJoinedPacketProcessor(entityManager, client, this));
        packetProcessors.put(PlayerLeftPacket.class, new PlayerLeftPacketProcessor(entityManager, client, this));
        packetProcessors.put(WorldDataPacket.class, new WorldDataPacketProcessor(entityManager, client, this));
    }

    @Override
    public void start() throws IOException {
        this.client.addListener(bindListener());
        this.client.start();
    }

    @Override
    protected synchronized void handlePacket(Connection connection, Object packet) {
        PacketProcessor processor = packetProcessors.get(packet.getClass());
        if (processor != null) {
            processor.process(connection, packet);
        } else {
            System.err.println("No processor found for packet type: " + packet.getClass().getSimpleName());
        }
    }

    public void queuePositionPacket(int playerId, PositionPacket packet) {
        positionPacketQueue.computeIfAbsent(playerId, k -> new ArrayList<>()).add(packet);
    }

    public void processQueuedPositionPackets(int playerId) {
        List<PositionPacket> queuedPackets = positionPacketQueue.remove(playerId);
        if (queuedPackets != null) {
            for (PositionPacket packet : queuedPackets) {
                handlePacket(null, packet);
            }
        }
    }

    @Override
    protected synchronized void onConnect(Connection connection) {
        System.out.println("Connecting to the server ... ");
        User.getInstance().setID(connection.getID());

        String username = User.getInstance().getUsername();
        Player player = new Player(connection.getID(), username);
        this.entityManager.addPlayerEntity(player.getId(), player);
        this.client.sendToTCP(new PlayerUsernamePacket(username));
        HudManager.getInstance().setPlayer(player);

        processQueuedPositionPackets(player.getId());
    }

    @Override
    protected void onDisconnect(Connection connection) {
        positionPacketQueue.clear();
        entityManager.clearAllEntities();
        System.out.println("DISCONNECTED");
        if (!isIntentionalDisconnect) {
            AlertManager.getInstance().showDisconnectAlert();
        }
    }

    @Override
    public void stop() {
        try {
            isIntentionalDisconnect = true;
            this.client.stop();
            ((BulletRemovePacketProcessor) this.packetProcessors.get(BulletRemovePacket.class)).shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void update() {
        this.clientUpdater.update(this.client);
    }

    public synchronized String getCurrentBroadcastedMessage() {
        return this.currentBroadcastedMessage;
    }
}
