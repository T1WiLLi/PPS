package pewpew.smash.game.network.client;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.esotericsoftware.kryonet.Connection;
import lombok.Getter;
import lombok.Setter;
import pewpew.smash.engine.controls.Direction;
import pewpew.smash.engine.controls.MouseInput;
import pewpew.smash.game.Alert.AlertManager;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.hud.HudManager;
import pewpew.smash.game.input.GamePad;
import pewpew.smash.game.input.MouseHandler;
import pewpew.smash.game.network.Handler;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.model.PlayerState;
import pewpew.smash.game.network.packets.*;
import pewpew.smash.game.network.serializer.WeaponStateSerializer;

public class ClientHandler extends Handler {
    @Getter
    private final EntityManager entityManager;
    private final ClientWrapper client;
    private final ConcurrentHashMap<Integer, String> pendingPlayers;

    @Getter
    private byte[][] worldData;
    @Getter
    private boolean isWorldDataReceived;

    @Setter
    @Getter
    private boolean isIntentionalDisconnect;

    public ClientHandler(String host, int port) {
        this.client = new ClientWrapper(host, port, port);
        this.entityManager = new EntityManager();
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

    private void handlePlayerJoinedPacket(PlayerJoinedPacket playerJoined) {
        if (this.entityManager.getPlayerEntity(playerJoined.getId()) == null) {
            pendingPlayers.put(playerJoined.getId(), playerJoined.getUsername());
            Player player = new Player(playerJoined.getId(), playerJoined.getUsername());
            this.entityManager.addPlayerEntity(player.getId(), player);
            System.out.println("Player joined: " + player.getId() + " - " + player.getUsername());
        }
    }

    private void handlePlayerLeftPacket(PlayerLeftPacket playerLeft) {
        pendingPlayers.remove(playerLeft.getId());
        this.entityManager.removePlayerEntity(playerLeft.getId());
        System.out.println("Player left: " + playerLeft.getId());
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
        this.entityManager.getPlayerEntities().forEach(Player::updateClient);
        sendDirection();
        sendMouseInput();
        sendWeaponState();
    }

    private void sendDirection() {
        Player localPlayer = entityManager.getPlayerEntity(User.getInstance().getLocalID().get());
        if (localPlayer != null) {
            Direction direction = GamePad.getInstance().getDirection();
            double rotation = MouseHandler.getAngle(localPlayer.getX() + localPlayer.getWidth() / 2,
                    localPlayer.getY() + localPlayer.getHeight() / 2);
            this.client.sendToUDP(new DirectionPacket(direction, (float) rotation));
        }
    }

    private void sendMouseInput() {
        MouseInput input = MouseInput.getCurrentInput();
        this.client.sendToUDP(new MouseInputPacket(input));
    }

    private void sendWeaponState() {
        Player localPlayer = entityManager.getPlayerEntity(User.getInstance().getLocalID().get());
        if (localPlayer != null) {
            WeaponStatePacket packet = WeaponStateSerializer.serializeWeaponState(localPlayer);
            if (packet != null) {
                this.client.sendToUDP(packet);
            }
        }
    }
}