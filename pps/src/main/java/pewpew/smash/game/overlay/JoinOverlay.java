package pewpew.smash.game.overlay;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.ui.Button;
import pewpew.smash.game.ui.TextField;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.HelpMethods;
import pewpew.smash.game.utils.ResourcesLoader;

public class JoinOverlay extends Overlay {

    private TextField ipInput;
    private TextField portInput;
    private Button playButton;
    private Button backButton;
    private String errorMessage = "";

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

    @Override
    public void handleMousePress(MouseEvent e) {
        if (HelpMethods.isIn(ipInput.getBounds())) {
            ipInput.setFocused(true);
            portInput.setFocused(false);
        } else if (HelpMethods.isIn(portInput.getBounds())) {
            portInput.setFocused(true);
            ipInput.setFocused(false);
        } else {
            ipInput.setFocused(false);
            portInput.setFocused(false);
        }
    }

    @Override
    public void handleKeyPress(KeyEvent e) {
        if (ipInput.isFocused()) {
            ipInput.keyPressed(e);
        } else if (portInput.isFocused()) {
            portInput.keyPressed(e);
        }
    }

    private void loadInputs() {
        ipInput = new TextField(x + 50, y + 100, width - 300, 30);
        portInput = new TextField(x + 50, y + 160, width - 300, 30);
    }

    private void loadButtons() {
        this.playButton = new Button(x + width / 2 - 120, 240, 110, 40,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/playButton"),
                this::validateInputs);

        this.backButton = new Button(x + width / 2 + 10, 240, 110, 40,
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
        canvas.renderString("Server IP:", x + 50, y + 90);
        canvas.renderString("Port:", x + 50, y + 150);
    }

    private void renderErrorMessage(Canvas canvas) {
        if (!errorMessage.isEmpty()) {
            FontFactory.IMPACT_SMALL.applyFont(canvas);
            int errorWidth = FontFactory.IMPACT_SMALL.getFontWidth(errorMessage, canvas);
            canvas.renderString(errorMessage, (width - errorWidth) / 2, height - 50, Color.RED);
        }
    }

    private void validateInputs() {
        if (ipInput.getText().isEmpty() || portInput.getText().isEmpty()) {
            errorMessage = "Both IP and Port fields must be filled.";
        } else {
            errorMessage = "";
            System.out.println("Joining server at IP: " + ipInput.getText() + " on port: " + portInput.getText());
        }
    }

    private void loadBackground() {
        background = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "overlay");
    }
}
