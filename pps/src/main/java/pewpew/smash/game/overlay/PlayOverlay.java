package pewpew.smash.game.overlay;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.ui.Button;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.ResourcesLoader;

public class PlayOverlay extends Overlay {

    private Button joinButton;
    private Button hostButton;
    private Button backButton;
    private String description = "";

    public PlayOverlay(int x, int y, int width, int height) {
        super(x, y, width, height);
        loadBackground();
        loadButtons();
    }

    @Override
    public void update() {
        joinButton.update();
        hostButton.update();
        backButton.update();
        updateDescription();
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderImage(background, x, y, width, height);
        joinButton.render(canvas);
        hostButton.render(canvas);
        backButton.render(canvas);
        renderTitle(canvas);
        renderDescription(canvas);
        FontFactory.resetFont(canvas);
    }

    private void renderTitle(Canvas canvas) {
        FontFactory.IMPACT_X_LARGE.applyFont(canvas);
        String title = "Choose Your Option!";
        int titleWidth = FontFactory.IMPACT_X_LARGE.getFontWidth(title, canvas);
        canvas.renderString(title, (width - titleWidth) / 2, 180);
    }

    private void renderDescription(Canvas canvas) {
        FontFactory.SMALL_FONT.applyFont(canvas);
        int descriptionWidth = FontFactory.SMALL_FONT.getFontWidth(description, canvas);
        canvas.renderString(description, (width - descriptionWidth) / 2, height - 50);
    }

    private void updateDescription() {
        description = "Join or Host a game!";
        if (joinButton.isMouseOver()) {
            description = "Join a game and play with your friends !";
        } else if (hostButton.isMouseOver()) {
            description = "Host a game and invite your friends to play with you!";
        }
    }

    private void loadBackground() {
        this.background = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "overlay");
    }

    private void loadButtons() {
        this.joinButton = new Button(Constants.LEFT_PADDING + 100, 320, 230, 60,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/joinButton"),
                () -> {
                    close();
                    OverlayManager.getInstance().push(OverlayFactory.getOverlay(OverlayType.JOIN));
                });

        this.hostButton = new Button(Constants.RIGHT_PADDING - 100, 320, 230, 60,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/hostButton"),
                () -> {
                    close();
                    OverlayManager.getInstance().push(OverlayFactory.getOverlay(OverlayType.HOST));
                });

        this.backButton = new Button(Constants.LEFT_PADDING, Constants.TOP_PADDING,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/backButton"),
                this::close);
    }
}