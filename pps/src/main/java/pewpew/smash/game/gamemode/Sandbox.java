package pewpew.smash.game.gamemode;

import java.awt.event.KeyEvent;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.network.NetworkManager;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.client.EntityRenderer;
import pewpew.smash.game.world.WorldGenerator;
import pewpew.smash.game.Camera;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.input.GamePad;

import java.awt.image.BufferedImage;

public class Sandbox implements GameMode {
    private NetworkManager networkManager;
    private EntityRenderer entityRenderer;
    private BufferedImage worldImage;
    private Camera camera;

    public Sandbox() {
        this.camera = new Camera();
    }

    @Override
    public void build(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("Missing required arguments: host, port, isHosting");
        }

        try {
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            boolean isHosting = Boolean.parseBoolean(args[2]);

            networkManager = new NetworkManager();
            networkManager.initialize(host, port, isHosting);
            entityRenderer = new EntityRenderer(networkManager.getEntityManager());
            System.out.println("Sandbox initialized");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize network components", e);
        }
    }

    @Override
    public void update(double deltaTime) {
        if (networkManager.isWorldDataReceived() && this.worldImage == null) {
            this.worldImage = WorldGenerator.getWorldImage(networkManager.getWorldData());
        }

        try {
            networkManager.update();

            Player player = networkManager.getEntityManager().getPlayerEntity(User.getInstance().getLocalID().get());
            if (player != null) {
                camera.centerOn(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_F1)) {
            camera.setZoom(1);
        } else if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_F2)) {
            camera.setZoom(0.75f);
        } else if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_F3)) {
            camera.setZoom(0.5f);
        } else if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_F4)) {
            camera.setZoom(0.25f);
        }
    }

    @Override
    public void render(Canvas canvas) {
        canvas.scale(Camera.getZoom(), Camera.getZoom());
        canvas.renderImage(this.worldImage, (int) -this.camera.getX(), (int) -this.camera.getY());
        entityRenderer.render(canvas, camera);
        canvas.resetScale();
    }

    @Override
    public void reset() {
        if (networkManager != null) {
            networkManager.stop();
        }
    }
}