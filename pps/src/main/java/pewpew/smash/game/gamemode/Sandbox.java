package pewpew.smash.game.gamemode;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.Camera;
import pewpew.smash.game.GameManager;
import pewpew.smash.game.entities.Player;
import pewpew.smash.game.input.GamePad;
import pewpew.smash.game.world.WorldGenerator;

import java.awt.image.BufferedImage;

import java.awt.event.KeyEvent;

public class Sandbox implements GameMode {

    private boolean isMultiplayer;
    private Player player1;
    private Camera camera;
    private BufferedImage image;

    public void build(boolean isMultiplayer) {
        this.isMultiplayer = isMultiplayer;
    }

    @Override
    public void update(double deltaTime) {
        player1.updateServer(deltaTime);
        if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_ESCAPE)) {
            GameManager.getInstance().conclude();
        }
        if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_1)) {
            camera.setZoom(1.0f);
        } else if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_2)) {
            camera.setZoom(0.75f);
        } else if (GamePad.getInstance().isKeyPressed(KeyEvent.VK_3)) {
            camera.setZoom(0.5f);
        }
        camera.centerOn(player1);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.scale(camera.getZoom(), camera.getZoom());
        canvas.renderImage(this.image, (int) -this.camera.getX(), (int) -this.camera.getY());
        canvas.translate(-camera.getX(), -camera.getY());
        player1.render(canvas);
        canvas.translate(camera.getX(), camera.getY());
        canvas.resetScale();
    }

    @Override
    public void reset() {

    }

    @Override
    public void start() {
        player1 = new Player(0);
        player1.teleport(1250, 1250);
        camera = new Camera();
        WorldGenerator generator = new WorldGenerator();
        image = WorldGenerator.getWorldImage(generator.getWorldData());
    }
}
