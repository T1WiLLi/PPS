package pewpew.smash.game.network.client;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
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
import pewpew.smash.game.network.processor.clientProcessor.ClientBroadcastMessagePacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.ClientBulletCreatePacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.ClientBulletRemovePacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.ClientInventoryPacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.ClientItemAddPacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.ClientItemRemovePacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.ClientMouseActionPacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.ClientPlayerDeathPacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.ClientPlayerJoinedPacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.ClientPlayerLeftPacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.ClientPlayerStatePacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.ClientPositionPacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.ClientWeaponStatePacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.ClientWorldDataPacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.ClientWorldEntityAddPacketProcessor;
import pewpew.smash.game.network.processor.clientProcessor.ClientWorldEntityStatePacketProcessor;

public class ClientHandler extends Handler {

    @Getter
    private final EntityManager entityManager;
    private final ClientUpdater clientUpdater;
    private final ClientWrapper client;
    private final Map<Integer, List<PositionPacket>> positionPacketQueue;
    private final Map<Class<? extends BasePacket>, PacketProcessor<? extends BasePacket>> packetProcessors = new HashMap<>();

    @Getter
    @Setter
    private long seed;
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

    }

    private void initPacketProcessors() {
        packetProcessors.put(PositionPacket.class, new ClientPositionPacketProcessor(entityManager, client, this));
        packetProcessors.put(MouseActionPacket.class, new ClientMouseActionPacketProcessor(entityManager, client));
        packetProcessors.put(PlayerStatePacket.class, new ClientPlayerStatePacketProcessor(entityManager, client));
        packetProcessors.put(BulletCreatePacket.class, new ClientBulletCreatePacketProcessor(entityManager, client));
        packetProcessors.put(BulletRemovePacket.class, new ClientBulletRemovePacketProcessor(entityManager, client));
        packetProcessors.put(WeaponStatePacket.class, new ClientWeaponStatePacketProcessor(entityManager, client));
        packetProcessors.put(ItemAddPacket.class, new ClientItemAddPacketProcessor(entityManager, client));
        packetProcessors.put(ItemRemovePacket.class, new ClientItemRemovePacketProcessor(entityManager, client));
        packetProcessors.put(InventoryPacket.class, new ClientInventoryPacketProcessor(entityManager, client));
        packetProcessors.put(PlayerDeathPacket.class, new ClientPlayerDeathPacketProcessor(entityManager, client));
        packetProcessors.put(BroadcastMessagePacket.class,
                new ClientBroadcastMessagePacketProcessor(entityManager, client, this));
        packetProcessors.put(PlayerJoinedPacket.class,
                new ClientPlayerJoinedPacketProcessor(entityManager, client, this));
        packetProcessors.put(PlayerLeftPacket.class, new ClientPlayerLeftPacketProcessor(entityManager, client, this));
        packetProcessors.put(WorldDataPacket.class, new ClientWorldDataPacketProcessor(entityManager, client, this));
        packetProcessors.put(WorldEntityStatePacket.class,
                new ClientWorldEntityStatePacketProcessor(entityManager, client));
        packetProcessors.put(WorldEntityAddPacket.class,
                new ClientWorldEntityAddPacketProcessor(entityManager, client));
    }

    @Override
    public void start() throws IOException {
        this.client.addListener(bindListener());
        this.client.start();
    }

    @Override
    protected void handlePacket(Connection connection, Object packet) {
        if (packet instanceof BasePacket basePacket) {
            PacketProcessor<? extends BasePacket> processor = packetProcessors.get(packet.getClass());
            if (processor != null) {
                @SuppressWarnings("unchecked")
                PacketProcessor<BasePacket> typedProcessor = (PacketProcessor<BasePacket>) processor;
                typedProcessor.process(connection, basePacket);
            } else {
                System.out.println("Unknown packet type: " + packet.getClass().getName());
            }
        } else {
            System.err.println("Received an invalid packet type: " + packet.getClass().getName());
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
            ((ClientBulletRemovePacketProcessor) this.packetProcessors.get(BulletRemovePacket.class)).shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void update() {
        this.clientUpdater.update(this.client);
        System.out.println("Current amount of static entities: " + entityManager.getStaticEntities().size());
    }

    public synchronized String getCurrentBroadcastedMessage() {
        return this.currentBroadcastedMessage;
    }
}
