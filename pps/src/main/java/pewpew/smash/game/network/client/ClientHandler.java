package pewpew.smash.game.network.client;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import lombok.Getter;
import pewpew.smash.engine.controls.Direction;
import pewpew.smash.engine.controls.MouseInput;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.input.GamePad;
import pewpew.smash.game.input.MouseHandler;
import pewpew.smash.game.network.Handler;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.manager.EntityManager;
import pewpew.smash.game.network.packets.*;

public class ClientHandler extends Handler {
    @Getter
    private final EntityManager entityManager;
    private final ClientWrapper client;

    @Getter
    private byte[][] worldData;
    @Getter
    private boolean isWorldDataReceived;

    public ClientHandler(String host, int port) {
        this.client = new ClientWrapper(host, port, port);
        this.entityManager = new EntityManager();
        registersClasses(this.client.getKryo());
    }

    @Override
    public void start() throws IOException {
        this.client.addListener(bindListener());
        this.client.start();
    }

    @Override
    protected void handlePacket(Connection connection, Object packet) {
        if (packet instanceof PositionPacket position) {
            Player player = this.entityManager.getPlayerEntity(position.getId());
            if (player != null) {
                player.teleport(position.getX(), position.getY());
                player.setRotation(position.getR());
            } else {
                System.out.println("No player found: " + position.getId());
                System.out.println("Current Entities: " + this.entityManager.size());
                this.entityManager.getPlayerEntities().forEach(System.out::println);
            }
        } else if (packet instanceof PlayerJoinedPacket playerJoined) {
            if (this.entityManager.getPlayerEntity(playerJoined.getId()) == null) {
                Player player = new Player(playerJoined.getId(), playerJoined.getUsername());
                this.entityManager.addPlayerEntity(player.getId(), player);
            }
        } else if (packet instanceof PlayerLeftPacket playerLeft) {
            this.entityManager.removePlayerEntity(playerLeft.getId());
        } else if (packet instanceof WorldDataPacket) {
            this.worldData = ((WorldDataPacket) packet).getWorldData();
            this.isWorldDataReceived = true;
        }
    }

    @Override
    protected void onConnect(Connection connection) {
        System.out.println("Connecting to the server ... ");
        synchronized (this.entityManager) {
            User.getInstance().setID(connection.getID());
            String username = User.getInstance().getUsername().equals("Guest")
                    ? User.getInstance().getUsername() + "-" + connection.getID()
                    : User.getInstance().getUsername();

            Player player = new Player(connection.getID(), username);
            this.entityManager.addPlayerEntity(player.getId(), player);
            this.client.sendToTCP(new PlayerUsernamePacket(username));

            System.out.println("Connected to the server with ID: " + connection.getID());
            System.out.println("Player added to EntityManager: " + player);
        }
    }

    @Override
    protected void onDisconnect(Connection connection) {
        // Handle disconnect logic if needed
    }

    @Override
    public void stop() {
        try {
            this.client.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        this.entityManager.getPlayerEntities().forEach(Player::updateClient);
        sendDirection();
        sendMouseInput();
    }

    private void sendDirection() {
        Direction direction = GamePad.getInstance().getDirection();
        double rotation = MouseHandler.getAngle(
                entityManager.getPlayerEntity(User.getInstance().getLocalID().get()).getX(),
                entityManager.getPlayerEntity(User.getInstance().getLocalID().get()).getY());
        this.client.sendToUDP(new DirectionPacket(direction, (float) rotation));
    }

    private void sendMouseInput() {
        MouseInput input = MouseInput.getCurrentInput();
        this.client.sendToUDP(new MouseInputPacket(input));
    }
}