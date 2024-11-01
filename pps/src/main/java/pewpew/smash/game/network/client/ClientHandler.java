package pewpew.smash.game.network.client;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.esotericsoftware.kryonet.Connection;
import lombok.Getter;
import lombok.Setter;
import pewpew.smash.game.SpectatorManager;
import pewpew.smash.game.Alert.AlertManager;
import pewpew.smash.game.entities.Bullet;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.hud.HudManager;
import pewpew.smash.game.network.Handler;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.model.PlayerState;
import pewpew.smash.game.network.packets.*;

public class ClientHandler extends Handler {
    @Getter
    private final EntityManager entityManager;
    private final ClientUpdater clientUpdater;
    private final ClientWrapper client;
    private final ConcurrentHashMap<Integer, String> pendingPlayers;

    @Getter
    private byte[][] worldData;
    @Getter
    private boolean isWorldDataReceived;

    @Setter
    @Getter
    private boolean isIntentionalDisconnect;

    private String currentBroadcastedMessage = "";

    @Getter
    private int spectatingPlayerId = Integer.MIN_VALUE;

    public ClientHandler(String host, int port) {
        this.client = new ClientWrapper(host, port, port);
        this.entityManager = new EntityManager();
        this.clientUpdater = new ClientUpdater(this.entityManager);
        this.pendingPlayers = new ConcurrentHashMap<>();
        registersClasses(this.client.getKryo());
    }

    @Override
    public void start() throws IOException {
        this.client.addListener(bindListener());
        this.client.start();
    }

    @Override
    protected synchronized void handlePacket(Connection connection, Object packet) {
        try {
            if (packet instanceof PositionPacket position) {
                handlePositionPacket(position);
            } else if (packet instanceof MouseActionPacket mouseActionPacket) {
                handleMouseActionPacket(mouseActionPacket);
            } else if (packet instanceof PlayerStatePacket) {
                handlePlayerStatePacket((PlayerStatePacket) packet);
            } else if (packet instanceof BulletCreatePacket) {
                handleBulletCreatePacket((BulletCreatePacket) packet);
            } else if (packet instanceof BulletRemovePacket) {
                handleBulletRemovePacket((BulletRemovePacket) packet);
            } else if (packet instanceof PlayerDeathPacket) {
                handlePlayerDeathPacket((PlayerDeathPacket) packet);
            } else if (packet instanceof BroadcastMessagePacket) {
                handleBroadcastMessagePacket((BroadcastMessagePacket) packet);
            } else if (packet instanceof PlayerJoinedPacket playerJoined) {
                handlePlayerJoinedPacket(playerJoined);
            } else if (packet instanceof PlayerLeftPacket playerLeft) {
                handlePlayerLeftPacket(playerLeft);
            } else if (packet instanceof WorldDataPacket worldDataPacket) {
                handleWorldDataPacket(worldDataPacket);
            }
        } catch (Exception e) {
            System.err.println("Error handling packet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handlePositionPacket(PositionPacket position) {
        Player player = this.entityManager.getPlayerEntity(position.getId());
        if (player != null) {
            player.teleport(position.getX(), position.getY());
            player.setRotation(position.getR());
        } else {
            System.out.println("Queuing position update for player: " + position.getId());
            String username = pendingPlayers.get(position.getId());
            if (username != null) {
                Player newPlayer = new Player(position.getId(), username);
                newPlayer.teleport(position.getX(), position.getY());
                newPlayer.setRotation(position.getR());
                this.entityManager.addPlayerEntity(newPlayer.getId(), newPlayer);
            }
        }
    }

    private void handleMouseActionPacket(MouseActionPacket mouseActionPacket) {
        Player player = this.entityManager.getPlayerEntity(mouseActionPacket.getPlayerID());
        if (player != null) {
            player.setMouseInput(mouseActionPacket.getMouseInput());
        } else {
            System.out
                    .println("Cannot process mouse action for non-existent player: " + mouseActionPacket.getPlayerID());
        }
    }

    private void handlePlayerStatePacket(PlayerStatePacket packet) {
        PlayerState newState = packet.getState();
        Player player = this.entityManager.getPlayerEntity(newState.getId());
        player.applyState(newState);
    }

    private void handleBulletCreatePacket(BulletCreatePacket packet) {
        Bullet bullet = new Bullet(entityManager.getPlayerEntity(packet.getOwnerID()));
        bullet.setId(packet.getBulletID());
        bullet.teleport(packet.getX(), packet.getY());
        this.entityManager.addBulletEntity(packet.getBulletID(), bullet);
    }

    private void handleBulletRemovePacket(BulletRemovePacket packet) {
        this.entityManager.removeBulletEntity(packet.getBulletID());
    }

    private void handlePlayerDeathPacket(PlayerDeathPacket packet) {
        int deadPlayerId = packet.getDeadPlayerID();
        int killerPlayerId = packet.getKillerPlayerID();

        if (deadPlayerId == User.getInstance().getLocalID().get()) {
            User.getInstance().setDead(true);
            SpectatorManager.getInstance().startSpectating(killerPlayerId);
        } else if (SpectatorManager.getInstance().isSpectating() &&
                deadPlayerId == SpectatorManager.getInstance().getSpectatingPlayerId()) {
            SpectatorManager.getInstance().startSpectating(killerPlayerId);
        }
        entityManager.removePlayerEntity(deadPlayerId);
    }

    private void handleBroadcastMessagePacket(BroadcastMessagePacket packet) {
        this.currentBroadcastedMessage = packet.getMessage();
    }

    private void handlePlayerJoinedPacket(PlayerJoinedPacket playerJoined) {
        if (this.entityManager.getPlayerEntity(playerJoined.getId()) == null) {
            pendingPlayers.put(playerJoined.getId(), playerJoined.getUsername());
            Player player = new Player(playerJoined.getId(), playerJoined.getUsername());
            this.entityManager.addPlayerEntity(player.getId(), player);
            this.currentBroadcastedMessage = player.getUsername() + " has joined the game.";

        }
    }

    private void handlePlayerLeftPacket(PlayerLeftPacket packet) {
        Player player = entityManager.getPlayerEntity(packet.getId());
        if (player != null) {
            currentBroadcastedMessage = player.getUsername() + " has left the game.";
            entityManager.removePlayerEntity(packet.getId());
        } else {
            System.err.println("Warning: Attempted to process a player left event for a non-existent player with ID "
                    + packet.getId());
        }
    }

    private void handleWorldDataPacket(WorldDataPacket worldDataPacket) {
        this.worldData = worldDataPacket.getWorldData();
        this.isWorldDataReceived = true;
    }

    @Override
    protected synchronized void onConnect(Connection connection) {
        System.out.println("Connecting to the server ... ");
        User.getInstance().setID(connection.getID());
        String username = User.getInstance().getUsername().equals("Guest")
                ? User.getInstance().getUsername() + "-" + connection.getID()
                : User.getInstance().getUsername();

        Player player = new Player(connection.getID(), username);
        this.entityManager.addPlayerEntity(player.getId(), player);
        this.client.sendToTCP(new PlayerUsernamePacket(username));
        HudManager.getInstance().setPlayer(player);
    }

    @Override
    protected void onDisconnect(Connection connection) {
        pendingPlayers.clear();
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

    public Player getSpectatingTarget() {
        if (entityManager.containsPlayerEntity(spectatingPlayerId)) {
            return entityManager.getPlayerEntity(spectatingPlayerId);
        }
        return null;
    }
}