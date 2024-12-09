package pewpew.smash.game.states;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.network.NetworkManager;
import pewpew.smash.game.network.client.ClientPostGameManager;
import pewpew.smash.game.ui.Button;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.ResourcesLoader;

public class PostGame implements State {

    private BufferedImage background;
    private Button quitButton;

    public PostGame() {
        init();
    }

    @Override
    public void update() {
        quitButton.update();
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderImage(background, 0, 0,
                Constants.DEFAULT_SCREEN_WIDTH, Constants.DEFAULT_SCREEN_HEIGHT);

        canvas.renderRectangle(0, 0, Constants.DEFAULT_SCREEN_WIDTH, Constants.DEFAULT_SCREEN_HEIGHT,
                new Color(0f, 0f, 0f, 0.3f));

        FontFactory.IMPACT_X_LARGE.applyFont(canvas);
        canvas.setColor(Color.WHITE);
        canvas.renderString("Game Over!", 200, 100);

        FontFactory.IMPACT_MEDIUM.applyFont(canvas);
        canvas.setColor(Color.YELLOW);
        canvas.renderString("Winner: " + ClientPostGameManager.getInstance().getWinnerName(), 200, 200);

        FontFactory.IMPACT_SMALL.applyFont(canvas);
        canvas.setColor(Color.LIGHT_GRAY);
        List<String> allPlayers = ClientPostGameManager.getInstance().getAllPlayers();
        int y = 300;
        canvas.renderString("Players:", 200, y - 30);
        for (String player : allPlayers) {
            canvas.renderString("- " + player, 200, y);
            y += 30;
        }

        FontFactory.resetFont(canvas);
        quitButton.render(canvas);
    }

    @Override
    public void handleKeyPress(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (NetworkManager.getInstance() != null) {
                NetworkManager.getInstance().stop();
            }
            StateManager.getInstance().setState(GameStateType.MENU);
        }
    }

    @Override
    public void handleKeyRelease(KeyEvent e) {
        // Not needed
    }

    private void init() {
        background = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "lobby-background");
        int x = Constants.LEFT_PADDING;
        int y = Constants.DEFAULT_SCREEN_HEIGHT - Constants.BOTTOM_PADDING - Constants.BUTTON_HEIGHT;
        quitButton = new Button(
                x,
                y,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/quitButton"),
                () -> System.exit(0));
    }
}