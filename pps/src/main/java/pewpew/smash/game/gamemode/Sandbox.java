package pewpew.smash.game.gamemode;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.network.NetworkManager;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.client.EntityRenderer;
import pewpew.smash.game.Camera;
import pewpew.smash.game.entities.Player;

public class Sandbox implements GameMode {
    private NetworkManager networkManager;
    private EntityRenderer entityRenderer;
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
        System.out.println("Updating !");

        try {
            networkManager.update();

            Player player = networkManager.getEntityManager().getPlayerEntity(User.getInstance().getLocalID().get());
            if (player != null) {
                camera.centerOn(player);
            } else {
                System.out.println("Player is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(Canvas canvas) {
        networkManager.getEntityManager().getPlayerEntity(User.getInstance().getLocalID().get()).render(canvas);

        try {
            canvas.scale(camera.getX(), camera.getY());
            entityRenderer.render(canvas, camera);
            canvas.resetScale();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reset() {
        if (networkManager != null) {
            networkManager.stop();
        }
    }
}