package pewpew.smash.game.overlay;

import java.awt.Color;

import pewpew.smash.engine.Canvas;
import pewpew.smash.game.gamemode.GameModeManager;
import pewpew.smash.game.states.GameStateType;
import pewpew.smash.game.states.StateManager;
import pewpew.smash.game.ui.Button;
import pewpew.smash.game.utils.ResourcesLoader;

public class PauseOverlay extends Overlay {

    private Button playButton, quitButton;

    public PauseOverlay(int x, int y, int width, int height) {
        super(x, y, width, height);
        loadButtons();
        loadBackground();
    }

    @Override
    public void update() {
        playButton.update();
        quitButton.update();
    }

    @Override
    public void render(Canvas canvas) {
        renderBackground(canvas);
        renderButtons(canvas);
    }

    private void renderBackground(Canvas canvas) {
        canvas.setTransparency(0.7f);
        canvas.renderImage(background, x, y, width, height);
        canvas.renderRectangleBorder(x, y, width, height, 5, Color.WHITE);
        canvas.resetTransparency();
    }

    private void renderButtons(Canvas canvas) {
        playButton.render(canvas);
        quitButton.render(canvas);
    }

    private void loadButtons() {
        this.playButton = new Button(x + 45, 220,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/playButton"),
                () -> {
                    OverlayManager.getInstance().pop();
                });
        this.quitButton = new Button(x + 45, 300,
                ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/quitButton"),
                () -> {
                    GameModeManager.getInstance().getCurrentGameMode().reset();
                    StateManager.getInstance().setState(GameStateType.MENU);
                    OverlayManager.getInstance().pop();
                    System.out.println("Overlay logic is over!");
                });
    }

    private void loadBackground() {
        this.background = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "pause");
    }
}
