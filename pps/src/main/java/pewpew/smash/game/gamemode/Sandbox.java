package pewpew.smash.game.gamemode;

import java.awt.Color;
import java.awt.event.KeyEvent;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.network.NetworkManager;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.client.ClientEntityRenderer;
import pewpew.smash.game.network.client.ClientEntityUpdater;
import pewpew.smash.game.objects.ItemFactory;
import pewpew.smash.game.objects.SpecialType;
import pewpew.smash.game.world.WorldGenerator;
import pewpew.smash.game.Camera;
import pewpew.smash.game.SpectatorManager;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.hud.HudManager;
import pewpew.smash.game.input.GamePad;

import java.awt.image.BufferedImage;

public class Sandbox implements GameMode {
    private NetworkManager networkManager;
    private ClientEntityRenderer entityRenderer;
    private ClientEntityUpdater entityUpdater;
    private BufferedImage worldImage;
    private Camera camera;

    public Sandbox() {
        this.camera = Camera.getInstance();
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
            SpectatorManager.getInstance().initialize(networkManager.getEntityManager());
            entityRenderer = new ClientEntityRenderer(networkManager.getEntityManager());
            entityUpdater = new ClientEntityUpdater(networkManager.getEntityManager());
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
            HudManager.getInstance().setWorldImage(this.worldImage);
        }

        try {
            networkManager.update();
            entityUpdater.update();
            HudManager.getInstance()
                    .setAmountOfPlayerAlive(networkManager.getEntityManager().getPlayerEntities().size());

            if (User.getInstance().isDead()) {
                SpectatorManager.getInstance().update();
            } else {
                Player localPlayer = networkManager.getEntityManager()
                        .getPlayerEntity(User.getInstance().getLocalID().get());
                if (localPlayer != null) {
                    camera.centerOn(localPlayer);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        handleZoomControls();
    }

    private void handleZoomControls() {
        Player local = networkManager.getEntityManager().getPlayerEntity(User.getInstance().getLocalID().get());
        if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_F1)) {
            local.setScope(ItemFactory.createItem(SpecialType.SCOPE_X1));
        } else if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_F2)) {
            local.setScope(ItemFactory.createItem(SpecialType.SCOPE_X2));
        } else if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_F3)) {
            local.setScope(ItemFactory.createItem(SpecialType.SCOPE_X3));
        } else if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_F4)) {
            local.setScope(ItemFactory.createItem(SpecialType.SCOPE_X4));
        }
    }

    @Override
    public void render(Canvas canvas) {
        canvas.scale(Camera.getZoom(), Camera.getZoom());
        canvas.renderImage(this.worldImage, (int) -this.camera.getX(), (int) -this.camera.getY());
        entityRenderer.render(canvas, camera);

        if (User.getInstance().isDead()) {
            SpectatorManager.getInstance().render(canvas);
        }

        canvas.renderString(networkManager.getBroadcastMessage(), 140, 580, Color.WHITE);
        canvas.resetScale();
    }

    @Override
    public void reset() {
        if (networkManager != null) {
            networkManager.stop();
        }
    }
}