package pewpew.smash.game.overlay;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.controls.MouseController;
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.ui.Button;
import pewpew.smash.game.ui.ButtonImage;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.HelpMethods;
import pewpew.smash.game.utils.ResourcesLoader;

public class PlayOverlay extends Overlay {

    private ButtonImage[] buttons = new ButtonImage[3];
    private Button backButton;

    public PlayOverlay(OverlayManager overlayManager, int x, int y, int width, int height) {
        super(overlayManager, x, y, width, height);
        loadBackground();
        loadButtons();
    }

    @Override
    public void update() {
        for (ButtonImage buttonImage : buttons) {
            buttonImage.update();
        }
        this.backButton.update();
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderImage(background, x, y, width, height);
        for (ButtonImage buttonImage : buttons) {
            buttonImage.render(canvas);
        }
        this.backButton.render(canvas);
        FontFactory.IMPACT_X_LARGE.applyFont(canvas);
        canvas.renderString("Choose you're gamemode !",
                800 / 2 - FontFactory.IMPACT_X_LARGE.getFontWidth("Choose you're gamemode", canvas) / 2, 500);
        FontFactory.resetFont(canvas);
    }

    @Override
    public void handleMousePress(MouseEvent e) {
        for (Button button : buttons) {
            handleMouseInput(true, button);
        }
        handleMouseInput(true, backButton);
    }

    @Override
    public void handleMouseRelease(MouseEvent e) {
        for (Button button : buttons) {
            handleMouseInput(false, button);
        }
        handleMouseInput(false, backButton);
    }

    @Override
    public void handleMouseMove(MouseEvent e) {
        for (Button button : buttons) {
            button.setMouseOver(false);
        }
        backButton.setMouseOver(false);

        for (Button button : buttons) {
            if (HelpMethods.isIn(MouseController.getMouseX(), MouseController.getMouseY(), button.getBounds())) {
                button.setMouseOver(true);
            }
        }

        if (HelpMethods.isIn(MouseController.getMouseX(), MouseController.getMouseY(), backButton.getBounds())) {
            backButton.setMouseOver(true);
        }
    }

    @Override
    public void handleMouseDrag(MouseEvent e) {

    }

    @Override
    public void handleKeyPress(KeyEvent e) {

    }

    @Override
    public void handleKeyRelease(KeyEvent e) {

    }

    private void handleMouseInput(boolean isPressed, Button button) {
        if (HelpMethods.isIn(MouseController.getMouseX(), MouseController.getMouseY(), button.getBounds())) {
            button.setMousePressed(isPressed);
        }
    }

    private void loadBackground() {
        this.background = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "overlay");
    }

    private void loadButtons() {
        this.buttons[0] = new ButtonImage(Constants.LEFT_PADDING, 300 - 120, 230, 240,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/sandbox"),
                () -> System.out.println("Sandbox"));
        this.buttons[1] = new ButtonImage(Constants.LEFT_PADDING + 230 + 25, 300 - 120, 230, 240,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/battleRoyale"),
                () -> System.out.println("Battle Royale"));
        this.buttons[2] = new ButtonImage(Constants.RIGHT_PADDING - 20, 300 - 120, 230, 240,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/arena"),
                () -> System.out.println("Arena"));
        this.backButton = new Button(
                Constants.LEFT_PADDING,
                Constants.TOP_PADDING,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/backButton"),
                () -> close());
    }
}
