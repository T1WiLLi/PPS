package pewpew.smash.game.network.client;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import pewpew.smash.game.network.processor.clientProcessor.ClientBulletRemovePacketProcessor;

public class ClientHandler extends Handler {

    @Getter
    private final EntityManager entityManager;
    private final ClientUpdater clientUpdater;
    @Getter
    private final ClientEventManager clientEventManager;
    private final ClientWrapper client;
    private final Map<Integer, List<PositionPacket>> positionPacketQueue;
    private final Map<Class<? extends BasePacket>, PacketProcessor<? extends BasePacket>> packetProcessors;

    @Setter
    @Getter
    private boolean isIntentionalDisconnect;

    private String currentBroadcastedMessage = "";

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public ClientHandler(String host, int port) {
        this.client = new ClientWrapper(host, port, port);
        this.entityManager = new EntityManager();
        this.clientEventManager = new ClientEventManager();
        this.clientUpdater = new ClientUpdater(this.entityManager);
        this.positionPacketQueue = new ConcurrentHashMap<>();
        registersClasses(this.client.getKryo());
        ClientPacketRegistry clientRegistry = new ClientPacketRegistry(entityManager, client, this, clientEventManager);
        packetProcessors = clientRegistry.getPacketProcessors();
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
    protected void onConnect(Connection connection) {
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

    public void update() {
        this.clientUpdater.update(this.client);
        this.clientEventManager.update();
    }

    public void setCurrentBroadcastMessage(String message) {
        this.currentBroadcastedMessage = message;

        scheduler.schedule(() -> {
            this.currentBroadcastedMessage = "";
        }, 3, TimeUnit.SECONDS);
    }

    public String getCurrentBroadcastedMessage() {
        return this.currentBroadcastedMessage;
    }

    public void resetCurrentBroadcastedMessage() {
        this.currentBroadcastedMessage = "";
    }
}
