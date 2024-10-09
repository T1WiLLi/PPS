package pewpew.smash.game.overlay;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.ui.Button;
import pewpew.smash.game.ui.ButtonImage;
import pewpew.smash.game.utils.FontFactory;
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
        updateButtons();
    }

    @Override
    public void render(Canvas canvas) {
        renderBackground(canvas);
        renderButtons(canvas);
        renderTitle(canvas);
        FontFactory.resetFont(canvas);
    }

    @Override
    public void handleMousePress(MouseEvent e) {
        handleMouseInput(true);
    }

    @Override
    public void handleMouseRelease(MouseEvent e) {
        handleMouseInput(false);
    }

    @Override
    public void handleMouseMove(MouseEvent e) {
        resetButtonHoverStates();
        updateButtonHoverStates();
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

    private void updateButtons() {
        for (ButtonImage buttonImage : buttons) {
            buttonImage.update();
        }
        backButton.update();
    }

    private void renderBackground(Canvas canvas) {
        canvas.renderImage(background, x, y, width, height);
    }

    private void renderButtons(Canvas canvas) {
        for (ButtonImage buttonImage : buttons) {
            buttonImage.render(canvas);
        }
        backButton.render(canvas);
    }

    private void renderTitle(Canvas canvas) {
        FontFactory.IMPACT_X_LARGE.applyFont(canvas);
        canvas.renderString("Choose your gamemode!",
                width / 2 - FontFactory.IMPACT_X_LARGE.getFontWidth("Choose your gamemode!", canvas) / 2, 500);
    }

    private void handleMouseInput(boolean isPressed) {
        for (Button button : buttons) {
            setButtonPressedState(button, isPressed);
        }
        setButtonPressedState(backButton, isPressed);
    }

    private void resetButtonHoverStates() {
        for (Button button : buttons) {
            button.setMouseOver(false);
        }
        backButton.setMouseOver(false);
    }

    private void updateButtonHoverStates() {
        for (Button button : buttons) {
            button.setMouseOver(isMouseInside(button.getBounds()));
        }
        backButton.setMouseOver(isMouseInside(backButton.getBounds()));
    }

    private void setButtonPressedState(Button button, boolean isPressed) {
        if (isMouseInside(button.getBounds())) {
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
                this::close);
    }
}