package pewpew.smash.game.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import pewpew.smash.engine.Canvas;
import pewpew.smash.engine.GameTime;
import pewpew.smash.game.GameManager;
import pewpew.smash.game.audio.AudioClip;
import pewpew.smash.game.audio.AudioPlayer;
import pewpew.smash.game.audio.AudioPlayer.SoundType;
import pewpew.smash.game.constants.Constants;
import pewpew.smash.game.network.User;
import pewpew.smash.game.overlay.*;
import pewpew.smash.game.ui.Button;
import pewpew.smash.game.utils.FontFactory;
import pewpew.smash.game.utils.ResourcesLoader;

public class Menu implements State {

    private OverlayManager overlayManager;
    private Button[] buttons;
    private BufferedImage background;
    private Button connectButton;
    private Button accountButton;

    private boolean isUserConnected = false;

    public Menu() {
        init();
    }

    @Override
    public void update() {
        if (overlayManager.hasActiveOverlays()) {
            overlayManager.update();
            return;
        }
        updateConnectOrAccountButton();
        updateButtons();
    }

    @Override
    public void render(Canvas canvas) {
        if (overlayManager.hasActiveOverlays()) {
            overlayManager.render(canvas);
        } else {
            renderBackground(canvas);
            renderButtons(canvas);
            renderBanner(canvas);
        }

        canvas.renderString("FPS: " + GameTime.getCurrentFps(), 10, 20, Color.WHITE);
    }

    @Override
    public void handleKeyPress(KeyEvent e) {
        if (!overlayManager.handleKeyPress(e)) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                GameManager.getInstance().conclude();
            }
        }
    }

    @Override
    public void handleKeyRelease(KeyEvent e) {
        overlayManager.handleKeyRelease(e);
    }

    private void init() {
        overlayManager = OverlayManager.getInstance();
        buttons = new Button[6];
        background = ResourcesLoader.getImage(ResourcesLoader.BACKGROUND_PATH, "menu");
        AudioPlayer.getInstance().play(AudioClip.MAIN_THEME, 0.75f, true,
                SoundType.MUSIC);
        loadButtons();
    }

    private void loadButtons() {
        buttons[0] = createButton(Constants.LEFT_PADDING, Constants.PLAY_BUTTON_Y, "playButton",
                () -> overlayManager.push(OverlayFactory.getOverlay(OverlayType.PLAY)));
        connectButton = createButton(Constants.LEFT_PADDING, Constants.CONNECT_BUTTON_Y, "connectButton",
                () -> overlayManager.push(OverlayFactory.getOverlay(OverlayType.CONNECTION)));
        buttons[1] = connectButton;
        accountButton = createButton(Constants.LEFT_PADDING, Constants.CONNECT_BUTTON_Y, "accountButton",
                () -> overlayManager.push(OverlayFactory.getOverlay(OverlayType.ACCOUNT)));
        buttons[2] = createButton(Constants.LEFT_PADDING, Constants.SETTINGS_BUTTON_Y, "optionsButton",
                () -> overlayManager.push(OverlayFactory.getOverlay(OverlayType.OPTIONS)));
        buttons[3] = createButton(Constants.LEFT_PADDING, Constants.CREDITS_BUTTON_Y, "aboutButton",
                () -> overlayManager.push(OverlayFactory.getOverlay(OverlayType.ABOUT)));
        buttons[4] = createButton(Constants.LEFT_PADDING, Constants.QUIT_BUTTON_Y, "quitButton",
                () -> GameManager.getInstance().conclude());
    }

    private Button createButton(int x, int y, String imageName, Runnable onClick) {
        return new Button(x, y, ResourcesLoader.getImage(ResourcesLoader.UI_PATH, "buttons/" + imageName), onClick);
    }

    private void updateButtons() {
        for (Button button : buttons) {
            if (button != null) {
                button.update();
            }
        }
    }

    private void renderButtons(Canvas canvas) {
        for (Button button : buttons) {
            if (button != null) {
                button.render(canvas);
            }
        }
    }

    private void renderBackground(Canvas canvas) {
        canvas.renderImage(background, 0, 0, 800, 600);
    }

    private void renderBanner(Canvas canvas) {
        int bannerWidth = 200;
        int bannerHeight = 50;

        int x = 800 - bannerWidth - 20;
        int y = 20;

        renderBannerBackground(canvas, x, y, bannerWidth, bannerHeight);
        renderBannerContent(canvas, x, y, bannerWidth, bannerHeight);
    }

    private void renderBannerBackground(Canvas canvas, int x, int y, int bannerWidth, int bannerHeight) {
        canvas.renderRectangle(x - 3, y - 3, bannerWidth + 6, bannerHeight + 6, Color.WHITE);
        canvas.renderRectangle(x, y, bannerWidth, bannerHeight, new Color(128, 128, 0));
    }

    private void renderBannerContent(Canvas canvas, int x, int y, int bannerWidth, int bannerHeight) {
        canvas.setFont(new Font("Impact", Font.PLAIN, 22));
        canvas.renderImage(User.getInstance().getRank().getImage(), x, y + bannerHeight / 2 - 25, 50, 50);
        canvas.renderString(User.getInstance().getUsername(), x + 60, y + bannerHeight / 2 + 7, Color.WHITE);
        FontFactory.resetFont(canvas);
    }

    private void updateConnectOrAccountButton() {
        boolean isUserCurrentlyConnected = User.getInstance().isConnected();
        if (isUserCurrentlyConnected != isUserConnected) {
            isUserConnected = isUserCurrentlyConnected;
            buttons[1] = isUserCurrentlyConnected ? accountButton : connectButton;
        }
    }
}