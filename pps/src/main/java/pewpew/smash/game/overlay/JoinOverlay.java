package pewpew.smash.game.overlay;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.gamemode.GameModeManager;
import pewpew.smash.game.gamemode.GameModeType;
import pewpew.smash.game.network.upnp.NetworkUtils;
import pewpew.smash.game.states.GameStateType;
import pewpew.smash.game.states.StateManager;
import pewpew.smash.game.ui.Button;
import pewpew.smash.game.ui.TextField;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.ResourcesLoader;

public class JoinOverlay extends Overlay {

    private TextField ipInput;
    private TextField portInput;
    private Button playButton;
    private Button backButton;

    private String errorMessage = "";
    private Timer errorResetTimer;

    public JoinOverlay(int x, int y, int width, int height) {
        super(x, y, width, height);
        loadBackground();
        loadInputs();
        loadButtons();
    }

    @Override
    public void update() {
        ipInput.update();
        portInput.update();
        playButton.update();
        backButton.update();
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderImage(background, x, y, width, height);
        renderTitle(canvas);
        renderLabels(canvas);
        ipInput.render(canvas);
        portInput.render(canvas);
        playButton.render(canvas);
        backButton.render(canvas);
        renderErrorMessage(canvas);
    }

    private void resetErrorMessageAfterDelay() {
        if (errorResetTimer != null) {
            errorResetTimer.cancel();
        }
        errorResetTimer = new Timer();
        errorResetTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                errorMessage = "";
            }
        }, 2000);
    }

    private void loadInputs() {
        ipInput = new TextField(90, 120, width - 300, 30);
        portInput = new TextField(90, 200, width - 300, 30);
    }

    private void loadButtons() {
        this.playButton = new Button(90, 300,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/playButton"),
                this::validateInputs);

        this.backButton = new Button(500, 300,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/backButton"),
                () -> {
                    close();
                    OverlayManager.getInstance().push(OverlayFactory.getOverlay(OverlayType.PLAY));
                });
    }

    private void renderTitle(Canvas canvas) {
        FontFactory.IMPACT_LARGE.applyFont(canvas);
        String title = "Join Game";
        int titleWidth = FontFactory.IMPACT_LARGE.getFontWidth(title, canvas);
        canvas.renderString(title, (width - titleWidth) / 2, 50);
    }

    private void renderLabels(Canvas canvas) {
        FontFactory.IMPACT_SMALL.applyFont(canvas);
        canvas.renderString("Server IP:", 90, 110);
        canvas.renderString("Port:", 90, 190);
    }

    private void renderErrorMessage(Canvas canvas) {
        if (!errorMessage.isEmpty()) {
            FontFactory.IMPACT_SMALL.applyFont(canvas);
            int errorWidth = FontFactory.IMPACT_SMALL.getFontWidth(errorMessage, canvas);
            canvas.renderString(errorMessage, (width - errorWidth) / 2, height - 50, Color.RED);
        }
    }

    private void validateInputs() {
        String ip = ipInput.getText().trim();
        String port = portInput.getText().trim();

        if (ip.isEmpty() || port.isEmpty()) {
            errorMessage = "Both IP and Port fields must be filled.";
            resetErrorMessageAfterDelay();
            return;
        }

        if (!NetworkUtils.validateIP(ip)) {
            errorMessage = "Invalid IP address.";
            resetErrorMessageAfterDelay();
            return;
        }

        try {
            if (!NetworkUtils.validatePort(Integer.parseInt(port))) {
                errorMessage = "Invalid Port. Port must be between 1 and 65535.";
                resetErrorMessageAfterDelay();
                return;
            }
        } catch (NumberFormatException e) {
            errorMessage = "Port must be a number.";
            resetErrorMessageAfterDelay();
        }

        errorMessage = "";

        StateManager.getInstance().setState(GameStateType.PLAYING);
        GameModeManager.getInstance().setGameMode(GameModeType.SANDBOX);
        GameModeManager.getInstance().getCurrentGameMode().build(new String[] { ip, port, "false" });

        close();
    }

    private void loadBackground() {
        background = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "overlay");
    }
}
