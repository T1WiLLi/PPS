package pewpew.smash.game.states;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.network.NetworkManager;
import pewpew.smash.game.network.User;
import pewpew.smash.game.network.client.ClientLobbyManager;
import pewpew.smash.game.ui.Button;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.ResourcesLoader;

public class Lobby implements State {

    private Button backButton;
    private BufferedImage background;
    private final String[] tips = {
            "Use cover to avoid enemy fire!",
            "Keep your weapon fully reloaded!.",
            "Keep moving to stay unpredictable.",
            "Manage your ammo carefully.",
            "A better Scope will allow you to see further.",
            "Use consumables to regain health in tough spots."
    };
    private int tipIndex = 0;
    private long lastTipChangeTime;

    public Lobby() {
        init();
    }

    private void init() {
        int x = Constants.LEFT_PADDING;
        int y = Constants.DEFAULT_SCREEN_HEIGHT - Constants.BOTTOM_PADDING - Constants.BUTTON_HEIGHT;
        backButton = new Button(
                x,
                y,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/backButton"),
                () -> {
                    if (NetworkManager.getInstance() != null) {
                        NetworkManager.getInstance().stop();
                    }
                    StateManager.getInstance().setState(GameStateType.MENU);
                });
        lastTipChangeTime = System.currentTimeMillis();
        background = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "lobby-background");
    }

    @Override
    public void update() {
        backButton.update();

        if (System.currentTimeMillis() - lastTipChangeTime > 5000) {
            tipIndex = (tipIndex + 1) % tips.length;
            lastTipChangeTime = System.currentTimeMillis();
        }
    }

    @Override
    public void render(Canvas canvas) {
        canvas.clear();
        canvas.renderImage(background, 0, 0, Constants.DEFAULT_SCREEN_WIDTH, Constants.DEFAULT_SCREEN_HEIGHT);

        FontFactory.IMPACT_LARGE.applyFont(canvas);
        canvas.setColor(Color.WHITE);
        canvas.renderString("Lobby - Waiting for players", 200, 100);
        FontFactory.resetFont(canvas);

        FontFactory.IMPACT_SMALL.applyFont(canvas);
        int y = 200;
        for (String p : ClientLobbyManager.getInstance().getPlayers()) {
            canvas.renderString(p, 100, y,
                    (User.getInstance().getLocalID().get() == Integer.parseInt(p.split("-")[1]) ? Color.GREEN
                            : Color.WHITE));
            y += 25;
        }
        FontFactory.resetFont(canvas);

        int countdown = ClientLobbyManager.getInstance().getCountdown();
        FontFactory.IMPACT_SMALL.applyFont(canvas);
        if (countdown > 0) {
            canvas.setColor(Color.YELLOW);
            canvas.renderString("Game starting in: " + countdown + "s", 100, y + 40);
        } else {
            canvas.setColor(Color.LIGHT_GRAY);
            canvas.renderString("Waiting for more players...", 100, y + 40);
        }
        FontFactory.resetFont(canvas);

        FontFactory.IMPACT_SMALL.applyFont(canvas);
        canvas.setColor(Color.ORANGE);
        y = 200;
        canvas.renderString("Tips:", 320, y);
        y += 30;
        for (String tip : tips) {
            canvas.renderString("- " + tip, 320, y);
            y += 20;
        }
        FontFactory.resetFont(canvas);

        backButton.render(canvas);
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
}
