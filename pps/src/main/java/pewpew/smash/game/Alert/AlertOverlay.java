package pewpew.smash.game.Alert;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.overlay.Overlay;
import pewpew.smash.game.states.GameStateType;
import pewpew.smash.game.states.StateManager;
import pewpew.smash.game.ui.Button;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.ResourcesLoader;

import java.awt.Color;

public class AlertOverlay extends Overlay {

    private String title;
    private String message;
    private Button confirmButton;
    private static final Color OVERLAY_COLOR = new Color(0, 0, 0, 200);
    private static final int PADDING = 20;

    public AlertOverlay(String title, String message) {
        super(200, 100, 400, 200);
        this.title = title;
        this.message = message;
        loadButtons();
    }

    @Override
    public void update() {
        this.confirmButton.update();
    }

    @Override
    public void render(Canvas canvas) {
        canvas.renderRectangle(this.x, this.y, this.width, this.height, OVERLAY_COLOR);
        FontFactory.IMPACT_LARGE.applyFont(canvas);
        int titleWidth = FontFactory.IMPACT_LARGE.getFontWidth(this.title, canvas);
        canvas.renderString(this.title, this.x + (this.width - titleWidth) / 2, this.y + PADDING + 20, Color.WHITE);

        FontFactory.IMPACT_MEDIUM.applyFont(canvas);
        int messageWidth = FontFactory.IMPACT_MEDIUM.getFontWidth(this.message, canvas);
        canvas.renderString(this.message, this.x + (this.width - messageWidth) / 2, this.y + this.height / 2,
                Color.WHITE);

        this.confirmButton.render(canvas);
        FontFactory.resetFont(canvas);
    }

    private void loadButtons() {
        this.confirmButton = new Button(
                (200 + Constants.BUTTON_WIDTH / 2),
                (100 + Constants.BUTTON_HEIGHT / 2 + 120),
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/yesButton"),
                () -> {
                    StateManager.getInstance().setState(GameStateType.MENU);
                    close();
                });
    }
}
