package pewpew.smash.game.gamemode;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.Camera;
import pewpew.smash.game.SpectatorManager;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.hud.HudManager;
import pewpew.smash.game.input.GamePad;
import pewpew.smash.game.network.NetworkManager;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.client.ClientEntityRenderer;
import pewpew.smash.game.network.client.ClientEntityUpdater;
import pewpew.smash.game.network.client.ClientItemRenderer;
import pewpew.smash.game.objects.ItemFactory;
import pewpew.smash.game.objects.SpecialType;
import pewpew.smash.game.objects.special.Scope;
import pewpew.smash.game.world.WorldClientIntegration;

public class Sandbox implements GameMode {
    private NetworkManager networkManager;
    private ClientEntityRenderer entityRenderer;
    private ClientItemRenderer itemRenderer;
    private ClientEntityUpdater entityUpdater;
    private BufferedImage worldImage;
    private Camera camera;

    public Sandbox() {
        this.camera = Camera.getInstance();
    }

    @Override
    public void build() {
        this.networkManager = NetworkManager.getInstance();
        SpectatorManager.getInstance().initialize(networkManager.getEntityManager());
        entityRenderer = new ClientEntityRenderer(networkManager.getEntityManager());
        entityUpdater = new ClientEntityUpdater(networkManager.getEntityManager());
        itemRenderer = new ClientItemRenderer();
    }

    @Override
    public void update() {
        if (WorldClientIntegration.getInstance().isWorldLoaded() && this.worldImage == null) {
            this.worldImage = WorldClientIntegration.getInstance().getWorldImage();
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
        if (local == null)
            return;

        if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_F1)) {
            local.setScope((Scope) ItemFactory.createItem(SpecialType.SCOPE_X1));
        } else if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_F2)) {
            local.setScope((Scope) ItemFactory.createItem(SpecialType.SCOPE_X2));
        } else if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_F3)) {
            local.setScope((Scope) ItemFactory.createItem(SpecialType.SCOPE_X3));
        } else if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_F4)) {
            local.setScope((Scope) ItemFactory.createItem(SpecialType.SCOPE_X4));
        }
    }

    @Override
    public void render(Canvas canvas) {
        canvas.scale(Camera.getZoom(), Camera.getZoom());
        if (this.worldImage != null) {
            canvas.renderImage(this.worldImage, (int) -this.camera.getX(), (int) -this.camera.getY());
        }
        itemRenderer.render(canvas, camera, networkManager.getEntityManager()
                .getPlayerEntity(User.getInstance().getLocalID().get()));
        entityRenderer.render(canvas, camera);
        networkManager.getEventsManager().render(canvas, camera);
        canvas.resetScale();

        if (User.getInstance().isDead()) {
            SpectatorManager.getInstance().render(canvas);
        }
        canvas.renderString(networkManager.getBroadcastMessage(), 140, 580, Color.WHITE);
    }

    @Override
    public void reset() {
        if (networkManager != null) {
            networkManager.stop();
            HudManager.getInstance().reset();
            this.worldImage = null;
        }
    }
}
