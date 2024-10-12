package pewpew.smash.game.overlay;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.HashMap;
import java.util.Map;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.states.GameStateType;
import pewpew.smash.game.states.StateManager;
import pewpew.smash.game.ui.Button;
import pewpew.smash.game.ui.ButtonImage;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.HelpMethods;
import pewpew.smash.game.utils.ResourcesLoader;

public class PlayOverlay extends Overlay {

    private ButtonImage[] buttons = new ButtonImage[3];
    private Button backButton;
    private ButtonImage hoveredButton = null;
    private Map<ButtonImage, BufferedImage> blurredBackgrounds = new HashMap<>();

    public PlayOverlay(int x, int y, int width, int height) {
        super(x, y, width, height);
        loadBackground();
        loadButtons();
        generateBlurredBackgrounds();
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
        if (hoveredButton != null && blurredBackgrounds.containsKey(hoveredButton)) {
            canvas.renderImage(blurredBackgrounds.get(hoveredButton), x, y, width, height);
        } else {
            canvas.renderImage(background, x, y, width, height);
        }
    }

    private void renderButtons(Canvas canvas) {
        for (ButtonImage buttonImage : buttons) {
            buttonImage.render(canvas);
        }
        backButton.render(canvas);
    }

    private void renderTitle(Canvas canvas) {
        FontFactory.IMPACT_X_LARGE.applyFont(canvas);
        String title = "Choose Your Game Mode!";
        int titleWidth = FontFactory.IMPACT_X_LARGE.getFontWidth(title, canvas);
        canvas.renderString(title, (width - titleWidth) / 2, 180);
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
        hoveredButton = null;
        for (ButtonImage button : buttons) {
            boolean isHovered = HelpMethods.isIn(button.getBounds());
            button.setMouseOver(isHovered);
            if (isHovered) {
                hoveredButton = button;
            }
        }
        backButton.setMouseOver(HelpMethods.isIn(backButton.getBounds()));
    }

    private void setButtonPressedState(Button button, boolean isPressed) {
        if (HelpMethods.isIn(button.getBounds())) {
            button.setMousePressed(isPressed);
        }
    }

    private void loadBackground() {
        this.background = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "overlay");
    }

    private void loadButtons() {
        this.buttons[0] = new ButtonImage(Constants.LEFT_PADDING, 320 - 120, 230, 240,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/sandbox"),
                () -> StateManager.getInstance().setState(GameStateType.PLAYING));
        this.buttons[1] = new ButtonImage(Constants.LEFT_PADDING + 230 + 25, 320 - 120, 230, 240,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/battleRoyale"),
                () -> System.out.println("Battle Royale"));
        this.buttons[2] = new ButtonImage(Constants.RIGHT_PADDING - 20, 320 - 120, 230, 240,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/arena"),
                () -> System.out.println("Arena"));
        this.backButton = new Button(
                Constants.LEFT_PADDING,
                Constants.TOP_PADDING,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/backButton"),
                this::close);
    }

    private void generateBlurredBackgrounds() {
        for (ButtonImage buttonImage : buttons) {
            blurredBackgrounds.put(buttonImage, applyBlurFilter(buttonImage.getNormalSprite()));
        }
    }

    private BufferedImage applyBlurFilter(BufferedImage image) {
        int kernelSize = 15;
        float[] matrix = new float[kernelSize * kernelSize];
        float value = 1.0f / (kernelSize * kernelSize);

        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = value;
        }

        BufferedImageOp op = new ConvolveOp(new Kernel(kernelSize, kernelSize, matrix), ConvolveOp.EDGE_NO_OP, null);
        return op.filter(image, null);
    }

}