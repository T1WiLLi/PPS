package pewpew.smash.game.overlay;

import java.awt.Color;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.ui.Button;
import pewpew.smash.game.ui.Cycler;
import pewpew.smash.game.ui.TextField;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.ResourcesLoader;

public class HostOverlay extends Overlay {

    private Button playButton, backButton;
    private TextField serverNameField, portField;
    private Cycler gamemodeSelector;

    private String selectedMode = "Sandbox"; // Default

    public HostOverlay(int x, int y, int width, int height) {
        super(x, y, width, height);
        loadBackground();
        loadButtons();
        loadTextField();
        loadCycler();
    }

    @Override
    public void update() {
        updateButtons();
        updateTextField();
        updateCycler();
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderImage(background, x, y, width, height);
        playButton.render(canvas);
        backButton.render(canvas);
        serverNameField.render(canvas);
        portField.render(canvas);
        gamemodeSelector.render(canvas);
        renderLabels(canvas);
    }

    private void updateButtons() {
        playButton.update();
        backButton.update();
    }

    private void updateTextField() {
        serverNameField.update();
        portField.update();
    }

    private void updateCycler() {
        gamemodeSelector.update();
    }

    private void renderLabels(Canvas canvas) {
        canvas.setColor(Color.WHITE);
        FontFactory.IMPACT_LARGE.applyFont(canvas);
        String title = "Create a game";
        int titleWidth = FontFactory.IMPACT_LARGE.getFontWidth(title, canvas);
        canvas.renderString(title, (width - titleWidth) / 2, 50);
        FontFactory.IMPACT_SMALL.applyFont(canvas);
        canvas.renderString("Server name", 50, 105);
        canvas.renderString("Port", 50, 172);
        canvas.renderString("Game mode : " + gamemodeSelector.getCurrentCycle(), 50, 235);
        FontFactory.resetFont(canvas);
    }

    private void loadBackground() {
        this.background = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "overlay");
    }

    private void loadButtons() {
        playButton = new Button(Constants.LEFT_PADDING + 60, 500,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/playButton"),
                () -> System.out.println("Play !"));
        backButton = new Button(Constants.RIGHT_PADDING - 60, 500,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/backButton"), () -> {
                    close();
                    OverlayManager.getInstance().push(OverlayFactory.getOverlay(OverlayType.PLAY));
                });
    }

    private void loadTextField() {
        serverNameField = new TextField(50, 120, 500, 30);
        portField = new TextField(50, 180, 500, 30);
    }

    private void loadCycler() {
        gamemodeSelector = new Cycler(250, 220, 20, 20, new String[] { "Sandbox", "Battle Royale", "Arena" },
                selectedMode, () -> selectedMode = gamemodeSelector.getCurrentCycle());
    }
}
